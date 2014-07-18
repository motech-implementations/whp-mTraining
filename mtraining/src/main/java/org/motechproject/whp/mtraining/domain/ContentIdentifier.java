package org.motechproject.whp.mtraining.domain;

import java.util.UUID;

/**
 * Object identifying a child element of a content using {@link ContentIdentifier#contentId} and {@link ContentIdentifier#version} fields.
 * Any content object which has children will have a list of this object.
 * For e.g., a {@link org.motechproject.mtraining.domain.Course} will have a list, each representing a {@link Module}, by fields having values as contentId and version of the module.
 */

public class ContentIdentifier {
    private UUID contentId;
    private Integer version;

    public ContentIdentifier() {
    }

    public ContentIdentifier(UUID contentId, Integer version) {
        this.contentId = contentId;
        this.version = version;
    }

    public UUID getContentId() {
        return contentId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void update(UUID contentId, Integer version) {
        this.contentId = contentId;
        this.version = version;
    }

    public boolean hasIdAndVersion() {
        return contentId != null && version != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ContentIdentifier that = (ContentIdentifier) o;

        if (contentId != null ? !contentId.equals(that.getContentId()) : that.contentId != null) {
            return false;
        }
        if (version != null ? !version.equals(that.version) : that.getVersion() != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = contentId != null ? contentId.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
