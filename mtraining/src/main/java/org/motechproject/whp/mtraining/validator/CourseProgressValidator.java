package org.motechproject.whp.mtraining.validator;

import org.motechproject.whp.mtraining.domain.CourseProgress;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.web.domain.ValidationError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.whp.mtraining.constants.CourseStatus.CLOSED;
import static org.motechproject.whp.mtraining.constants.CourseStatus.UNKNOWN;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.*;

public class CourseProgressValidator {

    public static Collection<? extends ValidationError> validate(CourseProgress courseProgress) {
        List<ValidationError> validationErrors = new ArrayList<>();
        if (isBlank(courseProgress.getCourseStatus()) || courseProgress.getCourseStatus().equalsIgnoreCase(UNKNOWN.getValue())) {
            validationErrors.add(new ValidationError(INVALID_COURSE_STATUS));
        }
        if (isBlank(courseProgress.getCourseStartTime())) {
            validationErrors.add(new ValidationError(MISSING_COURSE_START_TIME.getCode(), MISSING_COURSE_START_TIME.getMessage()));
        }
        if (!ISODateTimeUtil.validate(courseProgress.getCourseStartTime())) {
            validationErrors.add(new ValidationError(INVALID_DATE_TIME.getCode(), INVALID_DATE_TIME.getMessage().concat(" for: Course Start Time")));
        }
        if (courseProgress.getFlag() == null) {
            validationErrors.add(new ValidationError(INVALID_FLAG));
            return validationErrors;
        }
        validationErrors.addAll(courseProgress.getFlag().validate());
        return validationErrors;

    }

    public static boolean isCourseClosed(CourseProgress courseProgress) {
        return courseProgress != null && courseProgress.getCourseStatus().equalsIgnoreCase(CLOSED.getValue());
    }

}
