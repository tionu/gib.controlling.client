package gib.controlling.client.mappings;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GameStatus {

	public enum Status {
		OPEN_FOR_NEW_PLAYERS, GAME_ON, FINISHED;
	}

	@SerializedName("gameStatus")
	@Expose
	private Status gameStatus;

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(gameStatus).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof GameStatus) == false) {
			return false;
		}
		GameStatus rhs = ((GameStatus) other);
		return new EqualsBuilder().append(gameStatus, rhs.gameStatus).isEquals();
	}

	public Status getGameStatus() {
		return gameStatus;
	}

	public void setGameStatus(Status gameStatus) {
		this.gameStatus = gameStatus;
	}

}