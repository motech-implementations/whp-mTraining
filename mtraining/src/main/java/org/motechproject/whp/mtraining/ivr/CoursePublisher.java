package org.motechproject.whp.mtraining.ivr;

import org.joda.time.DateTime;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.whp.mtraining.CourseAdmin;
import org.motechproject.whp.mtraining.domain.Course;
import org.motechproject.whp.mtraining.repository.Courses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CoursePublisher {

    public static final Integer MAX_ATTEMPTS = 3;
    private static final Logger LOGGER = LoggerFactory.getLogger(CoursePublisher.class);

    private CourseService courseService;
    private IVRGateway ivrGateway;
    private CourseAdmin courseAdmin;
    private Courses courses;

    private Integer numberOfAttempts = 1;

    @Autowired
    public CoursePublisher(CourseService courseService, IVRGateway ivrGateway, CourseAdmin courseAdmin, Courses courses) {
        this.courseService = courseService;
        this.ivrGateway = ivrGateway;
        this.courseAdmin = courseAdmin;
        this.courses = courses;
    }

    public void publish(UUID courseId, Integer version) {
        if (numberOfAttempts > MAX_ATTEMPTS) {
            LOGGER.info(String.format("Attempt %d [%s] - Maximum number of attempts completed for courseId %s , version %s.", numberOfAttempts, DateTime.now(), courseId, version));
            return;
        }
        LOGGER.info(String.format("Attempt %d [%s] - Starting course publish to IVR for courseId %s , version %s ", numberOfAttempts, DateTime.now(), courseId, version));
        CourseDto course = courseService.getCourse(new ContentIdentifierDto(courseId, version));
        IVRResponse ivrResponse = ivrGateway.postCourse(course);
        courses.add(new Course(courseId, version, ivrResponse.isSuccess()));
        try {
            notifyCourseAdmin(courseId, version, ivrResponse);
        } catch (MailSendException ex) {
            LOGGER.error("Could not send mail", ex);
        }
    }

    private void notifyCourseAdmin(UUID courseId, Integer version, IVRResponse ivrResponse) {
        if (ivrResponse.isSuccess()) {
            LOGGER.info(String.format("Attempt %d [%s] - Course published to IVR for courseId %s , version %s ", numberOfAttempts, DateTime.now(), courseId, version));
            courseAdmin.notifyCoursePublished(courseId.toString());
        }
        if (ivrResponse.hasValidationErrors()) {
            LOGGER.error(String.format("Attempt %d [%s] - Course could not be published to IVR for courseId %s , version %s because of validation errors", numberOfAttempts, DateTime.now(), courseId, version));
            courseAdmin.notifyValidationFailures(courseId.toString(), ivrResponse);
        }
        if (ivrResponse.isNetworkFailure()) {
            LOGGER.error(String.format("Attempt %d [%s] - Course could not be published to IVR for courseId %s , version %s because of I/O errors", numberOfAttempts, DateTime.now(), courseId, version));
            courseAdmin.notifyNetworkFailure(courseId.toString());
            retryPublishing(courseId, version);
        }
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
