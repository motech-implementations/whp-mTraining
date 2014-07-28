package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.whp.mtraining.domain.CourseConfiguration;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.csv.request.CourseConfigurationRequest;
import org.motechproject.whp.mtraining.csv.request.CourseCsvRequest;
import org.motechproject.whp.mtraining.service.CourseConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.valueOf;

@Service
public class CourseImportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseImportService.class);

    @Autowired
    private MTrainingService mTrainingService;

    @Autowired
    private CourseConfigurationService courseConfigurationService;

    public Course importCourse(List<CourseCsvRequest> requests) {
        Map<String, Course> courseMap = formCourses(requests);
        Course course = courseMap.get(requests.get(0).getNodeName());
        return mTrainingService.updateCourse(course);
    }

    public void importCourseConfig(List<CourseConfigurationRequest> requests) {
        for (CourseConfigurationRequest request : requests) {
            CourseConfiguration courseConfiguration = new CourseConfiguration(request.getCourseName(),
                   valueOf(request.getCourseDurationInDays()), new Location(request.getBlock(), request.getDistrict(), request.getState()));
            if (courseConfigurationService.getCourseConfigurationById(courseConfiguration.getId()) == null) {
                courseConfigurationService.createCourseConfiguration(courseConfiguration);
            } else {
                courseConfigurationService.updateCourseConfiguration(courseConfiguration);
            }
        }
    }

    private Map<String, Course> formCourses(List<CourseCsvRequest> requests) {
        Map<String, Course> courseMap = new HashMap<>();
        for (CourseCsvRequest request : requests) {
            Course course = new Course(request.getNodeName(), request.getStatus(), request.getDescription());
            courseMap.put(request.getNodeName(), course);
        }
        return courseMap;
    }
}