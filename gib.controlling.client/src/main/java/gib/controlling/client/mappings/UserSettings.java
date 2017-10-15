package gib.controlling.client.mappings;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserSettings {

	@SerializedName("playerUuid")
	@Expose
	private String playerUuid;

	@SerializedName("playerGroup")
	@Expose
	private int playerGroup;

	@SerializedName("level")
	@Expose
	private int level;

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(playerUuid).append(playerGroup).append(level).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof UserSettings) == false) {
			return false;
		}
		UserSettings rhs = ((UserSettings) other);
		return new EqualsBuilder().append(playerUuid, rhs.playerUuid).append(playerGroup, rhs.playerGroup).isEquals();
	}

	public String getPlayerUuid() {
		return playerUuid;
	}

	public void setPlayerUuid(String playerUuid) {
		this.playerUuid = playerUuid;
	}

	public int getPlayerGroup() {
		return playerGroup;
	}

	public String getPlayerGroup2Digits() {
		return String.format("%02d", getPlayerGroup());
	}

	public void setPlayerGroup(int playerGroup) {
		this.playerGroup = playerGroup;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
