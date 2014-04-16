package org.motechproject.whp.mtraining.ivr;

import org.joda.time.DateTime;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.CourseAdmin;
import org.motechproject.whp.mtraining.domain.Course;
import org.motechproject.whp.mtraining.domain.CoursePublicationAttempt;
import org.motechproject.whp.mtraining.repository.AllCoursePublicationAttempts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CoursePublisher {

    public static final Integer MAX_ATTEMPTS = 3;
    private static final Logger LOGGER = LoggerFactory.getLogger(CoursePublisher.class);

    private CourseService courseService;
    private IVRGateway ivrGateway;
    private CourseAdmin courseAdmin;
    private AllCoursePublicationAttempts allCoursePublicationStatus;

    private Integer numberOfAttempts = 1;

    @Autowired
    public CoursePublisher(CourseService courseService, IVRGateway ivrGateway, CourseAdmin courseAdmin, AllCoursePublicationAttempts allCoursePublicationStatus) {
        this.courseService = courseService;
        this.ivrGateway = ivrGateway;
        this.courseAdmin = courseAdmin;
        this.allCoursePublicationStatus = allCoursePublicationStatus;
    }

    public void publish(UUID courseId, Integer version) {
        if (numberOfAttempts > MAX_ATTEMPTS) {
            LOGGER.info(String.format("Attempt %d [%s] - Maximum number of attempts completed for courseId %s , version %s.", numberOfAttempts, currentDateTime(), courseId, version));
            return;
        }

        CourseDto courseDTO = courseService.getCourse(new ContentIdentifierDto(courseId, version));
        if (!courseDTO.isActive()) {
            LOGGER.warn(String.format("[%s] Course with contentId %s and version %s inactive and hence not being published to IVR ", currentDateTime(), courseId, version));
            return;
        }

        LOGGER.info(String.format("Attempt %d [%s] - Starting course publish to IVR for courseId %s , version %s ", numberOfAttempts, currentDateTime(), courseId, version));

        Course course = new Course(courseDTO);
        course.removeInactiveContent();

        LOGGER.info(String.format("Attempt %d [%s] - Retrieved course %s courseId %s , version %s ", numberOfAttempts, currentDateTime(), course.getName(), courseId, version));

        IVRResponse ivrResponse = ivrGateway.postCourse(course);
        allCoursePublicationStatus.add(new CoursePublicationAttempt(courseId, version, ivrResponse.isSuccess()));

        if (ivrResponse.isSuccess()) {
            courseService.publish(new ContentIdentifierDto(courseId, version));
        }

        if (ivrResponse.isNetworkFailure()) {
            retryPublishing(courseId, version);
        }

        try {
            notifyCourseAdmin(course.getName(), version, ivrResponse);
        } catch (MailException ex) {
            LOGGER.error("Could not send mail because: ", ex);
        }
    }

    private void notifyCourseAdmin(String courseName, Integer version, IVRResponse ivrResponse) {
        if (ivrResponse.isSuccess()) {
            LOGGER.info(String.format("Attempt %d [%s] - Course published to IVR for course %s , version %s ", numberOfAttempts, currentDateTime(), courseName, version));
            courseAdmin.notifyCoursePublished(courseName, version);
            return;
        }
        LOGGER.error(String.format("Attempt %d [%s] - Course could not be published to IVR for course %s , version %s , responseCode - %s responseMessage - %s",
                numberOfAttempts, currentDateTime(), courseName, version, ivrResponse.getResponseCode(), ivrResponse.getResponseMessage()));
        courseAdmin.notifyCoursePublishFailure(courseName, version, ivrResponse);
    }

    private static DateTime currentDateTime() {
        return ISODateTimeUtil.nowInTimeZoneUTC();
    }

    private void retryPublishing(UUID courseId, Integer version) {
        try {
            Thread.sleep(numberOfAttempts * 1000l);
            numberOfAttempts = numberOfAttempts + 1;
            publish(courseId, version);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
