package gib.controlling.client;

import java.nio.file.Paths;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import gib.controlling.persistence.FileTransfer;
import gib.controlling.persistence.SettingsPersistence;

public class ObserveLevel extends Observable implements Runnable, Observer {

	public static enum State {
		CHANGING, IDLE;
	}

	private static ObserveLevel instance;

	private SettingsPersistence settingsPersistence = SettingsPersistence.getInstance();

	private ObserveLevel() {
	}

	public static ObserveLevel getInstance() {
		if (instance == null) {
			instance = new ObserveLevel();
		}
		return instance;
	}

	public void update(Observable o, Object arg) {
		changeLevel((Integer) arg);
	}

	public void changeLevel(int level) {
		setChanged();
		notifyObservers(State.CHANGING);
		FileTransfer.downloadFile(
				Paths.get("KL_STA" + settingsPersistence.getLocalSettings().getPlayerGroup2Digits() + ".DAT"));
		FileTransfer.downloadFile(Paths.get("SL.DAT"));
		settingsPersistence.getLocalSettings().setLevel(level);
		settingsPersistence.saveLocalSettings();
		settingsPersistence.setCloudSettings(settingsPersistence.getLocalSettings());
		settingsPersistence.saveCloudSettings();
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		setChanged();
		notifyObservers(State.IDLE);
	}

	public void run() {
		LevelChangeObservable levelObserver = new LevelChangeObservable();
		levelObserver.addObserver(this);
		new Thread(levelObserver).start();
	}

}
