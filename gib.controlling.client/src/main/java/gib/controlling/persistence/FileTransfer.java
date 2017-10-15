package gib.controlling.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import gib.controlling.client.setup.Params;
import gib.controlling.zohoAPI.ZohoPersistenceProvider;

public class FileTransfer {

	private static PersistenceProvider cloudPersistence = new ZohoPersistenceProvider(
			Params.ZOHO_AUTH_TOKEN.toString());

	public static void downloadFile(Path filePath) {
		byte[] fileData = null;
		try {
			fileData = cloudPersistence.read(filePath);
			Files.write(filePath, fileData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void uploadFile(Path filePath) {
		byte[] fileData;
		try {
			fileData = Files.readAllBytes(filePath);
			cloudPersistence.write(filePath, fileData);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
