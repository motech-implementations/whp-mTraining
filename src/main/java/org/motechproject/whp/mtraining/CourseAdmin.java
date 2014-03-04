package org.motechproject.whp.mtraining;

import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailSenderService;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

import static java.lang.String.format;

@Component
public class CourseAdmin {

    private EmailSenderService emailSenderService;
    private SettingsFacade settingsFacade;

    @Autowired
    public CourseAdmin(EmailSenderService emailSenderService, SettingsFacade settingsFacade) {
        this.emailSenderService = emailSenderService;
        this.settingsFacade = settingsFacade;
    }

    public void notifyCoursePublished(String courseId) {
        Properties properties = settingsFacade.getProperties("mtraining.properties");
        String fromAddress = properties.getProperty("course.admin.email.from");
        String toAddress = properties.getProperty("course.admin.email.to");
        String subject = properties.getProperty("course.admin.email.subject.success");
        String messageFormat = properties.getProperty("course.admin.email.message.format");
        emailSenderService.send(new Mail(fromAddress, toAddress, subject, format(messageFormat, courseId)));
    }
}
