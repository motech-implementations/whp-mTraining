package org.motechproject.whp.mtraining;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailSenderService;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.whp.mtraining.ivr.IVRResponse;
import org.motechproject.whp.mtraining.ivr.IVRResponseCodes;

import java.util.Properties;

import static java.lang.String.format;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CourseAdminTest {

    private EmailSenderService emailSenderService;
    private SettingsFacade settingsFacade;

    @Before
    public void before() {
        emailSenderService = mock(EmailSenderService.class);
        settingsFacade = mock(SettingsFacade.class);
    }

    @Test
    public void shouldSendCoursePublicationEmail() {
        Properties properties = new Properties();
        properties.put("course.admin.email.from", "motech@email.com");
        properties.put("course.admin.email.to", "admin@email.com");
        when(settingsFacade.getProperties("mtraining.properties")).thenReturn(properties);

        CourseAdmin courseAdmin = new CourseAdmin(emailSenderService, settingsFacade);
        courseAdmin.notifyCoursePublished("CS001", 1);

        Mail mail = new Mail("motech@email.com", "admin@email.com", format(CourseAdmin.SUCCESS_SUBJECT_FORMAT, "CS001", 1), format(CourseAdmin.SUCCESS_SUBJECT_FORMAT, "CS001", 1));
        verify(emailSenderService).send(mail);
    }

    @Test
    public void shouldSendPublishFailureMail() {
        Properties properties = new Properties();
        properties.put("course.admin.email.from", "motech@email.com");
        properties.put("course.admin.email.to", "admin@email.com");
        properties.put("course.admin.email.subject.validation.failure", "Course Validation Failed");
        properties.put("course.admin.email.validation.failure.message.format", "Course %s failed with reason : %s");
        when(settingsFacade.getProperties("mtraining.properties")).thenReturn(properties);

        IVRResponse response = new IVRResponse(IVRResponseCodes.MISSING_FILES, "hello.wav");

        CourseAdmin courseAdmin = new CourseAdmin(emailSenderService, settingsFacade);
        courseAdmin.notifyCoursePublishFailure("CS001", 1, response);

        Mail mail = new Mail("motech@email.com", "admin@email.com", format(CourseAdmin.FAILURE_SUBJECT_FORMAT, "CS001"), format(CourseAdmin.FAILURE_MESSAGE_FORMAT, "CS001", 1, response.getResponseMessage()));
        verify(emailSenderService).send(mail);
    }

}
