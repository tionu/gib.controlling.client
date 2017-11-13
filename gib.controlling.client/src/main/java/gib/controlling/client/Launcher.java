package gib.controlling.client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.swing.JTextArea;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.google.gson.Gson;

import gib.controlling.client.exceptions.CloudConnectionException;
import gib.controlling.client.mappings.GameState.State;
import gib.controlling.client.mappings.TimeStamp;
import gib.controlling.client.mappings.TimeStampLog;
import gib.controlling.client.mappings.UserSettings;
import gib.controlling.client.setup.AppProperties;
import gib.controlling.persistence.FileTransfer;
import gib.controlling.persistence.HiDrivePersistenceProvider;
import gib.controlling.persistence.PersistenceProvider;
import gib.controlling.persistence.SettingsPersistence;

public class Launcher {

	private static PersistenceProvider cloudPersistence = new HiDrivePersistenceProvider();
	private static SettingsPersistence settingsPersistence = SettingsPersistence.getInstance();
	private static LevelChangeObservable levelObservable = new LevelChangeObservable();
	private static Logger log = Logger.getLogger(Launcher.class.getName());

	private static boolean isNewGame = false;
	private static boolean resetLocalData = false;

	public static void main(String[] args) {

		Properties props = new Properties();
		try {
			props.load(Launcher.class.getResourceAsStream("setup/log4j.properties"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		PropertyConfigurator.configure(props);

		GuiAppender guiAppender = GuiAppender.getInstance();
		LogManager.getRootLogger().addAppender(guiAppender);
		log.info("loading...");

		log.info("get game state...");
		GameStateWatcher gameStateProvider = new GameStateWatcher();
		new Thread(gameStateProvider).start();

		while (true) {
			if (GameStateProvider.getGameState() == State.OFFLINE) {
				try {
					TimeUnit.MILLISECONDS.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				break;
			}
		}

		AppProperties.POLLING_INTERVALL_GAME_STATE = AppProperties.POLLING_INTERVALL_GAME_STATE_RUNNING;

		Path workingDirectory = AppProperties.getWorkingDirectory();
		if (!Files.exists(workingDirectory)) {
			log.debug("create working directory: " + workingDirectory.toString());
			new File(workingDirectory.toUri()).mkdir();
		}

		if (!Files.exists(AppProperties.APP_PATH)) {
			URL source = Launcher.class.getResource("setup/play.bin");
			File destination = new File(
					AppProperties.getWorkingDirectory().resolve(AppProperties.APP_PATH.getFileName()).toString());
			try {
				FileUtils.copyURLToFile(source, destination);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		settingsPersistence.loadLocalSettings();

		settingsPersistence.getLocalSettings().setClientVersion(AppProperties.CLIENT_VERSION);
		log.info("client version: " + settingsPersistence.getLocalSettings().getClientVersion());

		if (GameStateProvider.getGameState() == State.OPEN_FOR_NEW_PLAYERS) {
			isNewGame = checkGameSetup(isNewGame);
		}

		if (!settingsPersistence.validateSettings()) {
			if (GameStateProvider.getGameState() != State.OPEN_FOR_NEW_PLAYERS) {
				log.info("no new players allowed.");
			}
			if (GameStateProvider.getGameState() == State.FINISHED) {
				log.info("game finished.");
			}
			if (GameStateProvider.getGameState() == State.OFFLINE) {
				log.info("offline - please check internet connection.");
			}
			log.warn("settings invalid - can't start the game.");

			JTextArea textArea = GuiAppender.getInstance().getTextArea();
			KeyListener resetListener = new KeyListener() {

				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_R) {
						resetLocalData = true;
						backupGameData();
						UserSettings localUserSettings = settingsPersistence.getLocalSettings();
						localUserSettings.setPlayerGroup(0);
						if (GameStateProvider.getGameState() == State.OPEN_FOR_NEW_PLAYERS) {
							isNewGame = checkGameSetup(isNewGame);
							resetLocalData = false;
						}
					}
				}

				private void backupGameData() {
					long backupTimeStamp = System.currentTimeMillis();
					log.info("create backup: " + backupTimeStamp);
					new File(AppProperties.getWorkingDirectory().resolve(Paths.get("" + backupTimeStamp)).toUri())
							.mkdir();

					try {
						for (Path filePath : AppProperties.filePaths) {
							if (Files.exists(AppProperties.getWorkingDirectory().resolve(filePath))) {
								Files.copy(AppProperties.getWorkingDirectory().resolve(filePath),
										AppProperties.getWorkingDirectory()
												.resolve(Paths.get("" + backupTimeStamp + File.separator + filePath)));
							}
						}
						Files.copy(AppProperties.getWorkingDirectory().resolve(AppProperties.USER_SETTINGS_FILENAME),
								AppProperties.getWorkingDirectory().resolve(Paths.get(
										"" + backupTimeStamp + File.separator + AppProperties.USER_SETTINGS_FILENAME)));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

				public void keyTyped(KeyEvent e) {
				}

				public void keyReleased(KeyEvent e) {
				}

			};

			if (GameStateProvider.getGameState() == State.OPEN_FOR_NEW_PLAYERS) {
				log.warn("press [R] to delete current game data and reset to defaults...");
				textArea.addKeyListener(resetListener);
			}

			int counter = 0;
			while (!resetLocalData) {
				counter++;
				try {
					TimeUnit.MILLISECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (counter > 15000) {
					System.exit(1);
				}
			}
			log.info("reset game...");
			textArea.removeKeyListener(resetListener);
			counter = 0;
			while (resetLocalData) {
				counter++;
				try {
					TimeUnit.MILLISECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (counter > 15000) {
					System.exit(1);
				}
			}

		}

		try {
			settingsPersistence.loadCloudSettings();
			if (!settingsPersistence.getLocalSettings().getClientVersion()
					.equals(settingsPersistence.getCloudSettings().getClientVersion())) {
				settingsPersistence.setCloudSettings(settingsPersistence.getLocalSettings());
				settingsPersistence.saveCloudSettings();
			}
		} catch (IOException e) {
			log.debug("could not update cloud settings.");
		}

		updateLogInLog();

		if (!isNewGame && GameStateProvider.getGameState() != State.FINISHED) {
			log.info("update game files...");
			updateGameFiles();
		}

		startApp();

		if (GameStateProvider.getGameState() != State.FINISHED) {
			updateLogOutLog();

			startKeepAliveLog();

			ObserveGame observeGame = ObserveGame.getInstance();
			log.debug("start observing local game...");
			new Thread(observeGame).start();

			log.debug("start observing level change...");
			ObserveLevel observeLevel = ObserveLevel.getInstance();
			new Thread(observeLevel).start();
		}

	}

	private static boolean checkGameSetup(boolean isNewGame) {
		if (checkResetGame()) {
			log.info("reset game...");
			try {
				cloudPersistence.delete(Paths.get(settingsPersistence.getLocalSettings().getPlayerGroup2Digits() + "_"
						+ AppProperties.USER_SETTINGS_FILENAME));
				cloudPersistence.delete(
						Paths.get(settingsPersistence.getLocalSettings().getPlayerGroup2Digits() + "_reset.json"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			new GameSetup().createGame();
			isNewGame = true;
		} else if (settingsPersistence.getLocalSettings().getPlayerGroup() == 0) {
			log.info("create new game...");
			new GameSetup().createGame();
			isNewGame = true;
		}
		return isNewGame;
	}

	private static void startApp() {
		log.info("starting app: " + AppProperties.APP_PATH.getFileName().toString());
		AppControl appControl = new AppControl(AppProperties.APP_PATH.toString());
		new Thread(appControl).start();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Runtime runTime = Runtime.getRuntime();
				try {
					runTime.exec("taskkill /F /IM " + AppProperties.APP_PATH.getFileName().toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static boolean checkResetGame() {
		return cloudPersistence.exists(Paths.get(settingsPersistence.getLocalSettings().getPlayerGroup2Digits() + "_"
				+ AppProperties.RESET_GAME_FILENAME));
	}

	private static void updateGameFiles() {
		int level;
		try {
			level = levelObservable.getLevel();
			boolean levelChanged = level != settingsPersistence.getLocalSettings().getLevel();
			if (!levelChanged) {
				log.info("level unchanged - sync current game state...");
				FileTransfer.uploadFileWithTimeStamp(
						Paths.get("KL_STA" + settingsPersistence.getLocalSettings().getPlayerGroup2Digits() + ".DAT"));
			} else {
				log.info("level changed: " + level + " - download new level data...");
				ObserveLevel.getInstance().changeLevel(level);
			}
		} catch (CloudConnectionException e) {
			log.debug("offline - no cloud connection...");
		}

	}

	private static void updateLogInLog() {
		Thread updateLogInLogThread = new Thread() {
			public void run() {
				String groupId = SettingsPersistence.getInstance().getLocalSettings().getPlayerGroup2Digits();
				byte[] logInLogByteArray = new byte[0];
				try {
					logInLogByteArray = cloudPersistence
							.read(Paths.get(groupId + "_" + AppProperties.LOGIN_LOG_FILENAME));
				} catch (IOException e) {
				}
				TimeStampLog lastLogInLog = new Gson().fromJson(new String(logInLogByteArray), TimeStampLog.class);

				TimeStampLog logInLog;
				if (lastLogInLog != null) {
					logInLog = new TimeStampLog(lastLogInLog.getTimeStamps());
				} else {
					logInLog = new TimeStampLog();
				}
				logInLog.addTimeStamp();
				String timeStampLogJson = new Gson().toJson(logInLog);
				try {
					cloudPersistence.write(Paths.get(groupId + "_" + AppProperties.LOGIN_LOG_FILENAME),
							timeStampLogJson.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		updateLogInLogThread.start();
	}

	private static void updateLogOutLog() {
		Thread updateLogOutLogThread = new Thread() {
			public void run() {
				String groupId = SettingsPersistence.getInstance().getLocalSettings().getPlayerGroup2Digits();
				byte[] logOutLogByteArray = new byte[0];
				try {
					logOutLogByteArray = cloudPersistence
							.read(Paths.get(groupId + "_" + AppProperties.LOGOUT_LOG_FILENAME));
				} catch (IOException e) {
				}
				TimeStampLog lastLogOutLog = new Gson().fromJson(new String(logOutLogByteArray), TimeStampLog.class);

				TimeStampLog logOutLog;
				if (lastLogOutLog != null) {
					logOutLog = new TimeStampLog(lastLogOutLog.getTimeStamps());
				} else {
					logOutLog = new TimeStampLog();
				}

				byte[] lastKeepAliveByteArray = new byte[0];
				try {
					lastKeepAliveByteArray = cloudPersistence
							.read(Paths.get(groupId + "_" + AppProperties.KEEP_ALIVE_LOG_FILENAME));
				} catch (IOException e) {
				}
				TimeStamp lastKeepAlive = new Gson().fromJson(new String(lastKeepAliveByteArray), TimeStamp.class);
				if (lastKeepAlive != null) {
					logOutLog.addTimeStamp(lastKeepAlive.getTimeStamp());
				}
				String timeStampLogJson = new Gson().toJson(logOutLog);
				try {
					cloudPersistence.write(Paths.get(groupId + "_" + AppProperties.LOGOUT_LOG_FILENAME),
							timeStampLogJson.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		};
		updateLogOutLogThread.start();
	}

	private static void startKeepAliveLog() {
		Thread keepAliveThread = new Thread() {
			public void run() {
				while (true) {
					try {
						TimeUnit.SECONDS.sleep(AppProperties.INTERVALL_KEEP_ALIVE_LOG);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					TimeStamp keepAlive = new TimeStamp();
					String timeStampJson = new Gson().toJson(keepAlive);
					String groupId = SettingsPersistence.getInstance().getLocalSettings().getPlayerGroup2Digits();
					try {
						cloudPersistence.write(Paths.get(groupId + "_" + AppProperties.KEEP_ALIVE_LOG_FILENAME),
								timeStampJson.getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		keepAliveThread.start();
	}

}
