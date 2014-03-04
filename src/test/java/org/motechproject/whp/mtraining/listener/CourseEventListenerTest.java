package org.motechproject.whp.mtraining.listener;

import org.junit.Test;
import org.motechproject.event.MotechEvent;
import org.motechproject.whp.mtraining.ivr.CoursePublisher;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CourseEventListenerTest {

    @Test
    public void shouldPublishCourseToIVRWhenCourseIsAdded() {
        CoursePublisher coursePublisher = mock(CoursePublisher.class);

        CourseEventListener courseEventListener = new CourseEventListener(coursePublisher);
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("courseId", "CS001");

        courseEventListener.courseAdded(new MotechEvent(CourseEventListener.COURSE_ADDED_EVENT, eventData));

        verify(coursePublisher).publish("CS001");
    }


}
