package org.motechproject.whp.mtraining.web.domain;

import org.motechproject.whp.mtraining.domain.CourseProgress;
import org.motechproject.whp.mtraining.util.JSONUtil;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_NODE;

public class CourseProgressPostRequest extends IVRRequest {

    private CourseProgress courseProgress;

    public CourseProgressPostRequest() {
    }

    public CourseProgressPostRequest(Long callerId, String uniqueId, String sessionId, CourseProgress courseProgress) {
        super(callerId, sessionId, uniqueId);
        this.courseProgress = courseProgress;
    }


    public List<ValidationError> validate() {
        List<ValidationError> validationErrors = super.validate();
        if (isNotEmpty(validationErrors))
            return validationErrors;
        if (courseProgress == null) {
            validationErrors.add(new ValidationError(MISSING_NODE.getCode(), MISSING_NODE.getMessage().concat(" for: CourseProgress")));
            return validationErrors;
        }
        validationErrors.addAll(courseProgress.validate());
        return validationErrors;
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }

    public CourseProgress getCourseProgress() {
        return courseProgress;
    }
}