package org.motechproject.whp.mtraining;

import org.joda.time.DateTime;
import org.motechproject.mtraining.domain.*;
import org.motechproject.whp.mtraining.builder.FlagBuilder;
import org.motechproject.whp.mtraining.domain.Flag;
import org.motechproject.whp.mtraining.dto.ChapterDto;
import org.motechproject.whp.mtraining.dto.CoursePlanDto;
import org.motechproject.whp.mtraining.dto.LessonDto;
import org.motechproject.whp.mtraining.dto.ModuleDto;
import org.motechproject.whp.mtraining.dto.QuizDto;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.web.domain.Bookmark;
import org.motechproject.whp.mtraining.domain.ContentIdentifier;

import java.util.UUID;

public class BookmarkBuilder {

    private long courseId = new Course("New course", CourseUnitState.Active, "Content").getId();
    private long chapterId = new Chapter().getId();
    private long lessonId = new Lesson("New lesson", CourseUnitState.Active, "Content").getId();
    private long quizId = new Quiz().getId();
    private ModuleDto course = new ModuleDto(courseId, "New course", CourseUnitState.Active, DateTime.now(), DateTime.now());
    private ChapterDto chapter = new ChapterDto();
    private LessonDto lesson = new LessonDto(lessonId, "New lesson", CourseUnitState.Active, DateTime.now(), DateTime.now());
    private QuizDto quiz = new QuizDto();
    private CoursePlanDto coursePlanDto = new CoursePlanDto(1, "course plan dto", CourseUnitState.Active, DateTime.now(), DateTime.now());
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
        ContentIdentifier courseCI = (course.getContentId() == null) ? null : new ContentIdentifier(1, course.getContentId().toString());
        ContentIdentifier chapterCI = (chapter.getContentId() == null) ? null : new ContentIdentifier(1, chapter.getContentId().toString());
        ContentIdentifier lessonCI = (lesson.getContentId() == null) ? null : new ContentIdentifier(1, lesson.getContentId().toString());
        ContentIdentifier quizCI = (quiz.getContentId() == null) ? null : new ContentIdentifier(1, quiz.getContentId().toString());
        return new Bookmark(courseCI,
                chapterCI,
                lessonCI,
                quizCI,
                dateModified);
    }

    public Flag buildFlag() {
        return new FlagBuilder().buildFlagFrom(null, coursePlanDto, course, chapter, lesson);
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
