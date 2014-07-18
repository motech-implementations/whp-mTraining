package org.motechproject.whp.mtraining.builder;

import org.motechproject.whp.mtraining.domain.Bookmark;
import org.motechproject.whp.mtraining.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Updater to re-validate and set the message in a Bookmark against the provided Course Structure for a given enrollee.
 * In cases where a valid message cannot be set, build the Bookmark with a valid Quiz.
 * @see org.motechproject.mtraining.builder.BookmarkBuilder
 */

@Component
public class BookmarkMessageUpdater {

    private BookmarkBuilder courseBookmarkBuilder;

    @Autowired
    public BookmarkMessageUpdater(BookmarkBuilder bookmarkBuilder) {
        this.courseBookmarkBuilder = bookmarkBuilder;
    }


    /**
     * Given bookmark the API ensures that the current bookmark is valid and if not then updates the bookmark to a valid point
     * 1) If bookmark message does not exist, then build bookmark from the chapter
     * 2) If message is not active then set bookmark to next active message
     * 3) If no active message is left in the chapter then build bookmark from chapter quiz
     * 4) If no active quiz is left in the chapter then build bookmark from next active chapter
     * 5) If no next active chapter is left in the module then build bookmark from next active module
     * 5) If no next active module is left in the course then build course completion bookmark
     * @param bookmark
     * @param courseDto
     * @param moduleDto
     * @param chapterDto
     * @return
     */
    public BookmarkDto update(Bookmark bookmark, CourseDto courseDto, ModuleDto moduleDto, ChapterDto chapterDto) {
        MessageDto messageDto = chapterDto.getMessage(bookmark.getMessage().getContentId());
        String externalId = bookmark.getExternalId();
        if (messageDto == null) {
            return courseBookmarkBuilder.buildBookmarkFromFirstActiveContent(externalId, courseDto, moduleDto, chapterDto);
        }
        if (!messageDto.isActive()) {
            MessageDto nextActiveMessageDto = chapterDto.getNextActiveMessageAfter(messageDto.getContentId());
            if (nextActiveMessageDto != null) {
                return courseBookmarkBuilder.buildBookmarkFrom(externalId, courseDto, moduleDto, chapterDto, nextActiveMessageDto);
            }

            if (chapterDto.hasActiveQuiz()) {
                return courseBookmarkBuilder.buildBookmarkFrom(externalId, courseDto, moduleDto, chapterDto, chapterDto.getQuiz());
            }

            ChapterDto nextActiveChapterDto = moduleDto.getNextActiveChapterAfter(chapterDto.getContentId());
            if (nextActiveChapterDto != null) {
                return courseBookmarkBuilder.buildBookmarkFromFirstActiveContent(externalId, courseDto, moduleDto, nextActiveChapterDto);
            }

            ModuleDto nextActiveModuleDto = courseDto.getNextActiveModuleAfter(moduleDto.getContentId());
            if (nextActiveModuleDto != null) {
                return courseBookmarkBuilder.buildBookmarkFromFirstActiveContent(externalId, courseDto, nextActiveModuleDto);
            }

            return courseBookmarkBuilder.buildCourseCompletionBookmark(externalId, courseDto);
        }
        return courseBookmarkBuilder.buildBookmarkFrom(externalId, courseDto, moduleDto, chapterDto, messageDto, bookmark.getDateModified());
    }

}
