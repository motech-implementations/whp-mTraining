package org.motechproject.whp.mtraining.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mtraining.domain.MdsEntity;

/**
 * Contract object representing a CourseConfiguration.
 * + courseName    : Course name
 * + courseDuration : no of days in which the course has to be completed
 * + location : the region for which the course is configured
 */

@Entity
public class CourseConfiguration extends MdsEntity {

    private Long courseId;

    private Integer courseDuration;

    private Location location;

    public CourseConfiguration() {
    }

    public CourseConfiguration(Long courseId, Integer courseDuration, Location location) {
        this.courseId = courseId;
        this.courseDuration = courseDuration;
        this.location = location;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getCourseDuration() {
        return courseDuration;
    }

    public Location getLocation() {
        return location;
    }

    public void setCourseDuration(Integer courseDuration) {
        this.courseDuration = courseDuration;
    }
}
