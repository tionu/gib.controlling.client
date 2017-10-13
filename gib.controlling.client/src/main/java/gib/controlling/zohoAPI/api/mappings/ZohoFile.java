
package gib.controlling.zohoAPI.api.mappings;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ZohoFile {

	@SerializedName("AUTHOR_ID")
	@Expose
	private String authorId;
	@SerializedName("FOLDER_ID")
	@Expose
	private String folderId;
	@SerializedName("SCOPE")
	@Expose
	private Integer scope;
	@SerializedName("AUTHOR")
	@Expose
	private String author;
	@SerializedName("CREATED_TIME_IN_MILLISECONDS")
	@Expose
	private long createdTimeInMilliseconds;
	@SerializedName("FILE_EXTN")
	@Expose
	private String fileExtension;
	@SerializedName("DOCID")
	@Expose
	private String docId;
	@SerializedName("LAST_MODIFIEDBY_AUTHOR_NAME")
	@Expose
	private String lastModifiedByAuthorName;
	@SerializedName("ENCATT_NAME")
	@Expose
	private String encodedAttributeName;
	@SerializedName("IS_FAVOURITE")
	@Expose
	private Boolean isFavourite;
	@SerializedName("ENCHTML_NAME")
	@Expose
	private String encodedHtmlName;
	@SerializedName("LAST_OPENED_TIME")
	@Expose
	private String lastOpenedTime;
	@SerializedName("LAST_OPENED_TIME_IN_MILLISECONDS")
	@Expose
	private long lastOpenedTimeInMilliseconds;
	@SerializedName("IS_LOCKED")
	@Expose
	private Boolean isLocked;
	@SerializedName("AUTHOR_EMAIL")
	@Expose
	private String authorEmail;
	@SerializedName("CREATED_TIME")
	@Expose
	private String createdTime;
	@SerializedName("DOCNAME")
	@Expose
	private String docName;
	@SerializedName("FILETYPE")
	@Expose
	private String fileType;
	@SerializedName("TRIM_DOCNAME")
	@Expose
	private String trimDocName;
	@SerializedName("LAST_MODIFIEDTIME_IN_MILLISECONDS")
	@Expose
	private long lastModifiedTimeInMilliseconds;
	@SerializedName("EXTRA_PROP")
	@Expose
	private String extraProp;
	@SerializedName("LAST_MODIFIEDBY")
	@Expose
	private String lastModifiedBy;
	@SerializedName("ENCURL_NAME")
	@Expose
	private String encodedUrlName;
	@SerializedName("IS_SHARED")
	@Expose
	private Boolean isShared;
	@SerializedName("LAST_MODIFIEDTIME")
	@Expose
	private String lastModifiedTime;
	@SerializedName("SERVICE_TYPE")
	@Expose
	private String serviceType;

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(serviceType).append(docName).append(docId).append(fileExtension)
				.append(encodedAttributeName).append(lastOpenedTime).append(encodedHtmlName).append(scope)
				.append(folderId).append(fileType).append(createdTime).append(lastOpenedTimeInMilliseconds)
				.append(trimDocName).append(author).append(isShared).append(isLocked).append(lastModifiedByAuthorName)
				.append(encodedUrlName).append(extraProp).append(isFavourite).append(createdTimeInMilliseconds)
				.append(authorId).append(lastModifiedBy).append(lastModifiedTime).append(authorEmail)
				.append(lastModifiedTimeInMilliseconds).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof ZohoFile) == false) {
			return false;
		}
		ZohoFile rhs = ((ZohoFile) other);
		return new EqualsBuilder().append(serviceType, rhs.serviceType).append(docName, rhs.docName)
				.append(docId, rhs.docId).append(fileExtension, rhs.fileExtension)
				.append(encodedAttributeName, rhs.encodedAttributeName).append(lastOpenedTime, rhs.lastOpenedTime)
				.append(encodedHtmlName, rhs.encodedHtmlName).append(scope, rhs.scope).append(folderId, rhs.folderId)
				.append(fileType, rhs.fileType).append(createdTime, rhs.createdTime)
				.append(lastOpenedTimeInMilliseconds, rhs.lastOpenedTimeInMilliseconds)
				.append(trimDocName, rhs.trimDocName).append(author, rhs.author).append(isShared, rhs.isShared)
				.append(isLocked, rhs.isLocked).append(lastModifiedByAuthorName, rhs.lastModifiedByAuthorName)
				.append(encodedUrlName, rhs.encodedUrlName).append(extraProp, rhs.extraProp)
				.append(isFavourite, rhs.isFavourite).append(createdTimeInMilliseconds, rhs.createdTimeInMilliseconds)
				.append(authorId, rhs.authorId).append(lastModifiedBy, rhs.lastModifiedBy)
				.append(lastModifiedTime, rhs.lastModifiedTime).append(authorEmail, rhs.authorEmail)
				.append(lastModifiedTimeInMilliseconds, rhs.lastModifiedTimeInMilliseconds).isEquals();
	}

	public String getAuthorId() {
		return authorId;
	}

	public String getFolderId() {
		return folderId;
	}

	public Integer getScope() {
		return scope;
	}

	public String getAuthor() {
		return author;
	}

	public long getCreatedTimeInMilliseconds() {
		return createdTimeInMilliseconds;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public String getDocId() {
		return docId;
	}

	public String getLastModifiedByAuthorName() {
		return lastModifiedByAuthorName;
	}

	public String getEncodedAttributeName() {
		return encodedAttributeName;
	}

	public Boolean isFavourite() {
		return isFavourite;
	}

	public String getEncodedHtmlName() {
		return encodedHtmlName;
	}

	public String getLastOpenedTime() {
		return lastOpenedTime;
	}

	public long getLastOpenedTimeInMilliseconds() {
		return lastOpenedTimeInMilliseconds;
	}

	public Boolean isLocked() {
		return isLocked;
	}

	public String getAuthorEmail() {
		return authorEmail;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public String getDocName() {
		return docName;
	}

	public String getFileType() {
		return fileType;
	}

	public String getTrimDocName() {
		return trimDocName;
	}

	public long getLastModifiedTimeInMilliseconds() {
		return lastModifiedTimeInMilliseconds;
	}

	public String getExtraProp() {
		return extraProp;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public String getEncodedUrlName() {
		return encodedUrlName;
	}

	public Boolean isShared() {
		return isShared;
	}

	public String getLastModifiedTime() {
		return lastModifiedTime;
	}

	public String getServiceType() {
		return serviceType;
	}

}
