package org.motechproject.whp.mtraining.listener;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.event.MotechEvent;
import org.motechproject.whp.mtraining.ivr.CoursePublisher;
import org.motechproject.whp.mtraining.reports.CourseReporter;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CourseEventListenerTest {

    public static final String COURSE_ADDED_EVENT = "org.motechproject.mtraining.chapter.creation";
    public static final String CONTENT_ID = "CONTENT_ID";
    public static final String VERSION = "VERSION";

    private CoursePublisher coursePublisher;
    private CourseReporter courseReporter;
    private CourseEventListener courseEventListener;
    static long cs001 = 12;

    @Before
    public void before() {
        coursePublisher = mock(CoursePublisher.class);
        courseReporter = mock(CourseReporter.class);
        courseEventListener = new CourseEventListener(coursePublisher, courseReporter);

    }

    @Test
    public void shouldPublishCourseToIVRWhenCourseIsAdded() {
        Map<String, Object> eventData = new HashMap<>();

        eventData.put(CONTENT_ID, cs001);
        eventData.put(VERSION, 3);

        courseEventListener.courseAdded(new MotechEvent(COURSE_ADDED_EVENT, eventData));

        verify(coursePublisher).publish(cs001);
    }

    @Test
    public void shouldReportWhenCourseIsAdded() {
        Map<String, Object> eventData = new HashMap<>();

        eventData.put(CONTENT_ID, cs001);
        eventData.put(VERSION, 3);

        courseEventListener.courseAdded(new MotechEvent(COURSE_ADDED_EVENT, eventData));
        verify(courseReporter).reportCourseAdded(cs001);
    }


}
