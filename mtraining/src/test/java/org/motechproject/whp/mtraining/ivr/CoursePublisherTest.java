package org.motechproject.whp.mtraining.ivr;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.whp.mtraining.CourseAdmin;
import org.motechproject.whp.mtraining.domain.Course;
import org.motechproject.whp.mtraining.domain.CoursePublicationAttempt;
import org.motechproject.whp.mtraining.repository.AllCoursePublicationAttempts;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CoursePublisherTest {


    static UUID cs001 = UUID.randomUUID();

    private CourseService courseService;
    private IVRGateway ivrGateway;
    private AllCoursePublicationAttempts allCoursePublicationAttempts;
    private CourseAdmin courseAdmin;
    private CoursePublisher coursePublisher;

    @Before
    public void before() {
        courseService = mock(CourseService.class);
        ivrGateway = mock(IVRGateway.class);
        allCoursePublicationAttempts = mock(AllCoursePublicationAttempts.class);
        courseAdmin = mock(CourseAdmin.class);
        coursePublisher = new CoursePublisher(courseService, ivrGateway, courseAdmin, allCoursePublicationAttempts);
    }


    @Test
    public void shouldPostTheCorrectCourse() {
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(cs001, 3);
        CourseDto courseDTO = new CourseDto(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion(), true, "NA001", "This is a test course", "Test Author", null);
        when(courseService.getCourse(contentIdentifierDto)).thenReturn(courseDTO);


        IVRResponse ivrResponse = new IVRResponse(IVRResponseCodes.OK);
        when(ivrGateway.postCourse(any(Course.class))).thenReturn(ivrResponse);

        coursePublisher.publish(cs001, 3);

        ArgumentCaptor<Course> courseArgumentCaptor = ArgumentCaptor.forClass(Course.class);
        verify(ivrGateway).postCourse(courseArgumentCaptor.capture());

        Course publishedCourse = courseArgumentCaptor.getValue();

        assertThat(publishedCourse.getName(), Is.is("NA001"));
        assertThat(publishedCourse.getVersion(), Is.is(3));
        assertThat(publishedCourse.getDescription(), Is.is("This is a test course"));
        assertThat(publishedCourse.getModules().isEmpty(), Is.is(true));
    }


    @Test
    public void shouldPublishCourseToIVRUrl() {
        UUID courseId = UUID.randomUUID();

        List<ModuleDto> modules = Collections.emptyList();
        int courseVersion = 2;
        CourseDto courseDto = new CourseDto(true, "CS001", "CS Course", "Created By", modules);

        when(courseService.getCourse(new ContentIdentifierDto(courseId, courseVersion))).thenReturn(courseDto);

        IVRResponse ivrResponse = new IVRResponse(IVRResponseCodes.OK);
        when(ivrGateway.postCourse(any(Course.class))).thenReturn(ivrResponse);

        coursePublisher.publish(courseId, 2);

        verify(ivrGateway).postCourse(any(Course.class));
        verify(courseService).getCourse(new ContentIdentifierDto(courseId, courseVersion));
        verify(allCoursePublicationAttempts).add(new CoursePublicationAttempt(courseId, courseVersion, true));
    }

    @Test
    public void shouldNotifyCourseAdminOfSuccessfulCoursePublicationToIVR() {

        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(cs001, 2);
        CourseDto courseDTO = new CourseDto(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion(), true, "CS001", "", "Created By", null);
        when(courseService.getCourse(contentIdentifierDto)).thenReturn(courseDTO);

        when(ivrGateway.postCourse(any(Course.class))).thenReturn(new IVRResponse(800, "OK"));

        coursePublisher.publish(cs001, 2);

        verify(courseAdmin).notifyCoursePublished("CS001", 2);
    }

    @Test
    public void shouldNotifyCourseAdminOfFailureIfThereAreValidationErrorsInResponse() {
        IVRResponse ivrResponse = new IVRResponse(IVRResponseCodes.MISSING_FILES, "file1,file2");

        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(cs001, 2);
        CourseDto courseDTO = new CourseDto(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion(), true, "CS001", "", "Created By", null);
        when(courseService.getCourse(contentIdentifierDto)).thenReturn(courseDTO);

        given(ivrGateway.postCourse(any(Course.class))).willReturn(ivrResponse);

        coursePublisher.publish(cs001, 2);

        verify(courseAdmin).notifyCoursePublishFailure("CS001", 2, ivrResponse);

    }

    @Test
    public void shouldRetryPublishingInCaseOfNetworkFailure() {
        IVRResponse ivrResponse = new IVRResponse(IVRResponseCodes.NETWORK_FAILURE);

        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(cs001, 2);
        CourseDto courseDTO = new CourseDto(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion(), true, "CS001", "", "Created By", null);
        when(courseService.getCourse(contentIdentifierDto)).thenReturn(courseDTO);

        given(ivrGateway.postCourse(any(Course.class))).willReturn(ivrResponse);

        coursePublisher.publish(cs001, 2);

        verify(ivrGateway, times(CoursePublisher.MAX_ATTEMPTS)).postCourse(any(Course.class));
        verify(courseAdmin, times(CoursePublisher.MAX_ATTEMPTS)).notifyCoursePublishFailure("CS001", 2, ivrResponse);
    }

    @Test
    public void shouldNotPublishInactiveCourse() {
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(cs001, 2);
        CourseDto courseDTO = new CourseDto(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion(), false, "CS001", "", "Created By", null);
        when(courseService.getCourse(contentIdentifierDto)).thenReturn(courseDTO);

        coursePublisher.publish(cs001, 2);

        verify(ivrGateway, never()).postCourse(any(Course.class));

    }


}
