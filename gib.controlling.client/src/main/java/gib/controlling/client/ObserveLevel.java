package gib.controlling.client;

import java.nio.file.Paths;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import gib.controlling.persistence.FileTransfer;
import gib.controlling.persistence.SettingsPersistence;

public class ObserveLevel extends Observable implements Runnable, Observer {
	private SettingsPersistence settingsPersistence;

	public ObserveLevel(SettingsPersistence settingsPersistence) {
		this.settingsPersistence = settingsPersistence;
	}

	public void update(Observable o, Object arg) {
		setChanged();
		notifyObservers(GameChangeObservable.State.PAUSED);
		FileTransfer.downloadFile(
				Paths.get("KL_STA" + settingsPersistence.getLocalSettings().getPlayerGroup2Digits() + ".DAT"));
		FileTransfer.downloadFile(Paths.get("SL.DAT"));
		settingsPersistence.getLocalSettings().setLevel((Integer) arg);
		settingsPersistence.saveLocalSettings();
		settingsPersistence.setCloudSettings(settingsPersistence.getLocalSettings());
		settingsPersistence.saveCloudSettings();
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		setChanged();
		notifyObservers(GameChangeObservable.State.OBSERVE);
	}

	public void run() {
		LevelChangeObservable levelObserver = new LevelChangeObservable(settingsPersistence);
		levelObserver.addObserver(this);
		new Thread(levelObserver).start();
	}

}
