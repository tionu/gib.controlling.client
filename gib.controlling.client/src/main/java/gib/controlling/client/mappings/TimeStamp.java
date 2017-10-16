package gib.controlling.client.mappings;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TimeStamp {

	@SerializedName("timestamp")
	@Expose
	private long timeStamp;

	public TimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(timeStamp).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof TimeStamp) == false) {
			return false;
		}
		TimeStamp rhs = ((TimeStamp) other);
		return new EqualsBuilder().append(timeStamp, rhs.timeStamp).isEquals();
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

}
