package org.motechproject.whp.mtraining.ivr;

import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CoursePublisher {

    private CourseService courseService;
    private IVRGateway ivrGateway;

    @Autowired
    public CoursePublisher(CourseService courseService, IVRGateway ivrGateway) {
        this.courseService = courseService;
        this.ivrGateway = ivrGateway;
    }

    public PublishingResult publish(UUID courseId, Integer version) {
        CourseDto course = courseService.getCourse(new ContentIdentifierDto(courseId, version));
        IVRResponse ivrResponse = ivrGateway.postCourse(course);
        return PublishingResult.SUCCESS;
    }
}
