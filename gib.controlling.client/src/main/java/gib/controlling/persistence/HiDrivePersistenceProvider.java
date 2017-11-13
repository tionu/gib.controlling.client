package gib.controlling.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

public class HiDrivePersistenceProvider implements PersistenceProvider {

	public static final String USER = "";
	public static final String PASSWORD = "";

	public static final String BASE_PATH = "https://webdav.hidrive.strato.com/users/" + USER + "/";

	private Sardine webdav;
	private Logger log;

	public HiDrivePersistenceProvider() {
		log = Logger.getLogger(HiDrivePersistenceProvider.class.getName());

		webdav = SardineFactory.begin(USER, PASSWORD);

	}

	public void write(Path path, byte[] bytes) throws IOException {
		webdav.put(BASE_PATH + path.toString(), bytes);
		log.debug("write to cloud: " + path.toString().replace('\\', '/'));
	}

	public byte[] read(Path path) throws IOException {
		log.debug("read from cloud: " + path.toString());
		InputStream is = webdav.get(BASE_PATH + path.toString().replace('\\', '/'));
		return IOUtils.toByteArray(is);
	}

	public void delete(Path path) throws IOException {
		webdav.delete(BASE_PATH + path.toString().replace('\\', '/'));
	}

	public boolean exists(Path path) {
		try {
			return webdav.exists(BASE_PATH + path.toString().replace('\\', '/'));
		} catch (IOException e) {
			return false;
		}
	}

}
