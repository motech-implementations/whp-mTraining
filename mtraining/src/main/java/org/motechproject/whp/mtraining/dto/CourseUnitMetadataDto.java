package org.motechproject.whp.mtraining.dto;

import org.joda.time.DateTime;
import org.motechproject.mtraining.domain.CourseUnitState;

/**
 * Generic DTO for mTraining units
 */
public class CourseUnitMetadataDto {

    private long id;
    private String name;
    private String description;
    private CourseUnitState state;
    private String filename;
    private DateTime creationDate;
    private DateTime modificationDate;

    public CourseUnitMetadataDto() {
    }

    public CourseUnitMetadataDto(long id, String name, String description, CourseUnitState state, String filename,
                                 DateTime creationDate, DateTime modificationDate) {
        this(id, name, state, creationDate, modificationDate);
        this.description = description;
        this.filename = filename;
    }

    public CourseUnitMetadataDto(long id, String name, CourseUnitState state, DateTime creationDate, DateTime modificationDate) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CourseUnitState getState() {
        return state;
    }

    public void setState(CourseUnitState state) {
        this.state = state;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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
