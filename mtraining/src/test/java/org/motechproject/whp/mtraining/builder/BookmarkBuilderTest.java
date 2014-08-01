package org.motechproject.whp.mtraining.builder;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.motechproject.mtraining.domain.*;

import java.util.Arrays;
import java.util.Objects;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;

public class BookmarkBuilderTest {

    @Test
    public void shouldBuildBookmarkFromLessonWhenBothLessonAndQuizArePresent() {
        Lesson activeLesson = new Lesson("ms001", CourseUnitState.Active, "aud001");
        Lesson inactiveLesson = new Lesson("ms002", CourseUnitState.Inactive, "aud002");
        Question question = new Question("ques001", "ans01");
        Quiz activeQuiz = new Quiz("quiz001", CourseUnitState.Active, "content", Arrays.asList(question), 100.0);
        Chapter chapter01 = new Chapter("ch001", CourseUnitState.Active, "desc", asList(activeLesson, inactiveLesson), activeQuiz);
        Course course01 = new CourseContentBuilder()
                .withChapters(asList(chapter01))
                .buildCourse();

        Bookmark bookmark = new BookmarkBuilder().buildBookmarkFromFirstActiveMetadata("roll001", course01, chapter01);

        assertThat(bookmark.getExternalId(), Is.is("roll001"));
        assertThat(bookmark.getLessonIdentifier(), Is.is(Objects.toString(activeLesson.getId())));
    }
}
