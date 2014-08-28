package org.motechproject.whp.mtraining.dto;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.motechproject.mtraining.domain.CourseUnitState;
import org.motechproject.whp.mtraining.util.CustomDateDeserializer;
import org.motechproject.whp.mtraining.util.CustomDateSerializer;

import java.util.UUID;

/**
 * Generic DTO for mTraining units
 */
public class CourseUnitMetadataDto {

    private long id;
    private String name;
    private String description;
    private CourseUnitState state;
    private String externalId;
    private DateTime creationDate;
    private DateTime modificationDate;

    private UUID contentId;

    public CourseUnitMetadataDto() {
    }

    public CourseUnitMetadataDto(long id, String name, String description, CourseUnitState state, String externalId,
                                 DateTime creationDate, DateTime modificationDate) {
        this(id, name, state, creationDate, modificationDate);
        this.description = description;
        this.externalId = externalId;
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

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    public DateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    public DateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(DateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public UUID getContentId() {
        return contentId;
    }

    public void setContentId(UUID contentId) {
        this.contentId = contentId;
    }
}
