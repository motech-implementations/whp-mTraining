package org.motechproject.whp.mtraining.dto;

import org.joda.time.DateTime;
import org.motechproject.mtraining.domain.CourseUnitState;

import java.util.Set;

/**
 * DTO representation for lesson
 */
public class LessonDto extends CourseUnitMetadataDto {

    private Set<Long> parentIds;

    public LessonDto() {
    }

    public LessonDto(Integer id, String name, String description, CourseUnitState state,
                     String filename, DateTime creationDate, DateTime modificationDate, Set<Long> parentIds) {
        super(id, name, description, state, filename, creationDate, modificationDate);
        this.parentIds = parentIds;
    }

    public LessonDto(long id, String name, CourseUnitState state, DateTime creationDate, DateTime modificationDate) {
        super(id, name, state, creationDate, modificationDate);
    }

    public Set<Long> getParentIds() {
        return parentIds;
    }

    public void setParentIds(Set<Long> parentIds) {
        this.parentIds = parentIds;
    }
}
