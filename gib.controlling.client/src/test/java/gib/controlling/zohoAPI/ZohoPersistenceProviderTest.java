package gib.controlling.zohoAPI;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gib.controlling.persistence.PersistenceProvider;

public class ZohoPersistenceProviderTest {

	private static final String AUTH_TOKEN = "xyz";

	private Path testFile1 = Paths.get(
			"0K^eXA!bIi$x$l!TZQ6SIoseHn8RdtD6I7cOrwzSs$n8Y3he48$pUyf4hb8yuR$uRKelFyijci^bu4kFu2LKRLk2T@1iZWkwCXzq^BLbsPJ1xLxli@vwC1IOKPDkf^JX7h^CKUHWyjO7aGm7yDCM17K613Rnj7M17v4^zqmA^ili5jbhxEIhIWKkeZ@T4abFrpM.xyz");
	private Path testFile2 = Paths.get("jdsf9834kljkldfsay0.abc");
	private Path testFolderPath1Level = Paths.get("9XNJqIbY&hYdVzPIOEjHdlIj0IQ564nVG!&Px32p&QC4cqxTQThg&l!YYG");
	private Path testFolderPath3Level = Paths.get(
			"Sc5YnDW7SZY9Orku^sEkyJz!5Fx5jNO3^qaHxhaQbuRv5ucskWLLx4WwMf3HG$GOuRY&p5M@td&kDICyqEyaRu@Z2NYCk0dXr%/j/x7WbOpKknq9ioOk3Ld8v!zdv9UeT^BOr3Ql^xAkc@zR&uta4R46e@8@jGqeVM^c%2Fr&LrFAvkagdIXrB%1aA!RKmXrXA9tCZee");

	private byte[] data1 = "This is a short test text.".getBytes();
	private byte[] data2 = "lorem ipsum...".getBytes();

	PersistenceProvider persistence;

	@Before
	public void setUp() throws Exception {
		persistence = new ZohoPersistenceProvider(AUTH_TOKEN);
	}

	@Test
	public void testNonExistingFileOrFolder() {
		assertFalse(persistence.exists(testFile1));
		assertFalse(persistence.exists(testFolderPath1Level));
	}

	@Test
	public void testWriteAndDeleteFileToRootFolder() {
		try {
			persistence.write(testFile1, data1);
		} catch (IOException e) {
			fail();
		}
		assertTrue(persistence.exists(testFile1));

		try {
			persistence.delete(testFile1);
		} catch (IOException e) {
			fail();
		}
		assertFalse(persistence.exists(testFile1));

	}

	@Test
	public void testNoDuplicateFileOnMultipleWrite() {
		try {
			persistence.write(testFile1, data1);
		} catch (IOException e) {
			fail();
		}
		assertTrue(persistence.exists(testFile1));
		try {
			persistence.write(testFile1, data1);
		} catch (IOException e) {
			fail();
		}
		assertTrue(persistence.exists(testFile1));
		try {
			persistence.write(testFile1, data1);
		} catch (IOException e) {
			fail();
		}
		assertTrue(persistence.exists(testFile1));
		try {
			persistence.delete(testFile1);
		} catch (IOException e) {
			fail();
		}
		assertFalse(persistence.exists(testFile1));

	}

	@Test
	public void testWriteAndDeleteToLevel3SubFolder() {
		try {
			persistence.write(testFolderPath3Level.resolve(testFile1), data1);
		} catch (IOException e) {
			fail();
		}
		assertTrue(persistence.exists(testFolderPath3Level.resolve(testFile1)));

		try {
			persistence.delete(testFolderPath3Level.resolve(testFile1));
		} catch (IOException e) {
			fail();
		}
		assertFalse(persistence.exists(testFolderPath3Level.resolve(testFile1)));

		assertTrue(persistence.exists(testFolderPath3Level));
		try {
			persistence.delete(testFolderPath3Level.getName(0));
		} catch (IOException e) {
			fail();
		}
		assertFalse(persistence.exists(testFolderPath3Level));

	}

	@Test
	public void testWriteAndReadToRootFolder() {
		try {
			persistence.write(testFile1, data1);
		} catch (IOException e) {
			fail();
		}
		assertTrue(persistence.exists(testFile1));

		byte[] dataRead = null;
		try {
			dataRead = persistence.read(testFile1);
		} catch (IOException e1) {
			fail();
		}
		assertArrayEquals(data1, dataRead);

		try {
			persistence.delete(testFile1);
		} catch (IOException e) {
			fail();
		}
		assertFalse(persistence.exists(testFile1));

	}

	@Test
	public void testWriteAndReadToLevel3SubFolder() {
		try {
			persistence.write(testFolderPath3Level.resolve(testFile1), data1);
		} catch (IOException e) {
			fail();
		}
		assertTrue(persistence.exists(testFolderPath3Level.resolve(testFile1)));

		byte[] dataRead = null;
		try {
			dataRead = persistence.read(testFolderPath3Level.resolve(testFile1));
		} catch (IOException e1) {
			fail();
		}
		assertArrayEquals(data1, dataRead);

		try {
			persistence.delete(testFolderPath3Level.resolve(testFile1));
		} catch (IOException e) {
			fail();
		}
		assertFalse(persistence.exists(testFolderPath3Level.resolve(testFile1)));

		assertTrue(persistence.exists(testFolderPath3Level));
		try {
			persistence.delete(testFolderPath3Level.getName(0));
		} catch (IOException e) {
			fail();
		}
		assertFalse(persistence.exists(testFolderPath3Level));
	}

	@Test
	public void testWriteTwoFilesToLevel1SubFolder() {
		try {
			persistence.write(testFolderPath1Level.resolve(testFile1), data1);
		} catch (IOException e) {
			fail();
		}
		try {
			persistence.write(testFolderPath1Level.resolve(testFile2), data2);
		} catch (IOException e) {
			fail();
		}

		assertTrue(persistence.exists(testFolderPath1Level.resolve(testFile1)));
		assertTrue(persistence.exists(testFolderPath1Level.resolve(testFile2)));
		try {
			persistence.delete(testFolderPath1Level.getName(0));
		} catch (IOException e) {
			fail();
		}
		assertFalse(persistence.exists(testFolderPath1Level));

	}

	@Test
	public void testUpdateExistingFile() {
		try {
			persistence.write(testFolderPath1Level.resolve(testFile1), data1);
		} catch (IOException e) {
			fail();
		}

		byte[] dataRead1 = null;
		try {
			dataRead1 = persistence.read(testFolderPath1Level.resolve(testFile1));
		} catch (IOException e1) {
			fail();
		}
		assertArrayEquals(data1, dataRead1);

		try {
			persistence.write(testFolderPath1Level.resolve(testFile1), data2);
		} catch (IOException e) {
			fail();
		}

		byte[] dataRead2 = null;
		try {
			dataRead2 = persistence.read(testFolderPath1Level.resolve(testFile1));
		} catch (IOException e1) {
			fail();
		}
		assertArrayEquals(data2, dataRead2);

		assertTrue(persistence.exists(testFolderPath1Level.resolve(testFile1)));
		try {
			persistence.delete(testFolderPath1Level.getName(0));
		} catch (IOException e) {
			fail();
		}
		assertFalse(persistence.exists(testFolderPath1Level));

	}

	@After
	public void tearDown() throws Exception {
		deleteIfExists(testFile1);
		deleteIfExists(testFile2);
		deleteIfExists(testFolderPath1Level.getName(0));
		deleteIfExists(testFolderPath3Level.getName(0));
	}

	private void deleteIfExists(Path path) throws IOException {
		if (persistence.exists(path)) {
			persistence.delete(path);
		}
	}

}
