package org.motechproject.whp.mtraining.dto;

import java.util.UUID;

/**
 * Object identifying a child element of a contract object using {@link org.motechproject.mtraining.dto.ContentIdentifierDto#contentId}
 * and {@link org.motechproject.mtraining.dto.ContentIdentifierDto#version} fields.
 * Any contract object which has children will have a list of this object.
 * For e.g., a {@link org.motechproject.mtraining.dto.CourseDto} will have a list, each representing a {@link org.motechproject.mtraining.dto.ModuleDto},
 * by fields having values as contentId and version of the module.
 */
public class ContentIdentifierDto {
    private Long contentId;
    private UUID contentId2;
    private Integer version;

    public ContentIdentifierDto() {
    }

    public ContentIdentifierDto(Long contentId, Integer version) {
        this.contentId = contentId;
        this.version = version;
    }
    public ContentIdentifierDto(UUID contentId, Integer version) {
        this.contentId2 = contentId;
        this.version = version;
    }

    public UUID getContentId() {
        return contentId2;
    }

    public Integer getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        ContentIdentifierDto otherDto = (ContentIdentifierDto) other;
        return this.contentId.equals(otherDto.contentId) && this.version.equals(otherDto.version);
    }

    @Override
    public int hashCode() {
        int result = contentId != null ? contentId.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    public boolean hasContentIdAndVersion() {
        return contentId != null && version != null;
    }
}
