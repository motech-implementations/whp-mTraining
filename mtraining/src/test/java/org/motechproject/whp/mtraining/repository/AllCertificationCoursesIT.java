package org.motechproject.whp.mtraining.repository;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.whp.mtraining.CertificationCourseBuilder;
import org.motechproject.whp.mtraining.domain.Answer;
import org.motechproject.whp.mtraining.domain.CertificationCourse;
import org.motechproject.whp.mtraining.domain.Chapter;
import org.motechproject.whp.mtraining.domain.Message;
import org.motechproject.whp.mtraining.domain.Module;
import org.motechproject.whp.mtraining.domain.Question;
import org.motechproject.whp.mtraining.domain.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testWHPmTrainingApplicationContext.xml")
public class AllCertificationCoursesIT {

    @Autowired
    private AllCertificationCourses allCertificationCourses;

    @Autowired
    private AllTestModules allTestModules;

    @Autowired
    private AllTestChapters allTestChapters;

    @Autowired
    private AllTestMessages allTestMessages;

    @Autowired
    private AllTestQuizzes allTestQuizzes;

    @Autowired
    private AllTestQuestions allTestQuestions;


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

        Quiz quiz = chapter.getQuiz();
        assertThat(quiz, IsNull.notNullValue());

        assertThat(quiz.getPassPercentage(), Is.is(100l));
        assertThat(quiz.getNumberOfQuestions(), Is.is(1));

        assertNotNull(quiz.getQuestions());
        Question question = quiz.getQuestions().get(0);
        assertThat(question.getAnswerOptions(), Is.is("A,B"));

        Answer answer = question.getAnswer();
        assertThat(answer, IsNull.notNullValue());
        assertThat(answer.getCorrectOption(), Is.is("A"));

        List<Message> messages = chapter.getMessages();
        assertThat(messages.size(), Is.is(1));
        assertThat(messages.get(0).getAudioFileName(), Is.is("hello.wav"));
    }

    @Before
    public void deleteAll() {
        allTestMessages.deleteAll();
        allTestQuestions.deleteAll();
        allTestChapters.deleteAll();
        allTestQuizzes.deleteAll();
        allTestModules.deleteAll();
        allCertificationCourses.deleteAll();
    }

}
