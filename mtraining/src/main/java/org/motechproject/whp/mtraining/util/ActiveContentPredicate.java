package org.motechproject.whp.mtraining.util;

import org.apache.commons.collections.Predicate;
import org.motechproject.mtraining.domain.CourseUnitState;
import org.motechproject.whp.mtraining.dto.CourseUnitMetadataDto;

public class ActiveContentPredicate implements Predicate {
    @Override
    public boolean evaluate(Object object) {
        CourseUnitMetadataDto courseContent = (CourseUnitMetadataDto) object;
        return courseContent.getState() == CourseUnitState.Active;
    }
}