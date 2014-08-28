package org.motechproject.whp.mtraining.ivr;

import org.joda.time.DateTime;
import org.motechproject.mtraining.domain.CourseUnitState;
import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.dto.CoursePlanDto;
import org.motechproject.whp.mtraining.service.CoursePublicationAttemptService;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.CourseAdmin;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.whp.mtraining.domain.CoursePublicationAttempt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoursePublisher {

    public static final Integer MAX_ATTEMPTS = 3;
    private static final Logger LOGGER = LoggerFactory.getLogger(CoursePublisher.class);

    private MTrainingService courseService;
    private IVRGateway ivrGateway;
    private CourseAdmin courseAdmin;
    private CoursePublicationAttemptService coursePublicationAttemptService;
    private DtoFactoryService dtoFactoryService;

    private Integer numberOfAttempts = 1;

    @Autowired
    public CoursePublisher(MTrainingService courseService, IVRGateway ivrGateway, CourseAdmin courseAdmin,
                           CoursePublicationAttemptService coursePublicationAttemptService, DtoFactoryService dtoFactoryService) {
        this.courseService = courseService;
        this.ivrGateway = ivrGateway;
        this.courseAdmin = courseAdmin;
        this.coursePublicationAttemptService = coursePublicationAttemptService;
        this.dtoFactoryService = dtoFactoryService;
    }

    public void publish(long courseId) {
        if (numberOfAttempts > MAX_ATTEMPTS) {
            LOGGER.info(String.format("Attempt %d [%s] - Maximum number of attempts completed for courseId %s", numberOfAttempts, currentDateTime(), courseId));
            numberOfAttempts = 1;
            return;
        }

        CoursePlanDto course = dtoFactoryService.getCourseDtoWithChildCollections(courseId);
//        if (course.getState() != CourseUnitState.Active) {
//            LOGGER.warn(String.format("[%s] Course with contentId %s inactive and hence not being published to IVR ", currentDateTime(), courseId));
//            return;
//        }

        LOGGER.info(String.format("Attempt %d [%s] - Starting course publish to IVR for courseId %s", numberOfAttempts, currentDateTime(), courseId));

//TODO        course.removeInactiveContent();

        LOGGER.info(String.format("Attempt %d [%s] - Retrieved course %s courseId %s", numberOfAttempts, currentDateTime(), course.getName(), courseId));

        IVRResponse ivrResponse = ivrGateway.postCourse(course);
        coursePublicationAttemptService.createCoursePublicationAttempt(new CoursePublicationAttempt(courseId, ivrResponse.isSuccess()));
//TODO
//        if (ivrResponse.isSuccess()) {
//            courseService.publish(new ContentIdentifierDto());
//        }

        if (ivrResponse.isNetworkFailure()) {
            retryPublishing(courseId);
        }

        try {
            notifyCourseAdmin(course.getName(), ivrResponse);
        } catch (MailException ex) {
            LOGGER.error("Could not send mail because: ", ex);
        }
    }

    private void notifyCourseAdmin(String courseName, IVRResponse ivrResponse) {
        if (ivrResponse.isSuccess()) {
            LOGGER.info(String.format("Attempt %d [%s] - Course published to IVR for course %s", numberOfAttempts, currentDateTime(), courseName));
            courseAdmin.notifyCoursePublished(courseName);
            return;
        }
        LOGGER.error(String.format("Attempt %d [%s] - Course could not be published to IVR for course %s , version %s , responseCode - %s responseMessage - %s",
                numberOfAttempts, currentDateTime(), courseName, ivrResponse.getResponseCode(), ivrResponse.getResponseMessage()));
        courseAdmin.notifyCoursePublishFailure(courseName, ivrResponse);
    }

    private static DateTime currentDateTime() {
        return ISODateTimeUtil.nowInTimeZoneUTC();
    }

    private void retryPublishing(long courseId) {
        try {
            Thread.sleep(numberOfAttempts * 1000l);
            numberOfAttempts = numberOfAttempts + 1;
            publish(courseId);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
