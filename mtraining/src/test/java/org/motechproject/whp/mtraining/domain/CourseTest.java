package org.motechproject.whp.mtraining.domain;

import org.junit.Test;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.CourseUnitState;
import org.motechproject.mtraining.domain.Lesson;
import org.motechproject.mtraining.domain.Question;
import org.motechproject.mtraining.domain.Quiz;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CourseTest {

    @Test
    public void shouldTestCourseEquality() {
        long courseId = 1234567L;
        CoursePublicationAttempt coursePublicationAttempt = new CoursePublicationAttempt(courseId, true);
        CoursePublicationAttempt sameCoursePublicationAttempt = new CoursePublicationAttempt(courseId, true);
        CoursePublicationAttempt publishedCourseWithDifferentStatePublicationAttempt = new CoursePublicationAttempt(courseId, false);
        CoursePublicationAttempt courseWithDifferentCoursePublicationAttemptId = new CoursePublicationAttempt(7654321L, true);

        assertTrue(coursePublicationAttempt.equals(sameCoursePublicationAttempt));
        assertFalse(coursePublicationAttempt.equals(null));
        assertFalse(coursePublicationAttempt.equals(courseWithDifferentCoursePublicationAttemptId));
        assertFalse(coursePublicationAttempt.equals(publishedCourseWithDifferentStatePublicationAttempt));
    }

    @Test
    public void shouldRemoveInactiveContent() {
//        CoursePlan cp001 = buildCourse();
//
//        assertThat(cp001.getModules().size(), Is.is(2));
//
//        //TODO Uncomment or remove test
//        //cp001.removeInactiveContent();
//
//        assertThat(cp001.getModules().size(), Is.is(1));
//
//        Course course = cp001.getModules().get(0);
//        assertThat(course.getName(), Is.is(CourseUnitState.Active));
//        assertThat(course.getName(), Is.is("c001"));
//
//        List<Chapter> chapters = course.getChapters();
//        assertThat(chapters.size(), Is.is(2));
//
//        Chapter chapter = chapters.get(0);
//        assertThat(chapter.getState(), Is.is(CourseUnitState.Active));
//        assertThat(chapter.getName(), Is.is("ch002"));
//
//        List<Lesson> lessons = chapter.getLessons();
//        assertThat(lessons.isEmpty(), Is.is(true));
//
//        Quiz quiz = chapter.getQuiz();
//        assertThat(quiz.getState(), Is.is(CourseUnitState.Active));
//
//        List<Question> questions = quiz.getQuestions();
//        assertThat(questions.size(), Is.is(1));
//
//        Question question = questions.get(0);
//        assertThat(question.getQuestion(), Is.is("q001"));
//
//        Chapter secondChapter = chapters.get(1);
//        assertThat(secondChapter.getName(), Is.is("ch003"));
//        assertNull(secondChapter.getQuiz());
//
//        List<Lesson> secondChapterLessons = secondChapter.getLessons();
//        assertThat(secondChapterLessons.size(), Is.is(1));
//
//        Lesson lesson = secondChapterLessons.get(0);
//        assertThat(lesson.getState(), Is.is(CourseUnitState.Active));
//        assertThat(lesson.getName(), Is.is("les001"));
    }

    private CoursePlan buildCourse() {

        Lesson activeLesson = new Lesson("les001", CourseUnitState.Active, "aud01");
        Lesson inactiveLesson = new Lesson("les001", CourseUnitState.Inactive, "aud01.wav");

        Question q001 = new Question("q001", "correct-answer.wav");
        Question q002 = new Question("q002", "correct-answer.wav");

        Quiz quiz001 = new Quiz("quiz001", CourseUnitState.Active, "quiz file", Arrays.asList(q002, q001), 85.0);
        Quiz inactiveQuiz = new Quiz("quiz002", CourseUnitState.Inactive, "quiz file", Collections.<Question>emptyList(), 85.0);

        Chapter ch001 = new Chapter("ch001", CourseUnitState.Inactive,"chapter file", Arrays.asList(activeLesson), quiz001);
        Chapter ch002 = new Chapter("ch002", CourseUnitState.Active, "chapter file", Arrays.asList(inactiveLesson), quiz001);
        Chapter ch003 = new Chapter("ch003", CourseUnitState.Active, "chapter file", Arrays.asList(activeLesson), inactiveQuiz);

        Course crs001 = new Course("c001", CourseUnitState.Active, "course file", Arrays.asList(ch001, ch002, ch003));
        Course crs002 = new Course("c002", CourseUnitState.Inactive, "course file", Collections.<Chapter>emptyList());

        CoursePlan cp001 = new CoursePlan("cp001", CourseUnitState.Active, "course plan file", Arrays.asList(crs001, crs002));

        return cp001;
    }
}
