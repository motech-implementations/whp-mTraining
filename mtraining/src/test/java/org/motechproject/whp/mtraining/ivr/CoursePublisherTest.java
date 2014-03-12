package org.motechproject.whp.mtraining.ivr;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.whp.mtraining.CourseAdmin;
import org.motechproject.whp.mtraining.domain.Course;
import org.motechproject.whp.mtraining.repository.Courses;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CoursePublisherTest {


    static UUID cs001 = UUID.randomUUID();

    private CourseService courseService;
    private IVRGateway ivrGateway;
    private Courses courses;
    private CourseAdmin courseAdmin;

    @Before
    public void before() {
        courseService = mock(CourseService.class);
        ivrGateway = mock(IVRGateway.class);
        courses = mock(Courses.class);
        courseAdmin = mock(CourseAdmin.class);
    }


    @Test
    public void shouldPublishCourseToIVRUrl() {
        UUID courseId = UUID.randomUUID();

        List<ModuleDto> modules = Collections.emptyList();
        int courseVersion = 2;
        CourseDto course = new CourseDto("CS001", "CS Course", true, modules);

        when(courseService.getCourse(new ContentIdentifierDto(courseId, courseVersion))).thenReturn(course);

        IVRResponse ivrResponse = new IVRResponse(IVRResponseCodes.OK);
        when(ivrGateway.postCourse(any(CourseDto.class))).thenReturn(ivrResponse);

        CoursePublisher coursePublisher = new CoursePublisher(courseService, ivrGateway, courseAdmin, courses);
        coursePublisher.publish(courseId, 2);

        verify(ivrGateway).postCourse(course);
        verify(courseService).getCourse(new ContentIdentifierDto(courseId, courseVersion));
        verify(courses).add(new Course(courseId, courseVersion, true));
    }

    @Test
    public void shouldNotifyCourseAdminOfSuccessfulCoursePublicationToIVR() {

        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(cs001, 2);
        CourseDto courseDTO = new CourseDto("CS001", "", contentIdentifierDto, null);
        when(courseService.getCourse(contentIdentifierDto)).thenReturn(courseDTO);

        when(ivrGateway.postCourse(courseDTO)).thenReturn(new IVRResponse(800, "OK"));

        CoursePublisher coursePublisher = new CoursePublisher(courseService, ivrGateway, courseAdmin, courses);
        coursePublisher.publish(cs001, 2);

        verify(ivrGateway).postCourse(courseDTO);
        verify(courseAdmin).notifyCoursePublished("CS001", 2);
    }

    @Test
    public void shouldNotifyCourseAdminOfFailureIfThereAreValidationErrorsInResponse() {
        IVRResponse ivrResponse = new IVRResponse(IVRResponseCodes.MISSING_FILES, "file1,file2");

        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(cs001, 2);
        CourseDto courseDTO = new CourseDto("CS001", "", contentIdentifierDto, null);
        when(courseService.getCourse(contentIdentifierDto)).thenReturn(courseDTO);

        given(ivrGateway.postCourse(courseDTO)).willReturn(ivrResponse);

        CoursePublisher coursePublisher = new CoursePublisher(courseService, ivrGateway, courseAdmin, courses);
        coursePublisher.publish(cs001, 2);

        verify(ivrGateway).postCourse(courseDTO);
        verify(courseAdmin).notifyCoursePublishFailure("CS001", 2, ivrResponse);

    }

    @Test
    public void shouldRetryPublishingInCaseOfNetworkFailure() {
        IVRResponse ivrResponse = new IVRResponse(IVRResponseCodes.NETWORK_FAILURE);

        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(cs001, 2);
        CourseDto courseDTO = new CourseDto("CS001", "", contentIdentifierDto, null);
        when(courseService.getCourse(contentIdentifierDto)).thenReturn(courseDTO);

        given(ivrGateway.postCourse(courseDTO)).willReturn(ivrResponse);

        CoursePublisher coursePublisher = new CoursePublisher(courseService, ivrGateway, courseAdmin, courses);
        coursePublisher.publish(cs001, 2);

        verify(ivrGateway, times(CoursePublisher.MAX_ATTEMPTS)).postCourse(courseDTO);
        verify(courseAdmin, times(CoursePublisher.MAX_ATTEMPTS)).notifyCoursePublishFailure("CS001", 2, ivrResponse);
    }


}
