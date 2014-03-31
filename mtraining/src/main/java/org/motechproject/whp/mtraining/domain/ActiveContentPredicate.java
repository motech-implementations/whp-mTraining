package org.motechproject.whp.mtraining.domain;

import org.apache.commons.collections.Predicate;

public class ActiveContentPredicate implements Predicate {
    @Override
    public boolean evaluate(Object object) {
        CourseContent courseContent = (CourseContent) object;
        return courseContent.isActive();
    }
}
