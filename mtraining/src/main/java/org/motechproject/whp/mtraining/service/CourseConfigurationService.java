package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.domain.CourseConfiguration;

import java.util.List;

public interface CourseConfigurationService {

    CourseConfiguration createCourseConfiguration(CourseConfiguration CourseConfiguration);

    CourseConfiguration updateCourseConfiguration(CourseConfiguration CourseConfiguration);

    void deleteCourseConfiguration(CourseConfiguration CourseConfiguration);

    List<CourseConfiguration> getAllCourseConfigurations();

    CourseConfiguration getCourseConfigurationById(long id);

    CourseConfiguration getCourseConfigurationByCourseName(String courseName);

    CourseConfiguration getCourseConfigurationByCourseId(long id);
}