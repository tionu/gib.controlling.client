package gib.controlling.client.mappings;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TimeStampLog {

	@SerializedName("timestamps")
	@Expose
	private List<Long> timeStamps;

	public TimeStampLog() {
		timeStamps = new ArrayList<Long>();
	}

	public TimeStampLog(List<Long> timeStamps) {
		this.timeStamps = timeStamps;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(timeStamps).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof TimeStampLog) == false) {
			return false;
		}
		TimeStampLog rhs = ((TimeStampLog) other);
		return new EqualsBuilder().append(timeStamps, rhs.timeStamps).isEquals();
	}

	public List<Long> getTimeStamps() {
		return timeStamps;
	}

	public void setTimeStamps(List<Long> timeStamps) {
		this.timeStamps = timeStamps;
	}

	public void addTimeStamp() {
		timeStamps.add(System.currentTimeMillis());
	}

	public void addTimeStamp(Long timeStamp) {
		timeStamps.add(timeStamp);
	}

}
