package gib.controlling.client;

import com.google.gson.Gson;

import gib.controlling.client.mappings.GameStatus;
import gib.controlling.client.mappings.Level;

public class Test {
	public static void main(String[] args) {
		GameStatus status = new GameStatus();
		status.setGameStatus(GameStatus.Status.OPEN_FOR_NEW_PLAYERS);
		System.out.println(new Gson().toJson(status));

		Level level = new Level();
		level.setLevel(4);
		System.out.println(new Gson().toJson(level));

	}

}