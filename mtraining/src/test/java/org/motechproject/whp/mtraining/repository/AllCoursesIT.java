package org.motechproject.whp.mtraining.repository;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.whp.mtraining.CourseBuilder;
import org.motechproject.whp.mtraining.domain.Answer;
import org.motechproject.whp.mtraining.domain.Course;
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
public class AllCoursesIT {

    @Autowired
    private AllCourses allCourses;

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

        Course course = new CourseBuilder().withName("NA001").withMessageAudioFile("hello.wav").build();

        assertThat(allCourses.all().size(), Is.is(0));

        allCourses.add(course);

        List<Course> allCourses = this.allCourses.all();
        assertThat(allCourses.size(), Is.is(1));

        Course persistedCourse = allCourses.get(0);
        assertThat(persistedCourse.getName(), Is.is("NA001"));
        assertThat(persistedCourse.isActive(), Is.is(true));
        List<Module> modules = persistedCourse.getModules();
        assertThat(modules.size(), Is.is(1));
        Module module = modules.get(0);

        List<Chapter> chapters = module.getChapters();
        assertThat(chapters.size(), Is.is(1));
        Chapter chapter = chapters.get(0);

        Quiz quiz = chapter.getQuiz();
        assertThat(quiz, IsNull.notNullValue());

        assertThat(quiz.getPassPercentage(), Is.is(100l));
        assertThat(quiz.getNumberOfQuestions(), Is.is(2));

        assertNotNull(quiz.getQuestions());
        Question question = quiz.getQuestions().get(0);
        assertThat(question.getAnswerOptions(), Is.is("A,B"));

        Answer answer = question.getAnswer();
        assertThat(answer, IsNull.notNullValue());
        assertThat(answer.getCorrectOption(), Is.is("A"));

        List<Message> messages = chapter.getMessages();
        assertThat(messages.size(), Is.is(1));
        Message message = messages.get(0);
        assertThat(message.getAudioFileName(), Is.is("hello.wav"));
        assertThat(message.isActive(), Is.is(true));
    }

    @Before
    public void deleteAll() {
        allTestMessages.deleteAll();
        allTestQuestions.deleteAll();
        allTestChapters.deleteAll();
        allTestQuizzes.deleteAll();
        allTestModules.deleteAll();
        allCourses.deleteAll();
    }

}
