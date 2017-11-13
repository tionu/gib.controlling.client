package gib.controlling.client;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.apache.log4j.Logger;

import gib.controlling.client.ObserveLevel.State;

public class GameChangeObservable extends Observable implements Runnable, Observer {

	private Path pathToObserve;
	private Set<String> observeFilenames;
	private State levelChangerState;
	private ObserveLevel observeLevel;
	private Logger log;

	public GameChangeObservable(Path pathToObserve) {
		this.pathToObserve = pathToObserve;
		this.levelChangerState = State.IDLE;
		observeFilenames = new HashSet<String>();
		log = Logger.getLogger(GameChangeObservable.class.getName());
	}

	public void observe(String filenameToObserve) {
		observeFilenames.add(filenameToObserve);
	}

	public void stopObserving(String filenameToRemove) {
		observeFilenames.remove(filenameToRemove);
	}

	private void startDirectoryWatcher() {
		log.debug("start directory watcher...");
		WatchService watcher = null;
		WatchKey key;
		try {
			watcher = FileSystems.getDefault().newWatchService();
			key = pathToObserve.register(watcher, ENTRY_MODIFY);
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {

			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				return;
			}

			if (levelChangerState != State.IDLE) {
				log.debug("file changed but level changer state is idle.");
				continue;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();

				if (kind == OVERFLOW) {
					continue;
				}

				@SuppressWarnings("unchecked")
				WatchEvent<Path> ev = (WatchEvent<Path>) event;
				Path filename = ev.context();
				if (observeFilenames.contains(filename.toString())) {
					log.debug("file changed: " + filename.toString());
					setChanged();
					notifyObservers(filename.toString());
				}

			}

			boolean valid = key.reset();
			if (!valid) {
				break;
			}
		}

	}

	public void run() {
		observeLevel = ObserveLevel.getInstance();
		observeLevel.addObserver(this);
		startDirectoryWatcher();
	}

	public void update(Observable o, Object arg) {
		State state = (State) arg;
		log.debug("level changer state: " + state.toString());
		levelChangerState = state;
	}

}
