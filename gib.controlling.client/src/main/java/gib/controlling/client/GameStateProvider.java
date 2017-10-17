package gib.controlling.client;

import org.apache.log4j.Logger;

import gib.controlling.client.mappings.GameState.State;

public class GameStateProvider {

	private static GameStateProvider instance;

	private State gameState;

	private GameStateProvider() {
	}

	private static GameStateProvider getInstance() {
		if (instance == null) {
			instance = new GameStateProvider();
			instance.gameState = State.OFFLINE;
		}
		return instance;
	}

	public static void setState(State gameState) {
		getInstance().gameState = gameState;
		if (gameState == State.OFFLINE) {
			Logger.getLogger(GameStateProvider.class.getName()).info("offline - please check internet connection.");
		}
	}

	public static State getGameState() {
		return getInstance().gameState;
	}

}