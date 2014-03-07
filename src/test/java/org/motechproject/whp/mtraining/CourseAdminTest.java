package org.motechproject.whp.mtraining;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailSenderService;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.whp.mtraining.ivr.IVRResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
        properties.put("course.admin.email.subject.success", "Course Published");
        properties.put("course.admin.email.success.message.format", "Course %s Published");
        when(settingsFacade.getProperties("mtraining.properties")).thenReturn(properties);

        CourseAdmin courseAdmin = new CourseAdmin(emailSenderService, settingsFacade);
        courseAdmin.notifyCoursePublished("CS001");

        Mail mail = new Mail("motech@email.com", "admin@email.com", "Course Published", "Course CS001 Published");
        verify(emailSenderService).send(mail);
    }

    @Test
    public void shouldSendValidationFailureEmail() {
        Properties properties = new Properties();
        properties.put("course.admin.email.from", "motech@email.com");
        properties.put("course.admin.email.to", "admin@email.com");
        properties.put("course.admin.email.subject.validation.failure", "Course Validation Failed");
        properties.put("course.admin.email.validation.failure.message.format", "Course %s failed with reason : %s");
        when(settingsFacade.getProperties("mtraining.properties")).thenReturn(properties);

        IVRResponse response = new IVRResponse();
        Map<String, String> errors = new HashMap<>();
        errors.put("missingFiles", "hello.wav");
        response.setErrors(errors);

        CourseAdmin courseAdmin = new CourseAdmin(emailSenderService, settingsFacade);
        courseAdmin.notifyValidationFailures("CS001", response);

        Mail mail = new Mail("motech@email.com", "admin@email.com", "Course Validation Failed", "Course CS001 failed with reason : Missing file(s) hello.wav ");
        verify(emailSenderService).send(mail);
    }


}
