package org.motechproject.whp.mtraining.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mtraining.domain.MdsEntity;

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

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}
