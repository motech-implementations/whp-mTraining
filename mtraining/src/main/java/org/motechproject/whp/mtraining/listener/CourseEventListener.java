package org.motechproject.whp.mtraining.listener;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.whp.mtraining.constants.MTrainingEventConstants;
import org.motechproject.whp.mtraining.ivr.CoursePublisher;
import org.motechproject.whp.mtraining.reports.CourseReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class CourseEventListener {


    private CoursePublisher coursePublisher;
    private CourseReporter courseReporter;

    @Autowired
    public CourseEventListener(CoursePublisher coursePublisher, CourseReporter courseReporter) {
        this.coursePublisher = coursePublisher;
        this.courseReporter = courseReporter;
    }

    @MotechListener(subjects = MTrainingEventConstants.COURSE_CREATION_EVENT)
    public void courseAdded(MotechEvent event) {
        Map<String, Object> eventData = event.getParameters();
        Long courseId = (Long) eventData.get(MTrainingEventConstants.CONTENT_ID);
        Integer version = (Integer) eventData.get(MTrainingEventConstants.VERSION);
        courseReporter.reportCourseAdded(courseId);
        try {
            coursePublisher.publish(courseId);
        } catch (Exception ex) { }
    }

}
