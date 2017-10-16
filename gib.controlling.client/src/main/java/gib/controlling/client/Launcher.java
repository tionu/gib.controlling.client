package gib.controlling.client;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultCaret;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import gib.controlling.client.setup.AppProperties;
import gib.controlling.persistence.FileTransfer;
import gib.controlling.persistence.PersistenceProvider;
import gib.controlling.persistence.SettingsPersistence;
import gib.controlling.zohoAPI.ZohoPersistenceProvider;

public class Launcher {

	private static PersistenceProvider cloudPersistence = new ZohoPersistenceProvider(AppProperties.ZOHO_AUTH_TOKEN);
	private static SettingsPersistence settingsPersistence = SettingsPersistence.getInstance();
	private static LevelChangeObservable levelObservable = new LevelChangeObservable();
	private static Logger log = Logger.getLogger(Launcher.class.getName());

	public static void main(String[] args) {
		Properties props = new Properties();
		try {
			props.load(Launcher.class.getResourceAsStream("setup/log4j.properties"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		PropertyConfigurator.configure(props);

		JFrame frame = new JFrame();
		GuiAppender guiAppender = new GuiAppender(gui(frame));
		LogManager.getRootLogger().addAppender(guiAppender);
		log.info("loading...");

		Path workingDirectory = AppProperties.getWorkingDirectory();
		if (!Files.exists(workingDirectory)) {
			log.debug("create working directory: " + workingDirectory.toString());
			new File(workingDirectory.toUri()).mkdir();
		}

		if (!Files.exists(AppProperties.APP_PATH)) {
			URL source = Launcher.class.getResource("setup/play.bin");
			File destination = new File(AppProperties.getWorkingDirectory().resolve(AppProperties.APP_PATH.getFileName()).toString());
			try {
				FileUtils.copyURLToFile(source, destination);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		settingsPersistence.loadLocalSettings();

		boolean isNewGame = false;
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

		if (!settingsPersistence.validateSettings()) {
			log.warn("settings invalid - delete \"" + AppProperties.getWorkingDirectory() + "\\"
					+ AppProperties.USER_SETTINGS_FILENAME + "\" to reset game. ");
			while (true) {
				try {
					TimeUnit.DAYS.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		if (!isNewGame) {
			log.info("update game files...");
			updateGameFiles();
		}

		log.info("starting app: " + AppProperties.APP_PATH.getFileName().toString());
		AppControl appControl = new AppControl(AppProperties.APP_PATH.toString());
		new Thread(appControl).start();

		ObserveGame observeGame = ObserveGame.getInstance();
		log.debug("start observing local game...");
		new Thread(observeGame).start();

		log.debug("start observing level change...");
		ObserveLevel observeLevel = ObserveLevel.getInstance();
		new Thread(observeLevel).start();

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

		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		frame.setVisible(false);
	}

	private static boolean checkResetGame() {
		return cloudPersistence
				.exists(Paths.get(settingsPersistence.getLocalSettings().getPlayerGroup2Digits() + "_reset.json"));
	}

	private static void updateGameFiles() {
		int level = levelObservable.getLevel();
		boolean levelChanged = level != settingsPersistence.getLocalSettings().getLevel();
		if (!levelChanged) {
			log.info("level unchanged - sync current game state...");
			FileTransfer.uploadFileWithTimeStamp(
					Paths.get("KL_STA" + settingsPersistence.getLocalSettings().getPlayerGroup2Digits() + ".DAT"));
		} else {
			log.info("level changed: " + level + " - download new level data...");
			ObserveLevel.getInstance().changeLevel(level);
		}
	}

	private static JTextArea gui(JFrame frame) {
		frame.setTitle("KlimaOnline");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		JTextArea textArea = new JTextArea(6, 30);
		textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		textArea.setSelectedTextColor(Color.BLUE);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		panel.add(scrollPane);
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		return textArea;
	}

}
