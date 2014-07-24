package org.motechproject.whp.mtraining.builder;

import org.motechproject.mtraining.domain.Bookmark;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.CourseUnitState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Updater to re-validate and set the chapter in a Bookmark against the provided Course Structure for a given enrollee.
 * In cases where the {@link org.motechproject.mtraining.domain.Bookmark} already refers to a valid chapter, then jump to {@link org.motechproject.mtraining.builder.BookmarkMessageUpdater}
 * or {@link org.motechproject.mtraining.builder.BookmarkQuizUpdater} depending on whether the {@link org.motechproject.mtraining.domain.Bookmark} is for a message or a quiz.
 * @see org.motechproject.mtraining.builder.BookmarkBuilder
 */

@Component
public class BookmarkChapterUpdater {
    private BookmarkBuilder bookmarkBuilder;
    private BookmarkLessonUpdater bookmarkLessonUpdater;
    private BookmarkQuizUpdater bookmarkQuizUpdater;

    @Autowired
    public BookmarkChapterUpdater(BookmarkBuilder bookmarkBuilder, BookmarkLessonUpdater bookmarkLessonUpdater,
                                  BookmarkQuizUpdater bookmarkQuizUpdater) {
        this.bookmarkBuilder = bookmarkBuilder;
        this.bookmarkLessonUpdater = bookmarkLessonUpdater;
        this.bookmarkQuizUpdater = bookmarkQuizUpdater;
    }

    /**
     * Given bookmark the API ensures that the current bookmark is valid and if not then updates the bookmark to a valid point
     * 1) If bookmark chapter does not exist, then build bookmark from the first active chapter of the module
     * 2) If chapter is not active then set bookmark to next active chapter
     * 3) If no active chapter is left in the module then build bookmark from next active module
     * 4) If no active module is left in the course then build course completion bookmark
     * 5) If bookmark is for message then update bookmark message
     * 6) If bookmark is for quiz then update bookmark quiz
     * 7) If bookmark does not have either quiz or message then build bookmark from first active message/quiz of chapter
     * @param bookmark
     * @param course
     * @return
     */
    public Bookmark update(Bookmark bookmark, Course course) {
        //Chapter chapter = course.getChapters(Integer.parseInt(bookmark.getChapterIdentifier());
        Chapter chapter = course.getChapters().get(Integer.parseInt(bookmark.getChapterIdentifier()));
        String externalId = bookmark.getExternalId();
        if (chapter == null) {
            return bookmarkBuilder.buildBookmarkFromFirstActiveContent(externalId, course);
        }
        if (chapter.getState() == CourseUnitState.Active) {
            Chapter nextActiveChapter = BuilderHelper.getNextActive(chapter, course.getChapters());
            if (nextActiveChapter != null) {
                return bookmarkBuilder.buildBookmarkFromFirstActiveContent(externalId, course, nextActiveChapter);
            }
            return bookmarkBuilder.buildCourseCompletionBookmark(externalId, course);
        }
        //TODO Uncomment or remove
        //if (bookmark.isForMessage()) {
        //    return bookmarkLessonUpdater.update(bookmark, course, chapter);
        //}
        //if (bookmark.isForQuiz()) {
        //    return bookmarkQuizUpdater.update(bookmark, course, chapter);
        //}
        return bookmarkBuilder.buildBookmarkFromFirstActiveContent(externalId, course, chapter);
    }
}
