package org.motechproject.whp.mtraining.domain;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.motechproject.mtraining.dto.AnswerDto;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.dto.QuestionDto;
import org.motechproject.mtraining.dto.QuizDto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CourseTest {

    @Test
    public void shouldTestCourseEquality() {
        UUID courseId = UUID.randomUUID();
        CoursePublicationAttempt coursePublicationAttempt = new CoursePublicationAttempt(courseId, 1, true);
        CoursePublicationAttempt sameCoursePublicationAttempt = new CoursePublicationAttempt(courseId, 1, true);
        CoursePublicationAttempt coursePublicationAttemptWithDifferentVersion = new CoursePublicationAttempt(courseId, 2, true);
        CoursePublicationAttempt publishedCourseWithDifferentStatePublicationAttempt = new CoursePublicationAttempt(courseId, 2, false);
        CoursePublicationAttempt courseWithDifferentCoursePublicationAttemptId = new CoursePublicationAttempt(UUID.randomUUID(), 1, true);

        assertTrue(coursePublicationAttempt.equals(sameCoursePublicationAttempt));
        assertFalse(coursePublicationAttempt.equals(null));
        assertFalse(coursePublicationAttempt.equals(courseWithDifferentCoursePublicationAttemptId));
        assertFalse(coursePublicationAttempt.equals(coursePublicationAttemptWithDifferentVersion));
        assertFalse(coursePublicationAttempt.equals(publishedCourseWithDifferentStatePublicationAttempt));
    }

    @Test
    public void shouldTestThatEqualCoursesHaveSameHashCodes() {
        UUID courseId = UUID.randomUUID();
        CoursePublicationAttempt coursePublicationAttempt = new CoursePublicationAttempt(courseId, 1, true);
        CoursePublicationAttempt sameCoursePublicationAttempt = new CoursePublicationAttempt(courseId, 1, true);
        assertThat(coursePublicationAttempt.hashCode(), Is.is(sameCoursePublicationAttempt.hashCode()));
    }

    @Test
    public void shouldRemoveInactiveContent() {
        Course cs001 = buildCourse();

        assertThat(cs001.getModules().size(), Is.is(2));

        cs001.removeInactiveContent();

        assertThat(cs001.getModules().size(), Is.is(1));

        Module module = cs001.getModules().get(0);
        assertThat(module.isActive(), Is.is(true));
        assertThat(module.getName(), Is.is("m001"));

        List<Chapter> chapters = module.getChapters();
        assertThat(chapters.size(), Is.is(2));

        Chapter chapter = chapters.get(0);
        assertThat(chapter.isActive(), Is.is(true));
        assertThat(chapter.getName(), Is.is("ch002"));

        List<Message> messages = chapter.getMessages();
        assertThat(messages.isEmpty(), Is.is(true));

        Quiz quiz = chapter.getQuiz();
        assertThat(quiz.isActive(), Is.is(true));

        List<Question> questions = quiz.getQuestions();
        assertThat(questions.size(), Is.is(1));

        Question question = questions.get(0);
        assertThat(question.isActive(), Is.is(true));
        assertThat(question.getName(), Is.is("q001"));

        Chapter secondChapter = chapters.get(1);
        assertThat(secondChapter.getName(), Is.is("ch003"));
        assertNull(secondChapter.getQuiz());

        List<Message> secondChapterMessages = secondChapter.getMessages();
        assertThat(secondChapterMessages.size(), Is.is(1));

        Message message = secondChapterMessages.get(0);
        assertThat(message.isActive(), Is.is(true));
        assertThat(message.getName(), Is.is("msg001"));
    }

    private Course buildCourse() {
        String createdBy = "author";

        MessageDto activeMessage = new MessageDto(true, "msg001", "aud01", "message desc", createdBy);
        MessageDto inactiveMessage = new MessageDto(false, "msg001", "aud01.wav", "message desc", createdBy);

        QuestionDto q001 = new QuestionDto(true, "q001", "ques desc", "ques-aud.wav", new AnswerDto("C", "correct-answer.wav"),
                Arrays.asList("A", "B", "C"), createdBy);
        QuestionDto q002 = new QuestionDto(false, "q002", "ques desc", "ques-aud.wav", new AnswerDto("C", "correct-answer.wav"),
                Arrays.asList("A", "B", "C"), createdBy);


        QuizDto quiz001 = new QuizDto(true, "quiz001", Arrays.asList(q002, q001), 1, 85l, createdBy);
        QuizDto inactiveQuiz = new QuizDto(false, "quiz002", Collections.<QuestionDto>emptyList(), 1, 85l, createdBy);

        ChapterDto ch001 = new ChapterDto(false, "ch001", "chapter description", createdBy, Arrays.asList(activeMessage), quiz001);
        ChapterDto ch002 = new ChapterDto(true, "ch002", "chapter description", createdBy, Arrays.asList(inactiveMessage), quiz001);
        ChapterDto ch003 = new ChapterDto(true, "ch003", "chapter description", createdBy, Arrays.asList(activeMessage), inactiveQuiz);

        ModuleDto mod001 = new ModuleDto(true, "m001", "module desc", createdBy, Arrays.asList(ch001, ch002, ch003));
        ModuleDto mod002 = new ModuleDto(false, "m002", "module desc", createdBy, Collections.<ChapterDto>emptyList());

        CourseDto cs001 = new CourseDto(true, "cs001", "Course Desc", createdBy, Arrays.asList(mod001, mod002));

        return new Course(cs001);
    }
}
