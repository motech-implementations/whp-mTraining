package org.motechproject.whp.mtraining.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mtraining.domain.MdsEntity;
import org.motechproject.whp.mtraining.constants.CourseStatus;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.web.domain.ValidationError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.whp.mtraining.constants.CourseStatus.UNKNOWN;
import static org.motechproject.whp.mtraining.constants.CourseStatus.enumFor;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_COURSE_STATUS;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_DATE_TIME;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_FLAG;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_COURSE_START_TIME;

@Entity
public class CourseProgress extends MdsEntity {

    @Field
    Flag flag;

    @Field
    private String courseStartTime;

    @Field
    private int timeLeftToCompleteCourse;

    @Field
    private String courseStatus;

    @Field
    private String externalId;

    public CourseProgress() {
    }

    public CourseProgress(String courseStartTime, Flag flag, int timeLeftToCompleteCourse, String courseStatus) {
        this.courseStartTime = courseStartTime;
        this.flag = flag;
        this.timeLeftToCompleteCourse = timeLeftToCompleteCourse;
        this.courseStatus = courseStatus;
    }

    public CourseProgress(String externalId, String courseStartTime, Flag flag, int timeLeftToCompleteCourse, String courseStatus) {
        this.externalId = externalId;
        this.courseStartTime = courseStartTime;
        this.flag = flag;
        this.timeLeftToCompleteCourse = timeLeftToCompleteCourse;
        this.courseStatus = courseStatus;
    }


    public String getCourseStartTime() {
        return courseStartTime;
    }

    public Flag getFlag() {
        return flag;
    }

    public int getTimeLeftToCompleteCourse() {
        return timeLeftToCompleteCourse;
    }

    public String getCourseStatus() {
        return courseStatus;
    }

    public void setFlag(Flag flag) {
        this.flag = flag;
    }

    public void setCourseStartTime(String courseStartTime) {
        this.courseStartTime = courseStartTime;
    }

    public void setTimeLeftToCompleteCourse(int timeLeftToCompleteCourse) {
        this.timeLeftToCompleteCourse = timeLeftToCompleteCourse;
    }

    public void setCourseStatus(String courseStatus) {
        this.courseStatus = courseStatus;
    }

    public Collection<? extends ValidationError> validate() {
        List<ValidationError> validationErrors = new ArrayList<>();
        if (isBlank(courseStatus) || enumFor(courseStatus).equals(UNKNOWN)) {
            validationErrors.add(new ValidationError(INVALID_COURSE_STATUS));
        }
        if (isBlank(courseStartTime)) {
            validationErrors.add(new ValidationError(MISSING_COURSE_START_TIME.getCode(), MISSING_COURSE_START_TIME.getMessage()));
        }
        if (!ISODateTimeUtil.validate(courseStartTime)) {
            validationErrors.add(new ValidationError(INVALID_DATE_TIME.getCode(), INVALID_DATE_TIME.getMessage().concat(" for: Course Start Time")));
        }
        if (flag == null) {
            validationErrors.add(new ValidationError(INVALID_FLAG));
            return validationErrors;
        }
        validationErrors.addAll(flag.validate());
        return validationErrors;

    }

    @JsonIgnore
    public boolean isCourseClosed() {
        return courseStatus != null && courseStatus.equalsIgnoreCase(CourseStatus.CLOSED.value());
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}
