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

    private String courseName;

    private Integer courseDuration;

    private Location location;

    public CourseConfiguration() {
    }

    public CourseConfiguration(String courseName, Integer courseDuration, Location location) {
        this.courseName = courseName;
        this.courseDuration = courseDuration;
        this.location = location;
    }

    public String getCourseName() {
        return courseName;
    }

    public Integer getCourseDuration() {
        return courseDuration;
    }

    public Location getLocation() {
        return location;
    }
}
