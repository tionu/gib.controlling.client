package gib.controlling.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import gib.controlling.client.mappings.PlayerRequest;
import gib.controlling.client.setup.GameFiles;
import gib.controlling.client.setup.Params;
import gib.controlling.persistence.FileTransfer;
import gib.controlling.persistence.PersistenceProvider;
import gib.controlling.persistence.SettingsPersistence;
import gib.controlling.zohoAPI.ZohoPersistenceProvider;

public class GameSetup {

	private PersistenceProvider cloudPersistence;
	private SettingsPersistence settingsPersistence;

	public GameSetup() {
		cloudPersistence = new ZohoPersistenceProvider(Params.ZOHO_AUTH_TOKEN.toString());
		settingsPersistence = new SettingsPersistence();
	}

	public void createGame() {
		resetLocalGameFiles();
		settingsPersistence.createDefaultLocalSettings();
		setPlayerGroup();
		getNewGameFiles();
	}

	private void resetLocalGameFiles() {
		try {
			for (Path filePath : GameFiles.getFilePaths()) {
				Files.deleteIfExists(filePath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void getNewGameFiles() {
		Path playerFilePath = Paths
				.get("KL_STA" + settingsPersistence.getLocalSettings().getPlayerGroup2Digits() + ".DAT");
		FileTransfer.downloadFile(playerFilePath);
		FileTransfer.downloadFile(Paths.get("SL.DAT"));

	}

	private void setPlayerGroup() {
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

		int counter = 0;
		do {
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
			if (counter > 9) {
				break;
			}
			counter++;
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (!settingsPersistence.validateSettings());
	}

	private PlayerRequest loadPlayerRequest() {
		byte[] lastPlayerByteArray = null;
		try {
			lastPlayerByteArray = cloudPersistence.read(Paths.get("lastPlayerRequest.json"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		PlayerRequest lastPlayerRequest = new Gson().fromJson(new String(lastPlayerByteArray), PlayerRequest.class);
		return lastPlayerRequest;

	}

	private void savePlayerRequest(PlayerRequest playerRequest) {
		String settingsJson = new Gson().toJson(playerRequest);
		try {
			cloudPersistence.write(Paths.get("lastPlayerRequest.json"), settingsJson.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
