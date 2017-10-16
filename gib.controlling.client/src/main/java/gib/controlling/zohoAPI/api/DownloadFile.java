package gib.controlling.zohoAPI.api;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class DownloadFile extends ZohoDocsUtils {

	private static final String FILES_URL = "https://apidocs.zoho.eu/files/v1/content/";

	public DownloadFile(String authToken) {
		this.authToken = authToken;
	}

	public byte[] download(Path path) throws FileNotFoundException {

		if (!existsFile(path)) {
			throw new FileNotFoundException();
		}

		String docId = getDocId(path);

		try {
			URIBuilder uriBuilder = new URIBuilder(FILES_URL + docId);
			uriBuilder.addParameter("authtoken", authToken);
			uriBuilder.addParameter("scope", "docsapi");
			HttpGet docsGet = new HttpGet(uriBuilder.build());
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpResponse response = httpClient.execute(docsGet);
			InputStream is = response.getEntity().getContent();
			return IOUtils.toByteArray(is);
		} catch (Exception ee) {
		}
		return null;
	}

}
