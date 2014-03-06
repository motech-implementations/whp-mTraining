package org.motechproject.whp.mtraining.ivr;

import org.junit.Test;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.service.CourseService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CoursePublisherTest {

    @Test
    public void shouldPublishCourseToIVRUrl() {
        UUID courseId = UUID.randomUUID();

        CourseService courseService = mock(CourseService.class);
        IVRGateway ivrGateway = mock(IVRGateway.class);

        List<ModuleDto> modules = Collections.emptyList();
        CourseDto course = new CourseDto("CS001", "CS Course", 2, modules);

        when(courseService.getCourse(new ContentIdentifierDto(courseId, 2))).thenReturn(course);

        CoursePublisher coursePublisher = new CoursePublisher(courseService,ivrGateway);
        coursePublisher.publish(courseId, 2);

        verify(ivrGateway).postCourse(course);
        verify(courseService).getCourse(new ContentIdentifierDto(courseId, 2));
    }

}
