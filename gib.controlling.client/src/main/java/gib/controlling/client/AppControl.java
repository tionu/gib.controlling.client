package gib.controlling.client;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import gib.controlling.client.setup.AppProperties;
import gib.controlling.persistence.SettingsPersistence;

public class AppControl implements Runnable, Observer {

	public static enum State {
		IDLE, RUNNING, RESTART;
	}

	private String appPath;
	private Process app;
	private State state;
	private Logger log;

	public AppControl(String appPath) {
		this.appPath = appPath;
		state = State.IDLE;
		ObserveLevel.getInstance().addObserver(this);
		app = null;
		log = Logger.getLogger(AppControl.class.getName());
	}

	public void restartApp() {
		log.info("restart app.");
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
		if (!Files.exists(AppProperties.APP_PATH)
				|| !Files.exists(AppProperties.getWorkingDirectory().resolve("SL.DAT"))
				|| !Files.exists(AppProperties.getWorkingDirectory().resolve("KL_STA"
						+ SettingsPersistence.getInstance().getLocalSettings().getPlayerGroup2Digits() + ".DAT"))) {
			log.info("app not ready: " + AppProperties.APP_PATH.toString());
			GuiAppender.getInstance().setLockVisible(true);
			GuiAppender.getInstance().show();
			try {
				TimeUnit.SECONDS.sleep(15);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.exit(1);
		}
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(appPath);
			processBuilder.directory(AppProperties.getWorkingDirectory().toFile());
			app = processBuilder.start();
			GuiAppender.getInstance().setLockVisible(false);
			GuiAppender.getInstance().hide();
			app.waitFor();
			if (state != State.RESTART) {
				log.debug("shutting down...");
				System.exit(0);
			} else {
				state = State.RUNNING;
			}
		} catch (IOException e) {
			log.error("could not start app.");
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
