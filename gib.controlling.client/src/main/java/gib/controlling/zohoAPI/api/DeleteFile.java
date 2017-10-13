package gib.controlling.zohoAPI.api;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class DeleteFile {

	private static final String DELETE_FILE_URL = "https://apidocs.zoho.eu/files/v1/delete?scope=docsapi";

	private String authToken;

	public DeleteFile(String authToken) {
		this.authToken = authToken;
	}

	public void delete(String docId) throws IOException {
		if (docId == "") {
			throw new IllegalArgumentException();
		}

		URIBuilder uriBuilder;
		try {
			uriBuilder = new URIBuilder(DELETE_FILE_URL).addParameter("authtoken", authToken);
			uriBuilder.addParameter("docid", docId);
			HttpPost docsPost = new HttpPost(uriBuilder.build());
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			httpClient.execute(docsPost);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}

}
