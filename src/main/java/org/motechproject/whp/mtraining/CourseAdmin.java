package org.motechproject.whp.mtraining;

import org.apache.commons.lang.StringUtils;
import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailSenderService;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.whp.mtraining.ivr.IVRResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

import static java.lang.String.format;

@Component
public class CourseAdmin {

    private final Properties properties;
    private EmailSenderService emailSenderService;

    @Autowired
    public CourseAdmin(EmailSenderService emailSenderService, SettingsFacade settingsFacade) {
        this.emailSenderService = emailSenderService;
        this.properties = settingsFacade.getProperties("mtraining.properties");
    }

    public void notifyCoursePublished(String courseId) {
        String subject = properties.getProperty("course.admin.email.subject.success");
        String messageFormat = properties.getProperty("course.admin.email.success.message.format");
        emailSenderService.send(new Mail(fromAddress(), toAddress(), subject, format(messageFormat, courseId)));
    }

    public void notifyValidationFailures(String courseId, IVRResponse ivrResponse) {
        String subject = properties.getProperty("course.admin.email.subject.validation.failure");
        if (ivrResponse.hasValidationErrors()) {
            String validationFailureMessageFormat = properties.getProperty("course.admin.email.validation.failure.message.format");
            if (!ivrResponse.getMissingFiles().isEmpty()) {
                List<String> missingFiles = ivrResponse.getMissingFiles();
                String message = String.format("Missing file(s) %s ", StringUtils.join(missingFiles, IVRResponse.MISSING_FILES_DELIMITER));
                emailSenderService.send(new Mail(fromAddress(), toAddress(), subject, String.format(validationFailureMessageFormat, courseId, message)));
            }
        }
    }

    private String toAddress() {
        return properties.getProperty("course.admin.email.to");
    }

    private String fromAddress() {
        return properties.getProperty("course.admin.email.from");
    }


}
