package org.motechproject.whp.mtraining.dto;

import org.joda.time.DateTime;

public class ContentIdentifierDto extends MdsEntityDto {

    private long unitId;

    private String contentId;

    private long version;

    public ContentIdentifierDto(long id, DateTime creationDate, DateTime modificationDate, long unitId, String contentId, long version) {
        super(id, creationDate, modificationDate);
        this.unitId = unitId;
        this.contentId = contentId;
        this.version = version;
    }

    public long getUnitId() {
        return unitId;
    }

    public void setUnitId(long unitId) {
        this.unitId = unitId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}

