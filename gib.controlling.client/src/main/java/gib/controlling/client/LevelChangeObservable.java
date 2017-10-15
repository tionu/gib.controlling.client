package gib.controlling.client;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import gib.controlling.client.mappings.Level;
import gib.controlling.client.setup.Params;
import gib.controlling.persistence.PersistenceProvider;
import gib.controlling.persistence.SettingsPersistence;
import gib.controlling.zohoAPI.ZohoPersistenceProvider;

public class LevelChangeObservable extends Observable implements Runnable {

	public static final int POLLING_INTERVALL_LEVEL_CHANGE = 10;

	private SettingsPersistence settingsPersistence;
	private PersistenceProvider cloudPersistence;

	public LevelChangeObservable(SettingsPersistence settingsPersistence) {
		this.settingsPersistence = settingsPersistence;
		cloudPersistence = new ZohoPersistenceProvider(Params.ZOHO_AUTH_TOKEN.toString());
	}

	private void startLevelWatcher() {
		while (true) {
			int level = getLevel();
			if (level != settingsPersistence.getLocalSettings().getLevel()) {
				setChanged();
				notifyObservers(level);
			}

			try {
				TimeUnit.SECONDS.sleep(POLLING_INTERVALL_LEVEL_CHANGE);
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
