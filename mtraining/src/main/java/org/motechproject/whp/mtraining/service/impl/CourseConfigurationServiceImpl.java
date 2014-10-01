package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.whp.mtraining.domain.CourseConfiguration;
import org.motechproject.whp.mtraining.repository.CourseConfigurationDataService;
import org.motechproject.whp.mtraining.service.CourseConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("courseConfigurationService")
public class CourseConfigurationServiceImpl implements CourseConfigurationService {

    @Autowired
    private CourseConfigurationDataService courseConfigurationDataService;

    @Override
    public CourseConfiguration createCourseConfiguration(CourseConfiguration courseConfiguration) {
        return courseConfigurationDataService.create(courseConfiguration);
    }

    @Override
    public CourseConfiguration updateCourseConfiguration(CourseConfiguration courseConfiguration) {
        return courseConfigurationDataService.update(courseConfiguration);
    }

    @Override
    public void deleteCourseConfiguration(CourseConfiguration courseConfiguration) {
        courseConfigurationDataService.delete(courseConfiguration);
    }

    @Override
    public List<CourseConfiguration> getAllCourseConfigurations() {
        return courseConfigurationDataService.retrieveAll();
    }

    @Override
    public CourseConfiguration getCourseConfigurationById(long id) {
       return courseConfigurationDataService.findCourseConfigurationById(id);
    }

    @Override
    public CourseConfiguration getCourseConfigurationByCourseId(long id) {
        return courseConfigurationDataService.findCourseConfigurationByCourseId(id);
    }
}
