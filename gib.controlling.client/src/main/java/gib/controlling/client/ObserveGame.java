package gib.controlling.client;

import java.nio.file.Paths;
import java.util.Observable;
import java.util.Observer;

import gib.controlling.persistence.FileTransfer;
import gib.controlling.persistence.SettingsPersistence;

public class ObserveGame implements Runnable, Observer {
	private static ObserveGame instance;
	
	private SettingsPersistence settingsPersistence = SettingsPersistence.getInstance();
	private long gameChanged;
	private LevelChangeObservable levelObservable;

	private ObserveGame() {
		gameChanged = 0;
		levelObservable = new LevelChangeObservable();
	}

	public static ObserveGame getInstance() {
		if (instance == null) {
			instance = new ObserveGame();
		}
		return instance;
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
			ObserveLevel.getInstance().changeLevel(level);
		}
		gameChanged = System.currentTimeMillis();
	}

}
