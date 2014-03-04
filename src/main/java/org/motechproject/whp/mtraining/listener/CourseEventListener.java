package org.motechproject.whp.mtraining.listener;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.whp.mtraining.CourseAdmin;
import org.motechproject.whp.mtraining.ivr.CoursePublisher;
import org.motechproject.whp.mtraining.ivr.PublishingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CourseEventListener {

    public static final String COURSE_ADDED_EVENT = "CourseAddedEvent";
    private CoursePublisher coursePublisher;
    private CourseAdmin courseAdmin;

    @Autowired
    public CourseEventListener(CoursePublisher coursePublisher, CourseAdmin courseAdmin) {
        this.coursePublisher = coursePublisher;
        this.courseAdmin = courseAdmin;
    }

    @MotechListener(subjects = COURSE_ADDED_EVENT)
    public void courseAdded(MotechEvent event) {
        Map<String, Object> eventData = event.getParameters();
        String courseId = (String) eventData.get("courseId");
        PublishingResult result = coursePublisher.publish(courseId);
        if (result.isSuccess()) {
            courseAdmin.notifyCoursePublished(courseId);
        }
    }

}
