package org.motechproject.whp.mtraining.listener;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.event.MotechEvent;
import org.motechproject.whp.mtraining.CourseAdmin;
import org.motechproject.whp.mtraining.ivr.CoursePublisher;
import org.motechproject.whp.mtraining.ivr.IVRResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CourseEventListenerTest {

    public static final String COURSE_ADDED_EVENT = "org.motechproject.mtraining.chapter.creation";
    public static final String CONTENT_ID = "CONTENT_ID";
    public static final String VERSION = "VERSION";

    private CoursePublisher coursePublisher;
    private CourseEventListener courseEventListener;
    private CourseAdmin courseAdmin;
    static UUID cs001 = UUID.randomUUID();

    @Before
    public void before() {
        coursePublisher = mock(CoursePublisher.class);
        courseAdmin = mock(CourseAdmin.class);
        courseEventListener = new CourseEventListener(coursePublisher, courseAdmin);

    }

    @Test
    public void shouldPublishCourseToIVRWhenCourseIsAdded() {
        Map<String, Object> eventData = new HashMap<>();

        eventData.put(CONTENT_ID, cs001);
        eventData.put(VERSION, 3);

        given(coursePublisher.publish(cs001, 3)).willReturn(new IVRResponse(true));

        courseEventListener.courseAdded(new MotechEvent(COURSE_ADDED_EVENT, eventData));

        verify(coursePublisher).publish(cs001, 3);
    }



}
