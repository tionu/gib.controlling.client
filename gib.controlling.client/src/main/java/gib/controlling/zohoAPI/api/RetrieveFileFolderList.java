package gib.controlling.zohoAPI.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;

import gib.controlling.zohoAPI.api.mappings.ZohoDirectoryListing;

public class RetrieveFileFolderList {

	private static final String FILES_URL = "https://apidocs.zoho.eu/files/v1/folders/files?scope=docsapi";

	private String authToken;

	public RetrieveFileFolderList(String authToken) {
		this.authToken = authToken;
	}

	public ZohoDirectoryListing getDirectoryListing() {
		return getDirectoryListing("");
	}

	public ZohoDirectoryListing getDirectoryListing(String folderId) {
		BufferedReader reader = null;
		try {
			URIBuilder uriBuilder = new URIBuilder(FILES_URL).addParameter("authtoken", authToken);

			if (folderId != "") {
				uriBuilder.addParameter("folderid", folderId);
			}

			HttpGet docsGet = new HttpGet(uriBuilder.build());

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();

			HttpResponse response = httpClient.execute(docsGet);
			InputStream is = response.getEntity().getContent();
			StringBuilder builder = new StringBuilder();
			reader = new BufferedReader(new InputStreamReader(is), 8192);
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line + "\n");// No I18N
			}
			ZohoDirectoryListing directoyListing = new Gson().fromJson(builder.toString(), ZohoDirectoryListing.class);
			return directoyListing;
		} catch (Exception ee) {
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return null;
	}

}
