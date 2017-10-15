package gib.controlling.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import gib.controlling.client.setup.GameFiles;
import gib.controlling.client.setup.Params;
import gib.controlling.persistence.FileTransfer;
import gib.controlling.persistence.PersistenceProvider;
import gib.controlling.persistence.SettingsPersistence;
import gib.controlling.zohoAPI.ZohoPersistenceProvider;

public class Launcher {

	private static PersistenceProvider cloudPersistence = new ZohoPersistenceProvider(
			Params.ZOHO_AUTH_TOKEN.toString());
	private static SettingsPersistence settingsPersistence = SettingsPersistence.getInstance();
	private static LevelChangeObservable levelObservable = new LevelChangeObservable();

	public static void main(String[] args) {

		Path workingDirectory = GameFiles.getWorkingDirectory();
		if (!Files.exists(workingDirectory)) {
			new File(workingDirectory.toUri()).mkdir();
		}

		settingsPersistence.loadLocalSettings();
		if (settingsPersistence.getLocalSettings().getPlayerGroup() == 0) {
			new GameSetup().createGame();
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
			new GameSetup().createGame();
		}

		if (!settingsPersistence.validateSettings()) {
			// TODO handle invalid settings
		}

		updateGameFiles();
		ObserveGame observeGame = ObserveGame.getInstance();
		new Thread(observeGame).start();
		ObserveLevel observeLevel = ObserveLevel.getInstance();
		new Thread(observeLevel).start();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Runtime runTime = Runtime.getRuntime();
				try {
					runTime.exec("taskkill /F /IM " + GameFiles.getAppPath().getFileName().toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		AppControl appControl = new AppControl(GameFiles.getAppPath().toString());
		new Thread(appControl).start();
	}

	private static boolean checkResetGame() {
		return cloudPersistence
				.exists(Paths.get(settingsPersistence.getLocalSettings().getPlayerGroup2Digits() + "_reset.json"));
	}

	private static void updateGameFiles() {
		int level = levelObservable.getLevel();
		boolean levelChanged = level != settingsPersistence.getLocalSettings().getLevel();
		if (!levelChanged) {
			FileTransfer.uploadFile(
					Paths.get("KL_STA" + settingsPersistence.getLocalSettings().getPlayerGroup2Digits() + ".DAT"));
		} else {
			ObserveLevel.getInstance().changeLevel(level);
		}
	}

}
