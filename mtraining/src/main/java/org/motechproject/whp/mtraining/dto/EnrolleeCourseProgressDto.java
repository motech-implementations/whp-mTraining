package org.motechproject.whp.mtraining.dto;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.motechproject.whp.mtraining.constants.CourseStatus;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;

/**
 * Object representing a course progress structure.
 * Has both course status as well as the current bookmark.
 * externalId : Enrollee Id (like a student roll number)
 * bookmarkDto : points to the current bookmark of the enrollee
 * timeLeftToCompleteCourseInHours : hom much time is left for the student to complete the course
 * courseStartTime :when was the course started by the student
 * Expected by {@link org.motechproject.mtraining.service.CourseProgressService} APIs to manage a mTraining {@link org.motechproject.mtraining.domain.EnrolleeCourseProgress}.
 */

public class EnrolleeCourseProgressDto {

    @JsonProperty
    private String externalId;
    @JsonProperty
    private BookmarkDto bookmarkDto;
    @JsonProperty
    private String courseStartTime;
    @JsonProperty
    private int timeLeftToCompleteCourseInHrs;
    @JsonProperty
    private CourseStatus courseStatus;
    private static final Integer DEFAULT_COURSE_DURATION_IN_HOURS = 365;
    private static final Integer HOURS_IN_A_DAY = 24;


    public EnrolleeCourseProgressDto(String externalId, DateTime courseStartTime, BookmarkDto bookmarkDto, CourseStatus courseStatus) {
        this.externalId = externalId;
        this.bookmarkDto = bookmarkDto;
        this.courseStatus = courseStatus;
        this.courseStartTime = courseStartTime == null ? null : courseStartTime.toString();
    }

    public int getTimeLeftToCompleteCourseInHrs() {
        return timeLeftToCompleteCourseInHrs <= 0 ? 0 : timeLeftToCompleteCourseInHrs;
    }

    public String getExternalId() {
        return externalId;
    }

    public BookmarkDto getBookmarkDto() {
        return bookmarkDto;
    }

    public String getCourseStartTime() {
        return courseStartTime;
    }

    public CourseStatus getCourseStatus() {
        return courseStatus;
    }

    public void setBookmarkDto(BookmarkDto bookmarkDto) {
        this.bookmarkDto = bookmarkDto;
    }

    public void setTimeLeftToCompleteCourseInHrs(Integer courseDuration) {
        this.timeLeftToCompleteCourseInHrs = courseDuration * HOURS_IN_A_DAY - getHoursCompleted();
    }

    public void setTimeLeftToCompleteCourse() {
        this.timeLeftToCompleteCourseInHrs = DEFAULT_COURSE_DURATION_IN_HOURS * HOURS_IN_A_DAY - getHoursCompleted();
    }

    public void markComplete() {
        this.courseStatus = CourseStatus.COMPLETED;
    }

    private int getHoursCompleted() {
        DateTime startTime = ISODateTimeUtil.parseWithTimeZoneUTC(this.courseStartTime);
        if (startTime != null) {
            Period period = new Period(startTime, DateTime.now(), PeriodType.hours());
            return period.getHours();
        }
        return 0;
    }

    public ContentIdentifierDto getCourse() {
        return bookmarkDto.getCourse();
    }
}
