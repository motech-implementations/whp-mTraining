package org.motechproject.whp.mtraining.ivr;

import org.junit.Test;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.whp.mtraining.domain.Course;
import org.motechproject.whp.mtraining.repository.Courses;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CoursePublisherTest {

    @Test
    public void shouldPublishCourseToIVRUrl() {
        UUID courseId = UUID.randomUUID();
        Courses courses = mock(Courses.class);

        CourseService courseService = mock(CourseService.class);
        List<ModuleDto> modules = Collections.emptyList();
        int courseVersion = 2;
        CourseDto course = new CourseDto("CS001", "CS Course", courseVersion, modules);

        when(courseService.getCourse(new ContentIdentifierDto(courseId, courseVersion))).thenReturn(course);

        IVRGateway ivrGateway = mock(IVRGateway.class);
        IVRResponse ivrResponse = new IVRResponse();
        ivrResponse.markSuccess();
        when(ivrGateway.postCourse(any(CourseDto.class))).thenReturn(ivrResponse);

        CoursePublisher coursePublisher = new CoursePublisher(courseService, ivrGateway, courses);
        coursePublisher.publish(courseId, 2);

        verify(ivrGateway).postCourse(course);
        verify(courseService).getCourse(new ContentIdentifierDto(courseId, courseVersion));
        verify(courses).add(new Course(courseId, courseVersion, true));
    }

}
