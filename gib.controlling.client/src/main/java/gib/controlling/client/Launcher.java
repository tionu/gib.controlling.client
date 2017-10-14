package gib.controlling.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import gib.controlling.client.exceptions.InvalidSettingsException;
import gib.controlling.client.mappings.PlayerRequest;
import gib.controlling.persistence.PersistenceProvider;
import gib.controlling.persistence.SettingsPersistence;
import gib.controlling.zohoAPI.ZohoPersistenceProvider;

public class Launcher {

	private static PersistenceProvider cloudPersistence = new ZohoPersistenceProvider(
			Params.ZOHO_AUTH_TOKEN.toString());
	private static SettingsPersistence settingsPersistence = new SettingsPersistence();

	public static void main(String[] args) {

		settingsPersistence.loadLocalSettings();
		if (settingsPersistence.getLocalSettings().getPlayerGroup() == 0) {
			newGame();
		}

		if (checkResetGame()) {
			try {
				cloudPersistence.delete(Paths.get(settingsPersistence.getLocalSettings().getPlayerGroup2Digits() + "_"
						+ SettingsPersistence.SETTINGS_PATH));
				cloudPersistence.delete(
						Paths.get(settingsPersistence.getLocalSettings().getPlayerGroup2Digits() + "_reset.json"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			newGame();
		}

		try {
			validateSettings();
		} catch (InvalidSettingsException e) {
			// TODO handle invalid settings
			e.printStackTrace();
		}

		getGameFiles();

		System.out.println("Game ready");

	}

	private static boolean checkResetGame() {
		return cloudPersistence
				.exists(Paths.get(settingsPersistence.getLocalSettings().getPlayerGroup2Digits() + "_reset.json"));
	}

	private static void validateSettings() throws InvalidSettingsException {
		if (!settingsPersistence.loadCloudSettings().equals(settingsPersistence.getLocalSettings())) {
			throw new InvalidSettingsException();
		}
	}

	private static void newGame() {
		resetLocalGameFiles();

		settingsPersistence.createDefaultLocalSettings();

		setPlayerGroup();
	}

	private static void resetLocalGameFiles() {
		try {
			for (int i = 1; i <= 10; i++) {
				String groupNumber = String.format("%02d", i);
				Files.deleteIfExists(Paths.get("KL_STA" + groupNumber + ".DAT"));
			}
			Files.deleteIfExists(Paths.get("SL.DAT"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void getGameFiles() {
		Path playerFilePath = Paths
				.get("KL_STA" + settingsPersistence.getLocalSettings().getPlayerGroup2Digits() + ".DAT");
		downloadFile(playerFilePath);
		downloadFile(Paths.get("SL.DAT"));

	}

	private static void downloadFile(Path filePath) {
		byte[] fileData = null;
		try {
			fileData = cloudPersistence.read(filePath);
			Files.write(filePath, fileData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void setPlayerGroup() {

		long lastPlayerRequest = loadPlayerRequest().getTimestamp();
		long timestamp = System.currentTimeMillis();

		for (int i = 1; i < 10; i++) {
			if ((timestamp - lastPlayerRequest) < 3000) {
				try {
					TimeUnit.MILLISECONDS.sleep(new Random().nextInt(3000) + 500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				lastPlayerRequest = loadPlayerRequest().getTimestamp();
				timestamp = System.currentTimeMillis();
			} else {
				break;
			}
		}

		PlayerRequest playerRequest = new PlayerRequest();
		playerRequest.setTimestamp(System.currentTimeMillis());
		savePlayerRequest(playerRequest);

		for (int i = 1; i <= 10; i++) {
			String groupNumber = String.format("%02d", i);
			Path groupPath = Paths.get(groupNumber + "_" + SettingsPersistence.SETTINGS_PATH);
			if (!cloudPersistence.exists(groupPath)) {
				settingsPersistence.getLocalSettings().setPlayerGroup(i);
				settingsPersistence.setCloudSettings(settingsPersistence.getLocalSettings());
				settingsPersistence.saveCloudSettings();
				settingsPersistence.saveLocalSettings();
				break;
			}
		}
	}

	private static PlayerRequest loadPlayerRequest() {
		byte[] lastPlayerByteArray = null;
		try {
			lastPlayerByteArray = cloudPersistence.read(Paths.get("lastPlayerRequest.json"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		PlayerRequest lastPlayerRequest = new Gson().fromJson(new String(lastPlayerByteArray), PlayerRequest.class);
		return lastPlayerRequest;

	}

	private static void savePlayerRequest(PlayerRequest playerRequest) {
		String settingsJson = new Gson().toJson(playerRequest);
		try {
			cloudPersistence.write(Paths.get("lastPlayerRequest.json"), settingsJson.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
