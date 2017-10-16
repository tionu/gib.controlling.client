package gib.controlling.client;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import gib.controlling.client.mappings.Level;
import gib.controlling.client.setup.AppProperties;
import gib.controlling.persistence.PersistenceProvider;
import gib.controlling.persistence.SettingsPersistence;
import gib.controlling.zohoAPI.ZohoPersistenceProvider;

public class LevelChangeObservable extends Observable implements Runnable {

	private SettingsPersistence settingsPersistence = SettingsPersistence.getInstance();
	private PersistenceProvider cloudPersistence;
	private Logger log;

	public LevelChangeObservable() {
		cloudPersistence = new ZohoPersistenceProvider(AppProperties.ZOHO_AUTH_TOKEN);
		log = Logger.getLogger(LevelChangeObservable.class.getName());
	}

	private void startLevelWatcher() {
		log.debug("start level watcher...");
		while (true) {
			int level = getLevel();
			if (level != settingsPersistence.getLocalSettings().getLevel()) {
				log.info("new level: " + level);
				setChanged();
				notifyObservers(level);
			}

			try {
				TimeUnit.SECONDS.sleep(AppProperties.POLLING_INTERVALL_LEVEL_CHANGE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void run() {
		startLevelWatcher();
	}

	public int getLevel() {
		byte[] levelByteArray = null;
		try {
			levelByteArray = cloudPersistence.read(Paths.get("level.json"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Level level = new Gson().fromJson(new String(levelByteArray), Level.class);
		return level.getLevel();
	}

}
