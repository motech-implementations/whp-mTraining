package org.motechproject.whp.mtraining.reports;

import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.service.MTrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CourseReporter {

    private final MTrainingService courseService;

    @Autowired
    public CourseReporter(MTrainingService courseService) {
        this.courseService = courseService;
    }

    public void reportCourseAdded(long courseId) {
        Course course = courseService.getCourseById(courseId);
        courseService.createCourse(course);
    }

}
