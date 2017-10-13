
package gib.controlling.zohoAPI.api.mappings;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ZohoDirectoryListing {

	@SerializedName("FILES")
	@Expose
	private List<ZohoFile> files = null;
	@SerializedName("FOLDER")
	@Expose
	private List<ZohoFolder> folders = null;

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(folders).append(files).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof ZohoDirectoryListing) == false) {
			return false;
		}
		ZohoDirectoryListing rhs = ((ZohoDirectoryListing) other);
		return new EqualsBuilder().append(folders, rhs.folders).append(files, rhs.files).isEquals();
	}

	public List<ZohoFile> getFiles() {
		return files;
	}

	public List<ZohoFolder> getFolders() {
		return folders;
	}

}
