package org.motechproject.whp.mtraining;

import org.motechproject.mtraining.domain.*;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.web.domain.Bookmark;

public class BookmarkBuilder {

    private long course = new Course("New course", CourseUnitState.Active, "Content").getId();
    private long chapter = new Chapter().getId();
    private long lesson = new Lesson("New lesson", CourseUnitState.Active, "Content").getId();
    private long quiz = new Quiz().getId();
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
        return new Bookmark(course, chapter, lesson, quiz, dateModified);
    }

    public BookmarkBuilder withCourse(long course) {
        this.course = course;
        return this;
    }

    public BookmarkBuilder withChapter(long chapter) {
        this.chapter = chapter;
        return this;
    }

    public BookmarkBuilder withMessage(long lesson) {
        this.lesson = lesson;
        return this;
    }

    public BookmarkBuilder withQuiz(long quiz) {
        this.quiz = quiz;
        return this;
    }
}
