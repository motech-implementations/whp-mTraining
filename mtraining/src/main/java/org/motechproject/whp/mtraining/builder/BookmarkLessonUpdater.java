package org.motechproject.whp.mtraining.builder;

import org.motechproject.mtraining.domain.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Updater to re-validate and set the lesson in a Bookmark against the provided Course Structure for a given enrollee.
 * In cases where a valid lesson cannot be set, build the Bookmark with a valid Quiz.
 * @see org.motechproject.whp.mtraining.builder.BookmarkBuilder
 */

@Component
public class BookmarkLessonUpdater {

    private BookmarkBuilder courseBookmarkBuilder;

    @Autowired
    public BookmarkLessonUpdater(BookmarkBuilder bookmarkBuilder) {
        this.courseBookmarkBuilder = bookmarkBuilder;
    }


    /**
     * Given bookmark the API ensures that the current bookmark is valid and if not then updates the bookmark to a valid point
     * 1) If bookmark lesson does not exist, then build bookmark from the chapter
     * 2) If lesson is not active then set bookmark to next active lesson
     * 3) If no active lesson is left in the chapter then build bookmark from chapter quiz
     * 4) If no active quiz is left in the chapter then build bookmark from next active chapter
     * 5) If no next active chapter is left in the module then build bookmark from next active module
     * 5) If no next active module is left in the course then build course completion bookmark
     * @param bookmark
     * @param course
     * @param chapter
     * @return
     */
    public Bookmark update(Bookmark bookmark, Course course, Chapter chapter) {
        //Lesson lesson = chapter.getLesson(bookmark.getLessonIdentifier());
        Lesson lesson = chapter.getLessons().get(Integer.parseInt(bookmark.getLessonIdentifier()));
        String externalId = bookmark.getExternalId();
        if (lesson == null) {
            return courseBookmarkBuilder.buildBookmarkFromFirstActiveContent(externalId, course, chapter);
        }
        if (lesson.getState() != CourseUnitState.Active) {
            Lesson nextActiveLesson = BuilderHelper.getNextActive(lesson, chapter.getLessons());
            if (nextActiveLesson != null) {
                return courseBookmarkBuilder.buildBookmarkFrom(externalId, course, chapter, nextActiveLesson);
            }

//            if (chapterDto.hasActiveQuiz()) {
//                return courseBookmarkBuilder.buildBookmarkFrom(externalId, course, chapter, chapter.getQuiz());
//            }

            Chapter nextActiveChapter = BuilderHelper.getNextActive(chapter, course.getChapters());
            if (nextActiveChapter != null) {
                return courseBookmarkBuilder.buildBookmarkFromFirstActiveContent(externalId, course, nextActiveChapter);
            }
            
            return courseBookmarkBuilder.buildCourseCompletionBookmark(externalId, course);
        }
        return null;
        //return courseBookmarkBuilder.buildBookmarkFrom(externalId, course, chapter, lesson, bookmark.getDateModified());
    }

}
