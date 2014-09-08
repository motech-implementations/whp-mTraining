package org.motechproject.whp.mtraining.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mtraining.domain.MdsEntity;

@Entity
@JsonIgnoreProperties({"id", "creationDate", "modificationDate", "creator", "owner", "modifiedBy"})
public class CourseProgress extends MdsEntity {

    @Field
    @JsonProperty("bookmark")
    Flag flag;

    @Field
    private String courseStartTime;

    @Field
    private int timeLeftToCompleteCourse;

    @Field
    private String courseStatus;

    @Field
    @JsonIgnore
    private long callerId;

    public CourseProgress() {
    }

    public CourseProgress(String courseStartTime, Flag flag, int timeLeftToCompleteCourse, String courseStatus) {
        this.courseStartTime = courseStartTime;
        this.flag = flag;
        this.timeLeftToCompleteCourse = timeLeftToCompleteCourse;
        this.courseStatus = courseStatus;
    }

    public CourseProgress(long callerId, String courseStartTime, Flag flag, int timeLeftToCompleteCourse, String courseStatus) {
        this.callerId = callerId;
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

    public long getCallerId() {
        return callerId;
    }

    public void setCallerId(long callerId) {
        this.callerId = callerId;
    }
}
