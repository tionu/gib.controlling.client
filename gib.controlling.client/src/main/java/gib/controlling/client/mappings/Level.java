package gib.controlling.client.mappings;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Level {

	@SerializedName("level")
	@Expose
	private int level;

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(level).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Level) == false) {
			return false;
		}
		Level rhs = ((Level) other);
		return new EqualsBuilder().append(level, rhs.level).isEquals();
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
