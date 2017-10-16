package gib.controlling.zohoAPI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.log4j.Logger;

import gib.controlling.persistence.PersistenceProvider;
import gib.controlling.zohoAPI.api.DownloadFile;
import gib.controlling.zohoAPI.api.UploadFile;
import gib.controlling.zohoAPI.api.ZohoDocsUtils;

public class ZohoPersistenceProvider extends ZohoDocsUtils implements PersistenceProvider {

	private Logger log;

	public ZohoPersistenceProvider(String authToken) {
		this.authToken = authToken;
		log = Logger.getLogger(ZohoPersistenceProvider.class.getName());
	}

	public void write(Path path, byte[] bytes) throws IOException {
		log.debug("write to cloud: " + path.toString());
		UploadFile uploadFileApi = new UploadFile(authToken);
		uploadFileApi.upload(path, bytes);
	}

	public byte[] read(Path path) throws FileNotFoundException {
		log.debug("read from cloud: " + path.toString());
		DownloadFile downloadFileApi = new DownloadFile(authToken);
		return downloadFileApi.download(path);
	}

}
