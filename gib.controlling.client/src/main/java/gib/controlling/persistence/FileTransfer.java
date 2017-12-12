package gib.controlling.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import gib.controlling.client.mappings.TimeStamp;
import gib.controlling.client.setup.AppProperties;

public class FileTransfer {

	private static PersistenceProvider cloudPersistence = new HiDrivePersistenceProvider();

	private static Logger log = Logger.getLogger(FileTransfer.class.getName());

	public static void downloadFile(Path filePath) {
		log.debug("download file...");
		byte[] fileData = null;
		try {
			fileData = cloudPersistence.read(filePath);
			Files.write(AppProperties.getWorkingDirectory().resolve(filePath), fileData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void downloadFile(Path sourceFilePath, Path destinationFilePath) {
		log.debug("download file...");
		byte[] fileData = null;
		try {
			fileData = cloudPersistence.read(sourceFilePath);
			Files.write(AppProperties.getWorkingDirectory().resolve(destinationFilePath), fileData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void uploadFile(Path filePath) {
		uploadFile(filePath, filePath);
	}

	public static void uploadFile(Path SourceFilePath, Path targetFilePath) {
		log.debug("upload file...");
		byte[] fileData;
		try {
			fileData = Files.readAllBytes(AppProperties.getWorkingDirectory().resolve(SourceFilePath));
			cloudPersistence.write(targetFilePath, fileData);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void uploadFileWithTimeStamp(Path filePath) {
		log.debug("upload file with timestamp...");
		uploadFile(filePath);
		TimeStamp lastUpload = new TimeStamp();
		String timeStampJson = new Gson().toJson(lastUpload);
		String groupId = SettingsPersistence.getInstance().getLocalSettings().getPlayerGroup2Digits();
		try {
			cloudPersistence.write(Paths.get(groupId + "_" + AppProperties.UPLOAD_LOG_FILENAME),
					timeStampJson.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void deleteFile(Path filePath) {
		log.debug("delete file...");
		try {
			cloudPersistence.delete(filePath);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static PersistenceProvider getCloudPersistence() {
		return cloudPersistence;
	}

}
