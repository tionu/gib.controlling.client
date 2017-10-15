package gib.controlling.client;

import java.nio.file.Paths;
import java.util.Observable;
import java.util.Observer;

import gib.controlling.persistence.FileTransfer;
import gib.controlling.persistence.SettingsPersistence;

public class ObserveGame implements Runnable, Observer {
	private SettingsPersistence settingsPersistence;
	private long gameChanged;
	private LevelChangeObservable levelObservable;

	public ObserveGame(SettingsPersistence settingsPersistence) {
		this.settingsPersistence = settingsPersistence;
		gameChanged = 0;
		levelObservable = new LevelChangeObservable(this.settingsPersistence);
	}

	public void run() {
		GameChangeObservable fileObserver = new GameChangeObservable(Paths.get(""));
		fileObserver.observe("KL_STA" + settingsPersistence.getLocalSettings().getPlayerGroup2Digits() + ".DAT");
		fileObserver.addObserver(this);
		new Thread(fileObserver).start();
	}

	public void update(Observable o, Object arg) {
		if ((System.currentTimeMillis() - gameChanged) < 1000) {
			return;
		}
		int level = levelObservable.getLevel();
		boolean levelChanged = level != settingsPersistence.getLocalSettings().getLevel();
		if (!levelChanged) {
			FileTransfer.uploadFile(
					Paths.get("KL_STA" + settingsPersistence.getLocalSettings().getPlayerGroup2Digits() + ".DAT"));
		} else {
			new ObserveLevel(settingsPersistence).changeLevel(level);
		}
		gameChanged = System.currentTimeMillis();
	}

}
