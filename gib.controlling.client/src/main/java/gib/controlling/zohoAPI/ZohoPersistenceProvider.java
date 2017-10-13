package gib.controlling.zohoAPI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import gib.controlling.persistence.PersistenceProvider;
import gib.controlling.zohoAPI.api.DeleteFile;
import gib.controlling.zohoAPI.api.DeleteFolder;
import gib.controlling.zohoAPI.api.DownloadFile;
import gib.controlling.zohoAPI.api.UploadFile;
import gib.controlling.zohoAPI.api.ZohoDocsUtils;

public class ZohoPersistenceProvider extends ZohoDocsUtils implements PersistenceProvider {

	public ZohoPersistenceProvider(String authToken) {
		this.authToken = authToken;
	}

	public void write(Path path, byte[] bytes) throws IOException {
		UploadFile uploadFileApi = new UploadFile(authToken);
		uploadFileApi.upload(path, bytes);
	}

	public byte[] read(Path path) throws FileNotFoundException {
		DownloadFile downloadFileApi = new DownloadFile(authToken);
		return downloadFileApi.download(path);
	}

	public void delete(Path path) throws IOException {
		if (existsFolder(path)) {
			deleteFolder(path);
		} else {
			deleteFile(path);
		}
	}

	private void deleteFile(Path path) throws IOException {
		String docId = getDocId(path);
		if (docId == "") {
			throw new FileNotFoundException();
		}
		DeleteFile deleteFileApi = new DeleteFile(authToken);
		deleteFileApi.delete(docId);
	}

	private void deleteFolder(Path path) throws IOException {
		String folderId = getFolderId(path);
		if (folderId == "") {
			throw new FileNotFoundException();
		}
		DeleteFolder deleteFolderApi = new DeleteFolder(authToken);
		deleteFolderApi.delete(folderId);
	}

}
