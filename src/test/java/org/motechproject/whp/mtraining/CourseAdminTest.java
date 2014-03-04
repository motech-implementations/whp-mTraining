package org.motechproject.whp.mtraining;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.email.model.Mail;
import org.motechproject.email.service.EmailSenderService;
import org.motechproject.server.config.SettingsFacade;

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
        CourseAdmin courseAdmin = new CourseAdmin(emailSenderService, settingsFacade);

        Properties properties = new Properties();
        properties.put("course.admin.email.from", "motech@email.com");
        properties.put("course.admin.email.to", "admin@email.com");
        properties.put("course.admin.email.subject.success", "Course Published");
        properties.put("course.admin.email.message.format", "Course %s Published");
        when(settingsFacade.getProperties("mtraining.properties")).thenReturn(properties);

        courseAdmin.notifyCoursePublished("CS001");

        Mail mail = new Mail("motech@email.com", "admin@email.com", "Course Published", "Course CS001 Published");
        verify(emailSenderService).send(mail);
    }


}
