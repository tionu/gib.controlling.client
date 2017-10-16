package gib.controlling.client.setup;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class AppProperties {

	public static final Path APP_PATH = Paths.get("KlimaData/Klima.exe");

	public static final Path USER_SETTINGS_FILENAME = Paths.get("user.json");

	public static final Path UPLOAD_LOG_FILENAME = Paths.get("lastUpload.json");

	public static final int POLLING_INTERVALL_LEVEL_CHANGE = 10;

	public static final String ZOHO_AUTH_TOKEN = "";

	public static List<Path> filePaths = new ArrayList<Path>();

	static {
		filePaths.add(Paths.get("ent1.txt"));
		filePaths.add(Paths.get("ent1.xml"));
		filePaths.add(Paths.get("ent2.txt"));
		filePaths.add(Paths.get("ent2.xml"));
		filePaths.add(Paths.get("ENTERG.DAT"));
		filePaths.add(Paths.get("erg0.xml"));
		filePaths.add(Paths.get("erg1.xml"));
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
