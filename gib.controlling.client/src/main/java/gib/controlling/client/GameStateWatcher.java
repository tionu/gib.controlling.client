package gib.controlling.client;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import gib.controlling.client.mappings.GameState;
import gib.controlling.client.mappings.GameState.State;
import gib.controlling.client.setup.AppProperties;
import gib.controlling.persistence.PersistenceProvider;
import gib.controlling.zohoAPI.ZohoPersistenceProvider;

public class GameStateWatcher implements Runnable {

	private PersistenceProvider cloudPersistence;
	private Logger log;

	public GameStateWatcher() {
		cloudPersistence = new ZohoPersistenceProvider(AppProperties.ZOHO_AUTH_TOKEN);
		log = Logger.getLogger(GameStateWatcher.class.getName());
	}

	public void run() {
		while (GameStateProvider.getGameState() != State.FINISHED) {
			checkGameState();
			try {
				TimeUnit.SECONDS.sleep(AppProperties.POLLING_INTERVALL_GAME_STATE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void checkGameState() {
		byte[] gameStateByteArray = new byte[0];
		try {
			gameStateByteArray = cloudPersistence.read(AppProperties.GAME_STATE_FILENAME);
		} catch (IOException e) {
			log.debug("game state: " + State.OFFLINE.toString() + " because of IOException.");
			GameStateProvider.setState(State.OFFLINE);
			return;
		}
		if (gameStateByteArray == null) {
			log.debug("game state: " + State.OFFLINE.toString() + " because no byte data.");
			GameStateProvider.setState(State.OFFLINE);
			return;
		}
		try {
			GameState gamest = new Gson().fromJson(new String(gameStateByteArray), GameState.class);
			log.debug("game state: " + gamest.getGameState().toString());
			GameStateProvider.setState(gamest.getGameState());
		} catch (Exception e) {
			log.debug("game state: " + State.OFFLINE.toString() + " because incorrect data.");
			GameStateProvider.setState(State.OFFLINE);
			return;
		}
	}

}
