package org.motechproject.whp.mtraining;

import org.motechproject.server.config.SettingsFacade;
import org.motechproject.whp.mtraining.ivr.IVRResponse;
import org.motechproject.whp.mtraining.mail.Mail;
import org.motechproject.whp.mtraining.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

import static java.lang.String.format;

@Component
public class CourseAdmin {

    public static final String SUCCESS_SUBJECT_FORMAT = "Course %s (version %s) Published";
    public static final String FAILURE_SUBJECT_FORMAT = "Course %s (version %s) could not be published";
    public static final String FAILURE_MESSAGE_FORMAT = "Course %s (version %s) could not be published because: %d - %s";


    private final Properties properties;

    @Autowired
    private EmailService emailService;


    @Autowired
    public CourseAdmin(EmailService emailService, SettingsFacade settingsFacade) {
        this.emailService = emailService;
        this.properties = settingsFacade.asProperties();
    }

    public void notifyCoursePublished(String courseName) {
        emailService.send(new Mail(fromAddress(), toAddress(), format(SUCCESS_SUBJECT_FORMAT, courseName), format(SUCCESS_SUBJECT_FORMAT, courseName)));
    }

    public void notifyCoursePublishFailure(String courseName, IVRResponse ivrResponse) {
        emailService.send(new Mail(fromAddress(), toAddress(), format(FAILURE_SUBJECT_FORMAT, courseName), format(FAILURE_MESSAGE_FORMAT, courseName, ivrResponse.getResponseCode(), ivrResponse.getResponseMessage())));
    }

    private String toAddress() {
        return properties.getProperty("course.admin.email.to");
    }

    private String fromAddress() {
        return properties.getProperty("course.admin.email.from");
    }


}
