package gib.controlling.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import com.google.gson.Gson;

import gib.controlling.client.LevelChangeObservable;
import gib.controlling.client.exceptions.CloudConnectionException;
import gib.controlling.client.mappings.UserSettings;
import gib.controlling.client.setup.AppProperties;
import gib.controlling.zohoAPI.ZohoPersistenceProvider;

public class SettingsPersistence {

	private static SettingsPersistence instance;
	PersistenceProvider cloudPersistence;
	private UserSettings localSettings;
	private UserSettings cloudSettings;

	private SettingsPersistence() {
		cloudPersistence = new ZohoPersistenceProvider(AppProperties.ZOHO_AUTH_TOKEN);
	}

	public static SettingsPersistence getInstance() {
		if (instance == null) {
			instance = new SettingsPersistence();
		}
		return instance;
	}

	public UserSettings loadLocalSettings() {
		if (!Files.exists(AppProperties.getWorkingDirectory().resolve(AppProperties.USER_SETTINGS_FILENAME))) {
			localSettings = createDefaultLocalSettings();
			return localSettings;
		}

		byte[] settingsByteArray = new byte[0];
		try {
			settingsByteArray = Files
					.readAllBytes(AppProperties.getWorkingDirectory().resolve(AppProperties.USER_SETTINGS_FILENAME));
		} catch (IOException e) {
			e.printStackTrace();
		}
		localSettings = new Gson().fromJson(new String(settingsByteArray), UserSettings.class);
		return localSettings;
	}

	public UserSettings loadCloudSettings() throws IOException {
		byte[] settingsByteArray = new byte[0];
		settingsByteArray = cloudPersistence
				.read(Paths.get(localSettings.getPlayerGroup2Digits() + "_" + AppProperties.USER_SETTINGS_FILENAME));
		cloudSettings = new Gson().fromJson(new String(settingsByteArray), UserSettings.class);
		return cloudSettings;
	}

	public UserSettings createDefaultLocalSettings() {
		localSettings = getDefaultSettings();
		saveLocalSettings();
		return localSettings;
	}

	private UserSettings getDefaultSettings() {
		UserSettings defaultSettings = new UserSettings();
		UUID uuid = UUID.randomUUID();
		defaultSettings.setPlayerUuid(uuid.toString());
		int currentLevel;
		try {
			currentLevel = new LevelChangeObservable().getLevel();
			defaultSettings.setLevel(currentLevel);
		} catch (CloudConnectionException e) {
		}
		return defaultSettings;
	}

	public void saveLocalSettings() {
		String settingsJson = new Gson().toJson(localSettings);
		try {
			Files.write(AppProperties.getWorkingDirectory().resolve(AppProperties.USER_SETTINGS_FILENAME),
					settingsJson.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveCloudSettings() {
		String settingsJson = new Gson().toJson(cloudSettings);

		try {
			cloudPersistence.write(
					Paths.get(cloudSettings.getPlayerGroup2Digits() + "_" + AppProperties.USER_SETTINGS_FILENAME),
					settingsJson.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public UserSettings getLocalSettings() {
		return localSettings;
	}

	public void setLocalSettings(UserSettings localSettings) {
		this.localSettings = localSettings;
	}

	public UserSettings getCloudSettings() {
		return cloudSettings;
	}

	public void setCloudSettings(UserSettings cloudSettings) {
		this.cloudSettings = cloudSettings;
	}

	public boolean validateSettings() {
		try {
			if (!loadCloudSettings().equals(getLocalSettings())) {
				return false;
			}
		} catch (IOException e) {
			return false;
		}
		return true;
	}

}
