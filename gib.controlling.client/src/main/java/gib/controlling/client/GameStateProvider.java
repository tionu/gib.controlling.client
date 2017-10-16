package gib.controlling.client;

import org.apache.log4j.Logger;

import gib.controlling.client.mappings.GameState.State;

public class GameStateProvider {

	private static GameStateProvider instance;

	private State gameState;

	private GameStateProvider() {
	}

	public static void setState(State gameState) {

		if (getInstance().gameState != State.OFFLINE && gameState == State.OFFLINE) {
			GuiAppender guiAppender = GuiAppender.getInstance();
			guiAppender.setExitOnClose(false);
			guiAppender.show();
		} else if (getInstance().gameState == State.OFFLINE && gameState != State.OFFLINE) {
			GuiAppender.getInstance().hide();
		}
		getInstance().gameState = gameState;
		if (gameState == State.OFFLINE) {
			Logger.getLogger(GameStateProvider.class.getName()).info("offline - please check internet connection.");
		}
	}

	private static GameStateProvider getInstance() {
		if (instance == null) {
			instance = new GameStateProvider();
			instance.gameState = State.OFFLINE;
		}
		return instance;
	}

	public static State getGameState() {
		return getInstance().gameState;
	}

}