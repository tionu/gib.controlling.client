package gib.controlling.persistence;

import java.io.IOException;
import java.nio.file.Path;

public interface PersistenceProvider {

	/**
	 * writes data to persistence layer.
	 *
	 * @param path
	 *            target location in persistence layer
	 * @param bytes
	 *            data to write
	 * @throws IOException
	 */
	public void write(Path path, byte[] bytes) throws IOException;

	/**
	 * reads data from persistence layer.
	 *
	 * @param path
	 *            source location in persistence layer
	 * @return data to read
	 * @throws IOException
	 */
	public byte[] read(Path path) throws IOException;

	/**
	 * deletes data from persistence layer.
	 *
	 * @param path
	 *            data location in persistence layer
	 * @throws IOException
	 */
	public void delete(Path path) throws IOException;

	/**
	 * Tests whether a resource exists.
	 *
	 * @param path
	 *            the path to the resource
	 * @return {@code true} if the resource exists; {@code false} if the
	 *         resource does not exist or its existence cannot be determined.
	 */
	public boolean exists(Path path);

}
