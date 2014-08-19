package org.motechproject.whp.mtraining;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.whp.mtraining.ivr.IVRResponse;
import org.motechproject.whp.mtraining.ivr.IVRResponseCodes;
import org.motechproject.whp.mtraining.mail.Mail;
import org.motechproject.whp.mtraining.service.EmailService;

import java.util.Properties;

import static java.lang.String.format;
import static org.mockito.Mockito.*;

public class CourseAdminTest {

    private EmailService emailService;
    private SettingsFacade settingsFacade;

    @Before
    public void before() {
        emailService = mock(EmailService.class);
        settingsFacade = mock(SettingsFacade.class);
    }

    @Test
    public void shouldSendCoursePublicationEmail() {
        Properties properties = new Properties();
        properties.put("course.admin.email.from", "motech@email.com");
        properties.put("course.admin.email.to", "admin@email.com");
        when(settingsFacade.asProperties()).thenReturn(properties);
        CourseAdmin courseAdmin = new CourseAdmin(emailService, settingsFacade);
        courseAdmin.notifyCoursePublished("CS001");

        Mail mail = new Mail("motech@email.com", "admin@email.com", format(CourseAdmin.SUCCESS_SUBJECT_FORMAT, "CS001", 1), format(CourseAdmin.SUCCESS_SUBJECT_FORMAT, "CS001", 1));
        verify(emailService).send(mail);
    }

    @Test
    public void shouldSendPublishFailureMail() {
        Properties properties = new Properties();
        properties.put("course.admin.email.from", "motech@email.com");
        properties.put("course.admin.email.to", "admin@email.com");
        properties.put("course.admin.email.subject.validation.failure", "Course Validation Failed");
        properties.put("course.admin.email.validation.failure.message.format", "Course %s (version %s) failed with reason : %s");
        when(settingsFacade.asProperties()).thenReturn(properties);

        IVRResponse response = new IVRResponse(IVRResponseCodes.MISSING_FILES, "hello.wav");

        CourseAdmin courseAdmin = new CourseAdmin(emailService, settingsFacade);
        courseAdmin.notifyCoursePublishFailure("CS001", response);

        Mail mail = new Mail("motech@email.com", "admin@email.com", format(CourseAdmin.FAILURE_SUBJECT_FORMAT, "CS001", 1), format(CourseAdmin.FAILURE_MESSAGE_FORMAT, "CS001", 1001, "hello.wav", response.getResponseMessage()));
        verify(emailService).send(mail);
    }

}
