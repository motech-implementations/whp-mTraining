package org.motechproject.whp.mtraining.ivr;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.CourseUnitState;
import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.CourseAdmin;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.whp.mtraining.domain.CoursePublicationAttempt;
import org.motechproject.whp.mtraining.service.CoursePublicationAttemptService;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CoursePublisherTest {


    static long cs001 = 123L;

    private MTrainingService mTrainingService;
    private IVRGateway ivrGateway;
    private CoursePublicationAttemptService coursePublicationAttemptService;
    private CourseAdmin courseAdmin;
    private CoursePublisher coursePublisher;

    @Before
    public void before() {
        mTrainingService = mock(MTrainingService.class);
        ivrGateway = mock(IVRGateway.class);
        coursePublicationAttemptService = mock(CoursePublicationAttemptService.class);
        courseAdmin = mock(CourseAdmin.class);
        coursePublisher = new CoursePublisher(mTrainingService, ivrGateway, courseAdmin, coursePublicationAttemptService);
    }

    @Test
    public void shouldPostTheCorrectCourse() {
        Course course = new Course("NA001", CourseUnitState.Active, "This is a test course");
        when(mTrainingService.getCourseById(cs001)).thenReturn(course);


        IVRResponse ivrResponse = new IVRResponse(IVRResponseCodes.OK);
        when(ivrGateway.postCourse(any(Course.class))).thenReturn(ivrResponse);

        coursePublisher.publish(cs001);

        ArgumentCaptor<Course> courseArgumentCaptor = ArgumentCaptor.forClass(Course.class);
        verify(ivrGateway).postCourse(courseArgumentCaptor.capture());

        ArgumentCaptor<CoursePublicationAttempt> coursePublicationAttemptArgumentCaptor = ArgumentCaptor.forClass(CoursePublicationAttempt.class);

        InOrder inOrder = inOrder(mTrainingService, coursePublicationAttemptService);

        inOrder.verify(coursePublicationAttemptService).createCoursePublicationAttempt(coursePublicationAttemptArgumentCaptor.capture());
        //TODO inOrder.verify(mTrainingService).publish(contentIdentifierDto);

        Course publishedCourse = courseArgumentCaptor.getValue();

        assertThat(publishedCourse.getName(), Is.is("NA001"));
        assertThat(publishedCourse.getContent(), Is.is("This is a test course"));
        assertThat(publishedCourse.getChapters().isEmpty(), Is.is(true));

        CoursePublicationAttempt coursePublicationAttempt = coursePublicationAttemptArgumentCaptor.getValue();
        assertThat(coursePublicationAttempt.getCourseId(), Is.is(cs001));
        assertThat(coursePublicationAttempt.isPublishedToIvr(), Is.is(true));
    }


    @Test
    public void shouldPublishCourseToIVRUrl() {
        long courseId = 123L;

        List<Chapter> chapters = Collections.emptyList();
        int courseVersion = 2;
        Course course = new Course("CS001", CourseUnitState.Active, "Course file name", chapters);

        when(mTrainingService.getCourseById(courseId)).thenReturn(course);

        IVRResponse ivrResponse = new IVRResponse(IVRResponseCodes.OK);
        when(ivrGateway.postCourse(any(Course.class))).thenReturn(ivrResponse);

        coursePublisher.publish(courseId);

        verify(ivrGateway).postCourse(any(Course.class));
        verify(mTrainingService).getCourseById(courseId);
        verify(coursePublicationAttemptService).createCoursePublicationAttempt(new CoursePublicationAttempt(courseId, true));
    }

    @Test
    public void shouldNotifyCourseAdminOfSuccessfulCoursePublicationToIVR() {
        Course course = new Course("CS001", CourseUnitState.Inactive, "Course file name");
        when(mTrainingService.getCourseById(cs001)).thenReturn(course);

        when(ivrGateway.postCourse(any(Course.class))).thenReturn(new IVRResponse(800, "OK"));

        coursePublisher.publish(cs001);

        verify(courseAdmin).notifyCoursePublished("CS001");
    }

    @Test
    public void shouldNotifyCourseAdminOfFailureIfThereAreValidationErrorsInResponse() {
        IVRResponse ivrResponse = new IVRResponse(IVRResponseCodes.MISSING_FILES, "file1,file2");

        Course course = new Course("CS001", CourseUnitState.Inactive, "Course file name", null);
        when(mTrainingService.getCourseById(cs001)).thenReturn(course);

        given(ivrGateway.postCourse(any(Course.class))).willReturn(ivrResponse);

        coursePublisher.publish(cs001);

        verify(courseAdmin).notifyCoursePublishFailure("CS001", ivrResponse);

    }

    @Test
    public void shouldRetryPublishingInCaseOfNetworkFailure() {
        IVRResponse ivrResponse = new IVRResponse(IVRResponseCodes.NETWORK_FAILURE);

        Course course = new Course("CS001", CourseUnitState.valueOf(""), "Course file name", null);
        when(mTrainingService.getCourseById(cs001)).thenReturn(course);

        given(ivrGateway.postCourse(any(Course.class))).willReturn(ivrResponse);

        coursePublisher.publish(cs001);

        verify(ivrGateway, times(CoursePublisher.MAX_ATTEMPTS)).postCourse(any(Course.class));
        verify(courseAdmin, times(CoursePublisher.MAX_ATTEMPTS)).notifyCoursePublishFailure("CS001", ivrResponse);
    }

    @Test
    public void shouldNotPublishInactiveCourse() {
        Course course = new Course("CS001", CourseUnitState.valueOf(""), "Course file name", null);
        when(mTrainingService.getCourseById(cs001)).thenReturn(course);

        coursePublisher.publish(cs001);

        verify(ivrGateway, never()).postCourse(any(Course.class));
    }


    @Test
    public void shouldNotPublishToMTrainingIfCourseIsNotPublishedToIVR() throws Exception {
        Course course = new Course("CS001", CourseUnitState.valueOf(""), "Course file name", null);
        when(mTrainingService.getCourseById(cs001)).thenReturn(course);
        when(ivrGateway.postCourse(any(Course.class))).thenReturn(new IVRResponse(1001, "Missing Files"));

        coursePublisher.publish(cs001);

        verify(coursePublicationAttemptService).createCoursePublicationAttempt(new CoursePublicationAttempt(cs001, false));
        verify(mTrainingService).getCourseById(cs001);
        verifyNoMoreInteractions(mTrainingService);
    }
}
