package org.motechproject.whp.mtraining.builder;

import org.joda.time.DateTime;
import org.motechproject.mtraining.domain.*;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Factory class to create/modify a {@link org.motechproject.mtraining.dto.BookmarkDto} for an enrollee from a {@link org.motechproject.mtraining.dto.CourseDto}.
 * Used to create Initial bookmark in {@link org.motechproject.mtraining.service.impl.BookmarkServiceImpl}.
 * Also Used by Updaters in package {@link org.motechproject.mtraining.builder}.
 * It is assumed that an active course/module/chapter passed as an argument will have at least one active child content.
 * This class will not throw any exception if an active content does not have any active child content.
 */

@Component
public class BookmarkBuilder {


    /**
     * Build bookmark from the first active chapter of a course for the given parameter
     * @param externalId
     * @param course
     * @return
     */
    public Bookmark buildBookmarkFromFirstActiveContent(String externalId, Course course) {
        Chapter chapter = BuilderHelper.findFirstActive(course.getChapters());
        return buildBookmarkFromFirstActiveContent(externalId, course, chapter);
    }

    /**
     * Build bookmark from the first active Lesson of a chapter in a given course
     * If chapter does not have active lesson then try building  from  the chapter quiz
     * @param externalId
     * @param course
     * @param chapter
     * @return
     */
    public Bookmark buildBookmarkFromFirstActiveContent(String externalId, Course course, Chapter chapter) {
        if (chapter == null) {
            return null;
        }
        Lesson firstActiveLesson = BuilderHelper.findFirstActive(chapter.getLessons());
        if (firstActiveLesson != null) {
            return buildBookmarkFrom(externalId, course, chapter, firstActiveLesson);
        }
        return buildBookmarkFrom(externalId, course, chapter, chapter.getQuiz());
    }


    /**
     * Build bookmark from the provided message in a given chapter,course
     * @param externalId
     * @param course
     * @param chapter
     * @param lesson
     * @return
     */
    public Bookmark buildBookmarkFrom(String externalId, Course course, Chapter chapter, Lesson lesson) {
        if (lesson == null) {
            return null;
        }
        return new Bookmark(externalId, Objects.toString(course.getId()), Objects.toString(chapter.getId()), Objects.toString(lesson.getId()), null);
    }

    /**
     * Build bookmark from the provided quiz in a given chapter,course
     * @param externalId
     * @param course
     * @param chapter
     * @param quiz
     * @return
     */
    public Bookmark buildBookmarkFrom(String externalId, Course course, Chapter chapter, Quiz quiz) {
        if (quiz == null) {
            return null;
        }
        return new Bookmark(externalId, Objects.toString(course.getId()), Objects.toString(chapter.getId()), quiz.getName(), null);
    }


    /**
     * Build a bookmark that implies course completion
     * A course completed bookmark points only to the course with other values as null
     * @param externalId
     * @param course
     * @return
     */
    public Bookmark buildCourseCompletionBookmark(String externalId, Course course) {
        return new Bookmark();
        //return new Bookmark(externalId, course.toContentIdentifierDto());
    }


    /**
     * Build bookmark from last active content of a given course
     * if a course has both active message and quiz , then build bookmark from the quiz
     * otherwise build bookmark from the last active message
     * @param externalId
     * @param course
     * @return
     */
    public Bookmark buildBookmarkFromLastActiveContent(String externalId, Course course) {
        Chapter lastActiveChapter = BuilderHelper.findLastActive(course.getChapters());
        Lesson lastActiveLesson = BuilderHelper.findLastActive(lastActiveChapter.getLessons());
        if (lastActiveLesson != null) {
//            if (lastActiveChapter.hasActiveQuiz()) {
                return buildBookmarkFrom(externalId, course, lastActiveChapter, lastActiveChapter.getQuiz(), ISODateTimeUtil.nowInTimeZoneUTC());
//            }
//            return buildBookmarkFrom(externalId, course, lastActiveChapter, lastActiveMessage, ISODateTimeUtil.nowInTimeZoneUTC());
        }
        return buildBookmarkFrom(externalId, course, lastActiveChapter, lastActiveChapter.getQuiz(), ISODateTimeUtil.nowInTimeZoneUTC());
    }

    /**
     * Builds a bookmark from the given params with quiz as null
     * @param externalId
     * @param course
     * @param chapter
     * @param lesson
     * @param dateModified
     * @return
     */
    public Bookmark buildBookmarkFrom(String externalId, Course course, Chapter chapter, Lesson lesson, DateTime dateModified) {
        //return new Bookmark(externalId, Objects.toString(course.getId()),Objects.toString(chapter.getId()),Objects.toString(lesson.getId()), dateModified);
        return new Bookmark(externalId, Objects.toString(course.getId()), Objects.toString(chapter.getId()), Objects.toString(lesson.getId()), null);
    }

    /**
     * Builds a bookmark from the given params with message as null
     * @param externalId
     * @param course
     * @param chapter
     * @param quiz
     * @param dateModified
     * @return
     */
    public Bookmark buildBookmarkFrom(String externalId, Course course, Chapter chapter, Quiz quiz, DateTime dateModified) {
        //return new Bookmark(externalId, Objects.toString(course.getId()),Objects.toString(chapter.getId()), quiz.getName(), dateModified);
        return new Bookmark(externalId, Objects.toString(course.getId()), Objects.toString(chapter.getId()), quiz.getName(), null);
    }
}
