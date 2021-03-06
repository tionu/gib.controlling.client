package gib.controlling.client.setup;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class AppProperties {

	public static final String CLIENT_VERSION = "1.3";

	public static Path APP_PATH = Paths.get(System.getenv("APPDATA"), "/KlimaData/Klima.exe");

	public static Path APP_PATH_STRATEG = Paths.get(System.getenv("APPDATA"), "/KlimaStrateg/Strateg.exe");

	public static final Path USER_SETTINGS_FILENAME = Paths.get("user.json");

	public static final Path UPLOAD_LOG_FILENAME = Paths.get("last_upload.json");

	public static final Path KEEP_ALIVE_LOG_FILENAME = Paths.get("keep_alive.json");

	public static final Path LOGIN_LOG_FILENAME = Paths.get("login.json");

	public static final Path LOGOUT_LOG_FILENAME = Paths.get("logout.json");

	public static final Path GAME_STATE_FILENAME = Paths.get("game_state.json");

	public static final Path LEVEL_FILENAME = Paths.get("level.json");

	public static final Path LAST_PLAYER_REQUEST_FILENAME = Paths.get("lastPlayerRequest.json");

	public static final Path RESET_GAME_FILENAME = Paths.get("reset.json");

	public static final Path NEW_GAME_FILES_CLOUD_PATH = Paths.get("new_game/");

	public static final int POLLING_INTERVALL_LEVEL_CHANGE = 30;

	public static int POLLING_INTERVALL_GAME_STATE = 10;

	public static final int POLLING_INTERVALL_GAME_STATE_RUNNING = 60;

	public static final int INTERVALL_KEEP_ALIVE_LOG = 15;

	public static final String DATE_FORMAT = "dd.MM. HH:mm";

	public static List<Path> filePaths = new ArrayList<Path>();

	static {
		filePaths.add(Paths.get("ent1.txt"));
		filePaths.add(Paths.get("ent2.txt"));
		filePaths.add(Paths.get("ent3.txt"));
		filePaths.add(Paths.get("ent4.txt"));
		filePaths.add(Paths.get("ent5.txt"));
		filePaths.add(Paths.get("ent6.txt"));
		filePaths.add(Paths.get("ent7.txt"));
		filePaths.add(Paths.get("ent8.txt"));
		filePaths.add(Paths.get("ent9.txt"));
		filePaths.add(Paths.get("ent10.txt"));
		filePaths.add(Paths.get("ent1.xml"));
		filePaths.add(Paths.get("ent2.xml"));
		filePaths.add(Paths.get("ent3.xml"));
		filePaths.add(Paths.get("ent4.xml"));
		filePaths.add(Paths.get("ent5.xml"));
		filePaths.add(Paths.get("ent6.xml"));
		filePaths.add(Paths.get("ent7.xml"));
		filePaths.add(Paths.get("ent8.xml"));
		filePaths.add(Paths.get("ent9.xml"));
		filePaths.add(Paths.get("ent10.xml"));
		filePaths.add(Paths.get("ENTERG.DAT"));
		filePaths.add(Paths.get("erg0.xml"));
		filePaths.add(Paths.get("erg1.xml"));
		filePaths.add(Paths.get("erg2.xml"));
		filePaths.add(Paths.get("erg3.xml"));
		filePaths.add(Paths.get("erg4.xml"));
		filePaths.add(Paths.get("erg5.xml"));
		filePaths.add(Paths.get("erg6.xml"));
		filePaths.add(Paths.get("erg7.xml"));
		filePaths.add(Paths.get("erg8.xml"));
		filePaths.add(Paths.get("erg9.xml"));
		filePaths.add(Paths.get("erg10.xml"));
		filePaths.add(Paths.get("KL_STA01.DAT"));
		filePaths.add(Paths.get("KL_STA02.DAT"));
		filePaths.add(Paths.get("KL_STA03.DAT"));
		filePaths.add(Paths.get("KL_STA04.DAT"));
		filePaths.add(Paths.get("KL_STA05.DAT"));
		filePaths.add(Paths.get("KL_STA06.DAT"));
		filePaths.add(Paths.get("KL_STA07.DAT"));
		filePaths.add(Paths.get("KL_STA08.DAT"));
		filePaths.add(Paths.get("KL_STA09.DAT"));
		filePaths.add(Paths.get("KL_STA10.DAT"));
		filePaths.add(Paths.get("SL.DAT"));
	}

	public static Path getWorkingDirectory() {
		if (APP_PATH.getParent() != null) {
			return APP_PATH.getParent();
		} else {
			return Paths.get("");
		}
	}

}
