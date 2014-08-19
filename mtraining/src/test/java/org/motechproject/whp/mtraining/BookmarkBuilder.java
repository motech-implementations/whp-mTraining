package org.motechproject.whp.mtraining;

import org.motechproject.mtraining.domain.*;
import org.motechproject.whp.mtraining.builder.FlagBuilder;
import org.motechproject.whp.mtraining.domain.Flag;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.web.domain.Bookmark;

public class BookmarkBuilder {

    private long courseId = new Course("New course", CourseUnitState.Active, "Content").getId();
    private long chapterId = new Chapter().getId();
    private long lessonId = new Lesson("New lesson", CourseUnitState.Active, "Content").getId();
    private Course course = new Course("New course", CourseUnitState.Active, "Content");
    private Chapter chapter = new Chapter();
    private Lesson lesson = new Lesson("New lesson", CourseUnitState.Active, "Content");
    private long quizId = new Quiz().getId();
    private String dateModified = ISODateTimeUtil.nowInTimeZoneUTC().toString();
    private String externalId = "RMD001";


    public BookmarkBuilder withDateModified(String dateModified) {
        this.dateModified = dateModified;
        return this;
    }

    public BookmarkBuilder withExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public Bookmark build() {
        return new Bookmark(courseId, chapterId, lessonId, quizId, dateModified);
    }

    public Flag buildFlag() {
        return new FlagBuilder().buildFlagFrom(null, course, chapter, lesson);
    }

    public BookmarkBuilder withCourse(long course) {
        this.courseId = course;
        return this;
    }

    public BookmarkBuilder withChapter(long chapter) {
        this.chapterId = chapter;
        return this;
    }

    public BookmarkBuilder withMessage(long lesson) {
        this.lessonId = lesson;
        return this;
    }

    public BookmarkBuilder withQuiz(long quiz) {
        this.quizId = quiz;
        return this;
    }
}
