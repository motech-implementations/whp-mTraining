package org.motechproject.whp.mtraining.dto;

import org.joda.time.DateTime;
import org.motechproject.mtraining.domain.CourseUnitState;

/**
 * DTO representation for lesson
 */
public class LessonDto extends CourseUnitMetadataDto {

    public LessonDto() {
    }

    public LessonDto(Integer id, String name, String description, CourseUnitState state,
                     String filename, DateTime creationDate, DateTime modificationDate) {
        super(id, name, description, state, filename, creationDate, modificationDate);
    }
}
