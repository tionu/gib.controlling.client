package gib.controlling.client.mappings;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlayerRequest {

	@SerializedName("timeStamp")
	@Expose
	private long timestamp;

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(timestamp).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof PlayerRequest) == false) {
			return false;
		}
		PlayerRequest rhs = ((PlayerRequest) other);
		return new EqualsBuilder().append(timestamp, rhs.timestamp).isEquals();
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
