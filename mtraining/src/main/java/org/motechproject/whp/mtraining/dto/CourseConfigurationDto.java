package org.motechproject.whp.mtraining.dto;

/**
 * Contract object representing a CourseConfiguration.
 * + courseName    : Course name
 * + courseDuration : no of days in which the course has to be completed
 * + location : the region for which the course is configured
 */

public class CourseConfigurationDto {
    private String courseName;
    private Integer courseDuration;
    private LocationDto location;

    public CourseConfigurationDto() {
    }

    public CourseConfigurationDto(String courseName, Integer courseDuration, LocationDto location) {
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

    public LocationDto getLocation() {
        return location;
    }
}
