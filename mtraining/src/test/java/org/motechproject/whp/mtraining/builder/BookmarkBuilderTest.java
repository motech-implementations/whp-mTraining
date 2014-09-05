package org.motechproject.whp.mtraining.builder;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.motechproject.mtraining.domain.*;
import org.motechproject.whp.mtraining.domain.CoursePlan;
import org.motechproject.whp.mtraining.dto.*;

import java.util.Arrays;
import java.util.Objects;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;

public class BookmarkBuilderTest {

    @Test
    public void shouldBuildBookmarkFromLessonWhenBothLessonAndQuizArePresent() {
        LessonDto activeLesson = new LessonDto(1, "ms001", CourseUnitState.Active, null, null);
        LessonDto inactiveLesson = new LessonDto(2, "ms002", CourseUnitState.Inactive, null, null);
        QuestionDto question = new QuestionDto("ques001", null, null, null, null, null);
        QuizDto activeQuiz = new QuizDto(4, "quiz001", CourseUnitState.Active, null, null, 50, Arrays.asList(question));
        ChapterDto chapter01 = new ChapterDto(5, "ch001", "desc", CourseUnitState.Active, "desc", null, null, asList(activeLesson, inactiveLesson), null, activeQuiz);
        ModuleDto course01 = new CourseContentBuilder()
                .withChapters(asList(chapter01))
                .buildCourse();

        Bookmark bookmark = new Bookmark("roll001", Objects.toString(course01.getId()), Objects.toString(chapter01.getId()),
                Objects.toString(chapter01.getLessons().get(0).getId()), null);
        assertThat(bookmark.getExternalId(), Is.is("roll001"));
        assertThat(bookmark.getLessonIdentifier(), Is.is(Objects.toString(activeLesson.getId())));
    }
}
