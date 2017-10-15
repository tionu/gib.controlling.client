package gib.controlling.client;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

public class AppControl implements Runnable, Observer {

	public static enum State {
		IDLE, RUNNING, RESTART;
	}

	private String appPath;
	private Process app;
	private State state;

	public AppControl(String appPath) {
		this.appPath = appPath;
		state = State.IDLE;
		ObserveLevel.getInstance().addObserver(this);
		app = null;
	}

	public void restartApp() {
		state = State.RESTART;
		if (app != null) {
			app.destroy();
		}
		for (int i = 1; i < 10; i++) {
			if (!app.isAlive()) {
				break;
			} else {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		startApp();
	}

	public void update(Observable o, Object arg) {
		if ((ObserveLevel.State) arg == ObserveLevel.State.IDLE) {
			restartApp();
		}
	}

	public void run() {
		startApp();
	}

	private void startApp() {
		try {
			app = new ProcessBuilder(appPath).start();
			state = State.RUNNING;
			app.waitFor();
			if (state != State.RESTART) {
				System.exit(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
