package gib.controlling.client;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import gib.controlling.client.setup.GameFiles;

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
		state = State.RUNNING;
		startApp();
	}

	private void startApp() {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(appPath);
			processBuilder.directory(GameFiles.getWorkingDirectory().toFile());
			app = processBuilder.start();
			app.waitFor();
			if (state != State.RESTART) {
				System.exit(0);
			} else {
				state = State.RUNNING;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
