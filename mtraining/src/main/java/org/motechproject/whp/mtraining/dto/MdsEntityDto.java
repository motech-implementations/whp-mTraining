package org.motechproject.whp.mtraining.dto;

import org.joda.time.DateTime;

public class MdsEntityDto {

    private long id;

    private DateTime creationDate;

    private DateTime modificationDate;

    public MdsEntityDto(long id, DateTime creationDate, DateTime modificationDate) {
        this.id = id;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    public DateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(DateTime modificationDate) {
        this.modificationDate = modificationDate;
    }
}
