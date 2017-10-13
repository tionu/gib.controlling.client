package gib.controlling.zohoAPI.api;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class DeleteFolder {

	private static final String DELETE_FOLDERS_URL = "https://apidocs.zoho.eu/files/v1/folders/delete?scope=docsapi";

	private String authToken;

	public DeleteFolder(String authToken) {
		this.authToken = authToken;
	}

	public void delete(String folderId) throws IOException {
		if (folderId == "") {
			throw new IllegalArgumentException();
		}

		URIBuilder uriBuilder;
		try {
			uriBuilder = new URIBuilder(DELETE_FOLDERS_URL).addParameter("authtoken", authToken);
			uriBuilder.addParameter("folderid", folderId);
			HttpPost docsPost = new HttpPost(uriBuilder.build());
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			httpClient.execute(docsPost);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}

}
