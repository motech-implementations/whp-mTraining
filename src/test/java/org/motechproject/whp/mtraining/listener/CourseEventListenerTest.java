package org.motechproject.whp.mtraining.listener;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.event.MotechEvent;
import org.motechproject.whp.mtraining.CourseAdmin;
import org.motechproject.whp.mtraining.ivr.CoursePublisher;
import org.motechproject.whp.mtraining.ivr.PublishingResult;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CourseEventListenerTest {

    private CoursePublisher coursePublisher;
    private CourseEventListener courseEventListener;
    private CourseAdmin courseAdmin;

    @Before
    public void before() {
        coursePublisher = mock(CoursePublisher.class);
        courseAdmin = mock(CourseAdmin.class);
        courseEventListener = new CourseEventListener(coursePublisher, courseAdmin);
    }

    @Test
    public void shouldPublishCourseToIVRWhenCourseIsAdded() {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("courseId", "CS001");

        given(coursePublisher.publish("CS001")).willReturn(PublishingResult.SUCCESS);

        courseEventListener.courseAdded(new MotechEvent(CourseEventListener.COURSE_ADDED_EVENT, eventData));

        verify(coursePublisher).publish("CS001");
    }

    @Test
    public void shouldNotifyCourseAdminOfSuccessfulCoursePublicationToIVR() {

        given(coursePublisher.publish("CS001")).willReturn(PublishingResult.SUCCESS);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("courseId", "CS001");

        courseEventListener.courseAdded(new MotechEvent(CourseEventListener.COURSE_ADDED_EVENT, eventData));

        verify(courseAdmin).notifyCoursePublished("CS001");

    }


}
