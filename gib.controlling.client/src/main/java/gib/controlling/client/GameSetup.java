package gib.controlling.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import gib.controlling.client.mappings.PlayerRequest;
import gib.controlling.client.setup.AppProperties;
import gib.controlling.persistence.FileTransfer;
import gib.controlling.persistence.HiDrivePersistenceProvider;
import gib.controlling.persistence.PersistenceProvider;
import gib.controlling.persistence.SettingsPersistence;

public class GameSetup {

	private PersistenceProvider cloudPersistence;
	private SettingsPersistence settingsPersistence = SettingsPersistence.getInstance();
	private Logger log;

	public GameSetup() {
		cloudPersistence = new HiDrivePersistenceProvider();
		log = Logger.getLogger(GameSetup.class.getName());
	}

	public void createGame() {
		resetLocalGameFiles();
		settingsPersistence.createDefaultLocalSettings();
		setPlayerGroup();
		getNewGameFiles();
	}

	private void resetLocalGameFiles() {
		log.debug("reset local game files...");
		try {
			for (Path filePath : AppProperties.filePaths) {
				Files.deleteIfExists(AppProperties.getWorkingDirectory().resolve(filePath));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void getNewGameFiles() {
		log.info("get new game files...");
		Path playerFilePath = Paths
				.get("KL_STA" + settingsPersistence.getLocalSettings().getPlayerGroup2Digits() + ".DAT");
		FileTransfer.downloadFile(AppProperties.NEW_GAME_FILES_CLOUD_PATH.resolve(playerFilePath), playerFilePath);
		FileTransfer.downloadFile(AppProperties.NEW_GAME_FILES_CLOUD_PATH.resolve(Paths.get("SL.DAT")),
				Paths.get("SL.DAT"));
	}

	private void setPlayerGroup() {
		log.info("get player group...");
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
				Path groupPath = Paths.get(groupNumber + "_" + AppProperties.USER_SETTINGS_FILENAME);
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
		byte[] lastPlayerByteArray = new byte[0];
		try {
			lastPlayerByteArray = cloudPersistence.read(AppProperties.LAST_PLAYER_REQUEST_FILENAME);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PlayerRequest lastPlayerRequest = new Gson().fromJson(new String(lastPlayerByteArray), PlayerRequest.class);
		if (lastPlayerRequest != null) {
			return lastPlayerRequest;
		} else {
			PlayerRequest emptyRequest = new PlayerRequest();
			emptyRequest.setTimestamp(0);
			return emptyRequest;
		}

	}

	private void savePlayerRequest(PlayerRequest playerRequest) {
		String settingsJson = new Gson().toJson(playerRequest);
		try {
			cloudPersistence.write(AppProperties.LAST_PLAYER_REQUEST_FILENAME, settingsJson.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
