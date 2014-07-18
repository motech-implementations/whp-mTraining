package org.motechproject.whp.mtraining.builder;

import org.motechproject.whp.mtraining.domain.Bookmark;
import org.motechproject.whp.mtraining.dto.BookmarkDto;
import org.motechproject.whp.mtraining.dto.ChapterDto;
import org.motechproject.whp.mtraining.dto.CourseDto;
import org.motechproject.whp.mtraining.dto.ModuleDto;
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
    private BookmarkMessageUpdater bookmarkMessageUpdater;
    private BookmarkQuizUpdater bookmarkQuizUpdater;

    @Autowired
    public BookmarkChapterUpdater(BookmarkBuilder bookmarkBuilder, BookmarkMessageUpdater bookmarkMessageUpdater,
                                  BookmarkQuizUpdater bookmarkQuizUpdater) {
        this.bookmarkBuilder = bookmarkBuilder;
        this.bookmarkMessageUpdater = bookmarkMessageUpdater;
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
     * @param courseDto
     * @param moduleDto
     * @return
     */
    public BookmarkDto update(Bookmark bookmark, CourseDto courseDto, ModuleDto moduleDto) {
        ChapterDto chapterDto = moduleDto.getChapter(bookmark.getChapter().getContentId());
        String externalId = bookmark.getExternalId();
        if (chapterDto == null) {
            return bookmarkBuilder.buildBookmarkFromFirstActiveContent(externalId, courseDto, moduleDto);
        }
        if (!chapterDto.isActive()) {
            ChapterDto nextActiveChapterDto = moduleDto.getNextActiveChapterAfter(chapterDto.getContentId());
            if (nextActiveChapterDto != null) {
                return bookmarkBuilder.buildBookmarkFromFirstActiveContent(externalId, courseDto, moduleDto, nextActiveChapterDto);
            }

            ModuleDto nextActiveModuleDto = courseDto.getNextActiveModuleAfter(moduleDto.getContentId());
            if (nextActiveModuleDto != null) {
                return bookmarkBuilder.buildBookmarkFromFirstActiveContent(externalId, courseDto, nextActiveModuleDto);
            }

            return bookmarkBuilder.buildCourseCompletionBookmark(externalId, courseDto);

        }
        if (bookmark.isForMessage()) {
            return bookmarkMessageUpdater.update(bookmark, courseDto, moduleDto, chapterDto);
        }
        if (bookmark.isForQuiz()) {
            return bookmarkQuizUpdater.update(bookmark, courseDto, moduleDto, chapterDto);
        }

        return bookmarkBuilder.buildBookmarkFromFirstActiveContent(externalId, courseDto, moduleDto, chapterDto);

    }
}
