package org.motechproject.whp.mtraining;

import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailSenderService;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.whp.mtraining.ivr.IVRResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

import static java.lang.String.format;

@Component
public class CourseAdmin {

    public static final String SUCCESS_SUBJECT_FORMAT = "Course %s Published with version %s";
    public static final String FAILURE_SUBJECT_FORMAT = "Course %s could not be published";
    public static final String FAILURE_MESSAGE_FORMAT = "Course %s with version %s could not be published because of %s";


    private final Properties properties;
    private EmailSenderService emailSenderService;


    @Autowired
    public CourseAdmin(EmailSenderService emailSenderService, SettingsFacade settingsFacade) {
        this.emailSenderService = emailSenderService;
        this.properties = settingsFacade.getProperties("mtraining.properties");
    }

    public void notifyCoursePublished(String courseName, Integer version) {
        emailSenderService.send(new Mail(fromAddress(), toAddress(), format(SUCCESS_SUBJECT_FORMAT, courseName, version), format(SUCCESS_SUBJECT_FORMAT, courseName, version)));
    }

    public void notifyCoursePublishFailure(String courseName, Integer version, IVRResponse ivrResponse) {
        emailSenderService.send(new Mail(fromAddress(), toAddress(), format(FAILURE_SUBJECT_FORMAT, courseName), format(FAILURE_MESSAGE_FORMAT, courseName, version, ivrResponse.getResponseMessage())));
    }

    private String toAddress() {
        return properties.getProperty("course.admin.email.to");
    }

    private String fromAddress() {
        return properties.getProperty("course.admin.email.from");
    }


}
