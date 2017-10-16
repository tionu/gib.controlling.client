package gib.controlling.zohoAPI.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.log4j.Logger;

import gib.controlling.zohoAPI.api.mappings.ZohoDirectoryListing;
import gib.controlling.zohoAPI.api.mappings.ZohoFile;
import gib.controlling.zohoAPI.api.mappings.ZohoFolder;

public abstract class ZohoDocsUtils {

	protected String authToken;
	private Logger log;

	public ZohoDocsUtils() {
		log = Logger.getLogger(ZohoDocsUtils.class.getName());
	}

	protected String getDocId(Path path) {
		RetrieveFileFolderList filesApi = new RetrieveFileFolderList(authToken);
		ZohoDirectoryListing directoryListing;
		int nameCount = path.getNameCount();
		if (nameCount < 1) {
			return "";
		}

		String folderId = "";
		if (nameCount > 1) {
			folderId = getFolderId(path.getParent());
		}

		directoryListing = filesApi.getDirectoryListing(folderId);
		for (ZohoFile file : directoryListing.getFiles()) {
			if (file.getDocName().equals(path.getFileName().toString())) {
				return file.getDocId();
			}
		}

		return "";
	}

	protected String getFolderId(Path path) {
		RetrieveFileFolderList filesApi = new RetrieveFileFolderList(authToken);
		ZohoDirectoryListing directoryListing;
		int nameCount = path.getNameCount();
		if (nameCount < 1) {
			return "";
		}

		String folderId = "";
		int folderLevel = 0;
		while (nameCount > 1) {
			directoryListing = filesApi.getDirectoryListing(folderId);
			boolean folderFound = false;
			for (ZohoFolder folder : directoryListing.getFolders()) {
				if (folder.getFolderName().equals(path.getName(folderLevel).toString())) {
					folderId = folder.getFolderId();
					folderFound = true;
					folderLevel++;
					nameCount--;
					break;
				}
			}
			if (!folderFound) {
				return "";
			}
		}
		directoryListing = filesApi.getDirectoryListing(folderId);

		for (ZohoFolder folder : directoryListing.getFolders()) {
			if (folder.getFolderName().equals(path.getFileName().toString())) {
				return folder.getFolderId();
			}
		}
		return "";
	}

	public boolean exists(Path path) {
		return existsFile(path) || existsFolder(path);
	}

	protected boolean existsFile(Path path) {
		if (getDocId(path) != "") {
			return true;
		}
		return false;
	}

	protected boolean existsFolder(Path path) {
		if (getFolderId(path) != "") {
			return true;
		}
		return false;
	}

	public void delete(Path path) throws IOException {
		log.debug("delete in cloud: " + path.toString());
		if (existsFolder(path)) {
			deleteFolder(path);
		} else {
			deleteFile(path);
		}
	}

	private void deleteFile(Path path) throws IOException {
		log.debug("delete file in cloud: " + path.toString());
		String docId = getDocId(path);
		if (docId == "") {
			throw new FileNotFoundException();
		}
		DeleteFile deleteFileApi = new DeleteFile(authToken);
		deleteFileApi.delete(docId);
	}

	private void deleteFolder(Path path) throws IOException {
		log.debug("delete folder in cloud: " + path.toString());
		String folderId = getFolderId(path);
		if (folderId == "") {
			throw new FileNotFoundException();
		}
		DeleteFolder deleteFolderApi = new DeleteFolder(authToken);
		deleteFolderApi.delete(folderId);
	}

}
