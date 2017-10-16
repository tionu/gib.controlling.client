package gib.controlling.zohoAPI.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

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

public class RevisionFile extends ZohoDocsUtils {

	private static final String REVISION_FILE_URL = "https://apidocs.zoho.eu/files/v1/revision?scope=docsapi";

	public RevisionFile(String authToken) {
		this.authToken = authToken;
	}

	public void revision(Path path, byte[] bytes) throws IOException {
		String docId = getDocId(path);
		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
		entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		entityBuilder.addPart("filename", new StringBody(path.getFileName().toString(), ContentType.TEXT_PLAIN));
		entityBuilder.addPart("content", new ByteArrayBody(bytes, path.getFileName().toString()));
		HttpEntity entity = entityBuilder.build();
		URIBuilder uriBuilder;
		try {
			uriBuilder = new URIBuilder(REVISION_FILE_URL).addParameter("authtoken", authToken);
			uriBuilder.addParameter("docid", docId);
			HttpPost docsPost = new HttpPost(uriBuilder.build());
			docsPost.setEntity(entity);
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			httpClient.execute(docsPost);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

}
