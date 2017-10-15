package gib.controlling.client;

import java.io.IOException;
import java.nio.file.Paths;

import gib.controlling.client.setup.Params;
import gib.controlling.persistence.FileTransfer;
import gib.controlling.persistence.PersistenceProvider;
import gib.controlling.persistence.SettingsPersistence;
import gib.controlling.zohoAPI.ZohoPersistenceProvider;

public class Launcher {

	private static PersistenceProvider cloudPersistence = new ZohoPersistenceProvider(
			Params.ZOHO_AUTH_TOKEN.toString());
	private static SettingsPersistence settingsPersistence = new SettingsPersistence();
	private static LevelChangeObservable levelObservable = new LevelChangeObservable(settingsPersistence);

	public static void main(String[] args) {

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
		ObserveGame observeGame = new ObserveGame(settingsPersistence);
		new Thread(observeGame).start();
		ObserveLevel observeLevel = new ObserveLevel(settingsPersistence);
		new Thread(observeLevel).start();

		System.out.println("Game ready");

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
			new ObserveLevel(settingsPersistence).changeLevel(level);
		}
	}

}
