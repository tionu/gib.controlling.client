package gib.controlling.client.mappings;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GameState {

	public enum State {
		OFFLINE("offline"), OPEN_FOR_NEW_PLAYERS("new players welcome"), GAME_ON("ongoing game"), FINISHED("finished");

		private final String name;

		private State(String s) {
			name = s;
		}

		public String toString() {
			return this.name;
		}
	}

	@SerializedName("gameState")
	@Expose
	private State gameState;

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(gameState).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof GameState) == false) {
			return false;
		}
		GameState rhs = ((GameState) other);
		return new EqualsBuilder().append(gameState, rhs.gameState).isEquals();
	}

	public State getGameState() {
		return gameState;
	}

	public void setGameState(State gameState) {
		this.gameState = gameState;
	}

}