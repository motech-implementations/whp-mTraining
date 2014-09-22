package org.motechproject.whp.mtraining.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mtraining.domain.MdsEntity;

import java.util.UUID;

@Entity
public class ContentIdentifier extends MdsEntity {

    @JsonIgnore
    @Field
    private long unitId;
    @Field
    private String contentId;
    @Field
    private long version;

    public ContentIdentifier() { }

    public ContentIdentifier(long id, String contentId, long version) {
        this.unitId = id;
        this.contentId = contentId;
        this.version = version;
    }

    public ContentIdentifier(long id, UUID contentId, long version) {
        this.unitId = id;
        this.contentId = contentId.toString();
        this.version = version;
    }

    public ContentIdentifier(long id, String contentId) {
        this.unitId = id;
        this.contentId = contentId;
        this.version = 1;
    }

    public ContentIdentifier(String contentId, long version) {
        this.contentId = contentId;
        this.version = version;
    }

    public long getUnitId() {
        return unitId;
    }

    public void setUnitId(long id) {
        this.unitId = id;
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
