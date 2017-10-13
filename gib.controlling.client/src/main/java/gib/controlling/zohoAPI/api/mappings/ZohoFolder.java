
package gib.controlling.zohoAPI.api.mappings;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ZohoFolder {

	@SerializedName("PERMISSION")
	@Expose
	private int permission;
	@SerializedName("LAST_MODIFIED_AUTHOR_NAME")
	@Expose
	private String lastModifiedAuthorName;
	@SerializedName("FOLDERNAME")
	@Expose
	private String folderName;
	@SerializedName("AUTHOR_ID")
	@Expose
	private String authorId;
	@SerializedName("LAST_MODIFIED_TIME")
	@Expose
	private long lastModifiedTimeInMilliseconds;
	@SerializedName("CREATED_TIME")
	@Expose
	private long createdTimeInMilliseconds;
	@SerializedName("FOLDERID")
	@Expose
	private String folderId;
	@SerializedName("SCOPE")
	@Expose
	private int scope;
	@SerializedName("AUTHOR_NAME")
	@Expose
	private String authorName;
	@SerializedName("PARENT_FOLDER_ID")
	@Expose
	private String partentFolderId;
	@SerializedName("LAST_MODIFIEDBY")
	@Expose
	private String lastModifiedBy;
	@SerializedName("IS_SHARED")
	@Expose
	private boolean isShared;
	@SerializedName("IS_FAVOURITE")
	@Expose
	private boolean isFavourite;
	@SerializedName("LAST_OPENED_TIME")
	@Expose
	private long lastOpenedtimeInMilliseconds;

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(isShared).append(lastOpenedtimeInMilliseconds).append(scope)
				.append(folderId).append(createdTimeInMilliseconds).append(lastModifiedAuthorName).append(isFavourite)
				.append(partentFolderId).append(authorId).append(lastModifiedBy).append(folderName)
				.append(lastModifiedTimeInMilliseconds).append(permission).append(authorName).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof ZohoFolder) == false) {
			return false;
		}
		ZohoFolder rhs = ((ZohoFolder) other);
		return new EqualsBuilder().append(isShared, rhs.isShared)
				.append(lastOpenedtimeInMilliseconds, rhs.lastOpenedtimeInMilliseconds).append(scope, rhs.scope)
				.append(folderId, rhs.folderId).append(createdTimeInMilliseconds, rhs.createdTimeInMilliseconds)
				.append(lastModifiedAuthorName, rhs.lastModifiedAuthorName).append(isFavourite, rhs.isFavourite)
				.append(partentFolderId, rhs.partentFolderId).append(authorId, rhs.authorId)
				.append(lastModifiedBy, rhs.lastModifiedBy).append(folderName, rhs.folderName)
				.append(lastModifiedTimeInMilliseconds, rhs.lastModifiedTimeInMilliseconds)
				.append(permission, rhs.permission).append(authorName, rhs.authorName).isEquals();
	}

	public int getPermission() {
		return permission;
	}

	public String getLastModifiedAuthorName() {
		return lastModifiedAuthorName;
	}

	public String getFolderName() {
		return folderName;
	}

	public String getAuthorId() {
		return authorId;
	}

	public long getLastModifiedTimeInMilliseconds() {
		return lastModifiedTimeInMilliseconds;
	}

	public long getCreatedTimeInMilliseconds() {
		return createdTimeInMilliseconds;
	}

	public String getFolderId() {
		return folderId;
	}

	public int getScope() {
		return scope;
	}

	public String getAuthorName() {
		return authorName;
	}

	public String getPartentFolderId() {
		return partentFolderId;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public boolean isShared() {
		return isShared;
	}

	public boolean isFavourite() {
		return isFavourite;
	}

	public long getLastOpenedtimeInMilliseconds() {
		return lastOpenedtimeInMilliseconds;
	}

}
