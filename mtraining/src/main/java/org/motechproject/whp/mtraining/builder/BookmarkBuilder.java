package org.motechproject.whp.mtraining.builder;

import org.motechproject.mtraining.domain.*;
import org.motechproject.mtraining.domain.Bookmark;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Factory class to create/modify a {@link org.motechproject.mtraining.domain.Bookmark} for an enrollee from a {@link org.motechproject.mtraining.domain.Course}.
 * Used to create Initial bookmark in {@link org.motechproject.mtraining.service.impl.BookmarkServiceImpl}.
 * Also Used by Updaters in package {@link org.motechproject.whp.mtraining.builder}.
 * It is assumed that an active course/chapter passed as an argument will have at least one active child content.
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
    public Bookmark buildBookmarkFromFirstActiveMetadata(String externalId, Course course) {
        Chapter chapter = BuilderHelper.findFirstActive(course.getChapters());
        return buildBookmarkFromFirstActiveMetadata(externalId, course, chapter);
    }

    /**
     * Build bookmark from the first active Lesson of a chapter in a given course
     * If chapter does not have active lesson then try building  from  the chapter quiz
     * @param externalId
     * @param course
     * @param chapter
     * @return
     */
    public Bookmark buildBookmarkFromFirstActiveMetadata(String externalId, Course course, Chapter chapter) {
        if (chapter == null) {
            return null;
        }
        Lesson firstActiveLesson = BuilderHelper.findFirstActive(chapter.getLessons());
        if (firstActiveLesson != null) {
            return buildBookmarkFrom(externalId, course, chapter, firstActiveLesson);
        }
        return null;
    }

    /**
     * Build bookmark from the provided lesson in a given chapter,course
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
     * Build a bookmark that implies course completion
     * A course completed bookmark points only to the course with other values as null
     * @param externalId
     * @param course
     * @return
     */
    public Bookmark buildCourseCompletionBookmark(String externalId, Course course) {
        return new Bookmark(externalId, Objects.toString(course.getId()), null, null, null);
    }

    /**
     * Build bookmark from last active metadata of a given course
     * if a course has both active lesson and quiz, then build bookmark from the quiz
     * otherwise build bookmark from the last active lesson
     * @param externalId
     * @param course
     * @return
     */
    public Bookmark buildBookmarkFromLastActiveMetadata(String externalId, Course course) {
        Chapter lastActiveChapter = BuilderHelper.findLastActive(course.getChapters());
        Lesson lastActiveLesson = BuilderHelper.findLastActive(lastActiveChapter.getLessons());
        if (lastActiveLesson != null) {
            return buildBookmarkFrom(externalId, course, lastActiveChapter, lastActiveLesson);
        }
        return null;
    }
}
