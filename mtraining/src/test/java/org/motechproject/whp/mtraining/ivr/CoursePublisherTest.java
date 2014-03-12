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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CoursePublisherTest {


    public static final String CONTENT_ID = "CONTENT_ID";
    public static final String VERSION = "VERSION";
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

        IVRResponse ivrResponse = new IVRResponse();
        ivrResponse.markSuccess();
        when(ivrGateway.postCourse(any(CourseDto.class))).thenReturn(ivrResponse);

        CoursePublisher coursePublisher = new CoursePublisher(courseService, ivrGateway, courseAdmin, courses);
        coursePublisher.publish(courseId, 2);

        verify(ivrGateway).postCourse(course);
        verify(courseService).getCourse(new ContentIdentifierDto(courseId, courseVersion));
        verify(courses).add(new Course(courseId, courseVersion, true));
    }

    @Test
    public void shouldNotifyCourseAdminOfSuccessfulCoursePublicationToIVR() {

        CourseDto courseDTO = new CourseDto();
        when(courseService.getCourse(new ContentIdentifierDto(cs001, 2))).thenReturn(courseDTO);

        when(ivrGateway.postCourse(courseDTO)).thenReturn(new IVRResponse(true));

        CoursePublisher coursePublisher = new CoursePublisher(courseService, ivrGateway, courseAdmin, courses);
        coursePublisher.publish(cs001, 2);

        verify(ivrGateway).postCourse(courseDTO);
        verify(courseAdmin).notifyCoursePublished(cs001.toString());
    }

    @Test
    public void shouldNotifyCourseAdminOfFailureIfThereAreValidationErrorsInResponse() {

        IVRResponse ivrResponse = new IVRResponse(false);
        HashMap<String, String> errors = new HashMap<>();
        errors.put("missingFiles", "hello.wav");
        ivrResponse.setErrors(errors);

        CourseDto courseDTO = new CourseDto();
        when(courseService.getCourse(new ContentIdentifierDto(cs001, 2))).thenReturn(courseDTO);

        given(ivrGateway.postCourse(courseDTO)).willReturn(ivrResponse);

        CoursePublisher coursePublisher = new CoursePublisher(courseService, ivrGateway, courseAdmin, courses);
        coursePublisher.publish(cs001, 2);

        verify(ivrGateway).postCourse(courseDTO);
        verify(courseAdmin).notifyValidationFailures(cs001.toString(), ivrResponse);

    }

    @Test
    public void shouldRetryPublishingInCaseOfNetworkFailure() {
        IVRResponse ivrResponse = new IVRResponse(false);
        ivrResponse.markNetworkFailure();

        CourseDto courseDTO = new CourseDto();
        when(courseService.getCourse(new ContentIdentifierDto(cs001, 2))).thenReturn(courseDTO);

        given(ivrGateway.postCourse(courseDTO)).willReturn(ivrResponse);

        CoursePublisher coursePublisher = new CoursePublisher(courseService, ivrGateway, courseAdmin, courses);
        coursePublisher.publish(cs001, 2);

        verify(ivrGateway, times(CoursePublisher.MAX_ATTEMPTS)).postCourse(courseDTO);
        verify(courseAdmin, times(CoursePublisher.MAX_ATTEMPTS)).notifyNetworkFailure(cs001.toString());
    }


}
