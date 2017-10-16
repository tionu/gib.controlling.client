package gib.controlling.client;

import java.nio.file.Paths;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import gib.controlling.client.exceptions.CloudConnectionException;
import gib.controlling.client.setup.AppProperties;
import gib.controlling.persistence.FileTransfer;
import gib.controlling.persistence.SettingsPersistence;

public class ObserveGame implements Runnable, Observer {

	private static ObserveGame instance;

	private SettingsPersistence settingsPersistence = SettingsPersistence.getInstance();
	private long gameChanged;
	private LevelChangeObservable levelObservable;
	private Logger log;

	private ObserveGame() {
		gameChanged = 0;
		levelObservable = new LevelChangeObservable();
		log = Logger.getLogger(ObserveGame.class.getName());
	}

	public static ObserveGame getInstance() {
		if (instance == null) {
			instance = new ObserveGame();
		}
		return instance;
	}

	public void run() {
		GameChangeObservable fileObserver = new GameChangeObservable(AppProperties.getWorkingDirectory());
		fileObserver.observe("KL_STA" + settingsPersistence.getLocalSettings().getPlayerGroup2Digits() + ".DAT");
		fileObserver.addObserver(this);
		new Thread(fileObserver).start();
	}

	public void update(Observable o, Object arg) {
		if ((System.currentTimeMillis() - gameChanged) < 1000) {
			return;
		}
		log.info("game state changed. check current level...");
		int level;
		try {
			level = levelObservable.getLevel();
			boolean levelChanged = level != settingsPersistence.getLocalSettings().getLevel();
			if (!levelChanged) {
				log.info("level unchanged - sync current game state...");
				FileTransfer.uploadFileWithTimeStamp(
						Paths.get("KL_STA" + settingsPersistence.getLocalSettings().getPlayerGroup2Digits() + ".DAT"));
			} else {
				log.info("level changed: " + level + " - download new level data...");
				ObserveLevel.getInstance().changeLevel(level);
			}
		} catch (CloudConnectionException e) {
			log.debug("offline - no cloud connection...");
		}

		gameChanged = System.currentTimeMillis();
	}

}
