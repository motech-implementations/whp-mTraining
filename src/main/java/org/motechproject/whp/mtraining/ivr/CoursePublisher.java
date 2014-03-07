package org.motechproject.whp.mtraining.ivr;

import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.whp.mtraining.domain.Course;
import org.motechproject.whp.mtraining.repository.Courses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CoursePublisher {

    private CourseService courseService;
    private IVRGateway ivrGateway;
    private Courses courses;

    @Autowired
    public CoursePublisher(CourseService courseService, IVRGateway ivrGateway, Courses courses) {
        this.courseService = courseService;
        this.ivrGateway = ivrGateway;
        this.courses = courses;
    }

    public IVRResponse publish(UUID courseId, Integer version) {
        CourseDto course = courseService.getCourse(new ContentIdentifierDto(courseId, version));
        IVRResponse ivrResponse = ivrGateway.postCourse(course);
        courses.add(new Course(courseId,version,ivrResponse.isSuccess()));
        return ivrResponse;
    }
}
