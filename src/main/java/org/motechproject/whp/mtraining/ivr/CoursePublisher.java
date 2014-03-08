package org.motechproject.whp.mtraining.ivr;

import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.whp.mtraining.CourseAdmin;
import org.motechproject.whp.mtraining.domain.Course;
import org.motechproject.whp.mtraining.repository.Courses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CoursePublisher {

    private CourseService courseService;
    private IVRGateway ivrGateway;
    private CourseAdmin courseAdmin;
    private Courses courses;

    @Autowired
    public CoursePublisher(CourseService courseService, IVRGateway ivrGateway, CourseAdmin courseAdmin, Courses courses) {
        this.courseService = courseService;
        this.ivrGateway = ivrGateway;
        this.courseAdmin = courseAdmin;
        this.courses = courses;
    }

    public IVRResponse publish(UUID courseId, Integer version) {
        CourseDto course = courseService.getCourse(new ContentIdentifierDto(courseId, version));
        IVRResponse ivrResponse = ivrGateway.postCourse(course);
        if (ivrResponse.isSuccess()) {
            courseAdmin.notifyCoursePublished(courseId.toString());
        }
        if (ivrResponse.hasValidationErrors()) {
            courseAdmin.notifyValidationFailures(courseId.toString(), ivrResponse);
        }
        courses.add(new Course(courseId, version, ivrResponse.isSuccess()));
        return ivrResponse;
    }
}
