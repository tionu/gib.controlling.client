package gib.controlling.zohoAPI.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class UploadFile extends ZohoDocsUtils {

	private static final String UPLOAD_FILE_URL = "https://apidocs.zoho.eu/files/v1/upload?scope=docsapi";

	public UploadFile(String authToken) {
		this.authToken = authToken;
	}

	public void upload(Path path, byte[] bytes) throws IOException {

		int nameCount = path.getNameCount();
		if (nameCount < 1) {
			throw new IllegalArgumentException();
		}

		if (existsFile(path)) {
			if (FilenameUtils.getExtension(path.getFileName().toString()).equalsIgnoreCase("json")) {
				update(path, bytes);
			} else {
				delete(path);
				create(path, bytes);
			}
		} else {
			create(path, bytes);
		}
	}

	private void update(Path path, byte[] bytes) throws IOException {
		RevisionFile revisionFileApi = new RevisionFile(authToken);
		revisionFileApi.revision(path, bytes);
	}

	private void create(Path path, byte[] bytes) throws IOException {
		int nameCount = path.getNameCount();
		String folderId = "";
		if (nameCount > 1) {
			folderId = getFolderId(path.getParent());
			if (folderId == "") {
				CreateFolder createFolderApi = new CreateFolder(authToken);
				createFolderApi.create(path.getParent());
				folderId = getFolderId(path.getParent());
			}
		}

		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
		entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		entityBuilder.addPart("filename", new StringBody(path.getFileName().toString(), ContentType.TEXT_PLAIN));
		entityBuilder.addPart("content", new ByteArrayBody(bytes, path.getFileName().toString()));
		HttpEntity entity = entityBuilder.build();
		URIBuilder uriBuilder;
		try {
			uriBuilder = new URIBuilder(UPLOAD_FILE_URL).addParameter("authtoken", authToken);
			if (folderId != "") {
				uriBuilder.addParameter("fid", folderId);
			}
			HttpPost docsPost = new HttpPost(uriBuilder.build());
			docsPost.setEntity(entity);
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			httpClient.execute(docsPost);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

}
