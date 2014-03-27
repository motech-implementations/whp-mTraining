package org.motechproject.whp.mtraining.reports;

import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.whp.mtraining.domain.CertificationCourse;
import org.motechproject.whp.mtraining.repository.AllCertificationCourses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CourseReporter {

    private final CourseService courseService;
    private final AllCertificationCourses allCertificationCourses;

    @Autowired
    public CourseReporter(CourseService courseService, AllCertificationCourses allCertificationCourses) {
        this.courseService = courseService;
        this.allCertificationCourses = allCertificationCourses;
    }

    public void reportCourseAdded(UUID courseId, Integer version) {
        CourseDto course = courseService.getCourse(new ContentIdentifierDto(courseId, version));
        allCertificationCourses.add(new CertificationCourse(course));
    }

}
