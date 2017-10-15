package gib.controlling.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import com.google.gson.Gson;

import gib.controlling.client.mappings.Settings;
import gib.controlling.client.setup.Params;
import gib.controlling.zohoAPI.ZohoPersistenceProvider;

public class SettingsPersistence {

	public static final Path SETTINGS_PATH = Paths.get("settings.json");

	private static SettingsPersistence instance;
	PersistenceProvider cloudPersistence;
	private Settings localSettings;
	private Settings cloudSettings;

	private SettingsPersistence() {
		cloudPersistence = new ZohoPersistenceProvider(Params.ZOHO_AUTH_TOKEN.toString());
	}

	public static SettingsPersistence getInstance() {
		if (instance == null) {
			instance = new SettingsPersistence();
		}
		return instance;
	}

	public Settings loadLocalSettings() {
		if (!Files.exists(SETTINGS_PATH)) {
			localSettings = createDefaultLocalSettings();
			return localSettings;
		}

		byte[] settingsByteArray = new byte[0];
		try {
			settingsByteArray = Files.readAllBytes(SETTINGS_PATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
		localSettings = new Gson().fromJson(new String(settingsByteArray), Settings.class);
		return localSettings;
	}

	public Settings loadCloudSettings() throws IOException {
		byte[] settingsByteArray = null;
		settingsByteArray = cloudPersistence
				.read(Paths.get(localSettings.getPlayerGroup2Digits() + "_" + SETTINGS_PATH));
		cloudSettings = new Gson().fromJson(new String(settingsByteArray), Settings.class);
		return cloudSettings;
	}

	public Settings createDefaultLocalSettings() {
		localSettings = getDefaultSettings();
		saveLocalSettings();
		return localSettings;
	}

	private Settings getDefaultSettings() {
		Settings defaultSettings = new Settings();
		UUID uuid = UUID.randomUUID();
		defaultSettings.setPlayerUuid(uuid.toString());
		defaultSettings.setLevel(1);
		return defaultSettings;
	}

	public void saveLocalSettings() {
		String settingsJson = new Gson().toJson(localSettings);
		try {
			Files.write(SETTINGS_PATH, settingsJson.getBytes());
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

	public Settings getLocalSettings() {
		return localSettings;
	}

	public void setLocalSettings(Settings localSettings) {
		this.localSettings = localSettings;
	}

	public Settings getCloudSettings() {
		return cloudSettings;
	}

	public void setCloudSettings(Settings cloudSettings) {
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
