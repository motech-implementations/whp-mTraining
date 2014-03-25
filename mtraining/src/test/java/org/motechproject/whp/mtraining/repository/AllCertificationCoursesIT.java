package org.motechproject.whp.mtraining.repository;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.whp.mtraining.CertificationCourseBuilder;
import org.motechproject.whp.mtraining.domain.CertificationCourse;
import org.motechproject.whp.mtraining.domain.Chapter;
import org.motechproject.whp.mtraining.domain.Message;
import org.motechproject.whp.mtraining.domain.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testWHPmTrainingApplicationContext.xml")
public class AllCertificationCoursesIT {

    @Autowired
    private AllCertificationCourses allCertificationCourses;

    @Test
    public void shouldAddACertificateCourseStructure() {

        CertificationCourse certificationCourse = new CertificationCourseBuilder().withName("NA001").withMessageAudioFile("hello.wav").build();

        assertThat(allCertificationCourses.all().size(), Is.is(0));

        allCertificationCourses.add(certificationCourse);

        List<CertificationCourse> allCourses = allCertificationCourses.all();
        assertThat(allCourses.size(), Is.is(1));

        CertificationCourse persistedCourse = allCourses.get(0);
        assertThat(persistedCourse.getName(), Is.is("NA001"));
        List<Module> modules = persistedCourse.getModules();
        assertThat(modules.size(), Is.is(1));
        Module module = modules.get(0);
        List<Chapter> chapters = module.getChapters();
        assertThat(chapters.size(), Is.is(1));
        Chapter chapter = chapters.get(0);
        List<Message> messages = chapter.getMessages();
        assertThat(messages.size(), Is.is(1));
        assertThat(messages.get(0).getAudioFileName(), Is.is("hello.wav"));
    }

    @Before
    public void deleteAll() {
        allCertificationCourses.deleteAll();
    }

}
