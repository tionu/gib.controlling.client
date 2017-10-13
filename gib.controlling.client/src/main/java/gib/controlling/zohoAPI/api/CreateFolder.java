package gib.controlling.zohoAPI.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class CreateFolder extends ZohoDocsUtils {

	private static final String CREATE_FOLDERS_URL = "https://apidocs.zoho.eu/files/v1/folders/create?scope=docsapi";

	public CreateFolder(String authToken) {
		this.authToken = authToken;
	}

	public void create(Path path) throws IOException {
		int nameCount = path.getNameCount();
		if (nameCount < 1) {
			throw new IllegalArgumentException();
		}

		int folderLevel = 0;
		Path partialPath = Paths.get("");
		while (nameCount > 0) {
			partialPath = partialPath.resolve(path.getName(folderLevel));
			if (!existsFolder(partialPath)) {
				String folderName = partialPath.getName(partialPath.getNameCount() - 1).toString();
				String folderId = partialPath.getParent() != null ? getFolderId(partialPath.getParent()) : "";
				createSubFolder(folderName, folderId);
			}
			nameCount--;
			folderLevel++;
		}
	}

	private void createSubFolder(String folderName, String folderId) throws ClientProtocolException, IOException {
		URIBuilder uriBuilder;
		try {
			uriBuilder = new URIBuilder(CREATE_FOLDERS_URL).addParameter("authtoken", authToken);
			uriBuilder.addParameter("foldername", folderName);
			if (folderId != "") {
				uriBuilder.addParameter("parentfolderid", folderId);
			}
			HttpPost docsPost = new HttpPost(uriBuilder.build());
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			httpClient.execute(docsPost);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

}
