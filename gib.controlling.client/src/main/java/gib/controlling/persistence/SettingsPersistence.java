package gib.controlling.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import com.google.gson.Gson;

import gib.controlling.client.mappings.UserSettings;
import gib.controlling.client.setup.GameFiles;
import gib.controlling.client.setup.Params;
import gib.controlling.zohoAPI.ZohoPersistenceProvider;

public class SettingsPersistence {

	public static final Path SETTINGS_PATH = Paths.get("user.json");

	private static SettingsPersistence instance;
	PersistenceProvider cloudPersistence;
	private UserSettings localSettings;
	private UserSettings cloudSettings;

	private SettingsPersistence() {
		cloudPersistence = new ZohoPersistenceProvider(Params.ZOHO_AUTH_TOKEN.toString());
	}

	public static SettingsPersistence getInstance() {
		if (instance == null) {
			instance = new SettingsPersistence();
		}
		return instance;
	}

	public UserSettings loadLocalSettings() {
		if (!Files.exists(GameFiles.getWorkingDirectory().resolve(SETTINGS_PATH))) {
			localSettings = createDefaultLocalSettings();
			return localSettings;
		}

		byte[] settingsByteArray = new byte[0];
		try {
			settingsByteArray = Files.readAllBytes(GameFiles.getWorkingDirectory().resolve(SETTINGS_PATH));
		} catch (IOException e) {
			e.printStackTrace();
		}
		localSettings = new Gson().fromJson(new String(settingsByteArray), UserSettings.class);
		return localSettings;
	}

	public UserSettings loadCloudSettings() throws IOException {
		byte[] settingsByteArray = null;
		settingsByteArray = cloudPersistence
				.read(Paths.get(localSettings.getPlayerGroup2Digits() + "_" + SETTINGS_PATH));
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
		defaultSettings.setLevel(1);
		return defaultSettings;
	}

	public void saveLocalSettings() {
		String settingsJson = new Gson().toJson(localSettings);
		try {
			Files.write(GameFiles.getWorkingDirectory().resolve(SETTINGS_PATH), settingsJson.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveCloudSettings() {
		String settingsJson = new Gson().toJson(cloudSettings);

		try {
			cloudPersistence.write(Paths.get(cloudSettings.getPlayerGroup2Digits() + "_" + SETTINGS_PATH),
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
