package org.motechproject.whp.mtraining.builder;

import org.motechproject.whp.mtraining.domain.Bookmark;
import org.motechproject.whp.mtraining.dto.BookmarkDto;
import org.motechproject.whp.mtraining.dto.CourseDto;
import org.motechproject.whp.mtraining.dto.ModuleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Updater to re-validate and set the module in a Bookmark against the provided Course Structure for a given enrollee.
 * In cases where the {@link org.motechproject.mtraining.domain.Bookmark} already refers to a valid module, then jump to {@link org.motechproject.mtraining.builder.BookmarkChapterUpdater}.
 * @see org.motechproject.mtraining.builder.BookmarkBuilder
 */

@Component
public class BookmarkModuleUpdater {

    private BookmarkBuilder courseBookmarkBuilder;
    private BookmarkChapterUpdater bookmarkChapterUpdater;

    @Autowired
    public BookmarkModuleUpdater(BookmarkBuilder bookmarkBuilder, BookmarkChapterUpdater bookmarkChapterUpdater) {
        this.courseBookmarkBuilder = bookmarkBuilder;
        this.bookmarkChapterUpdater = bookmarkChapterUpdater;
    }


    /**
     * Given bookmark the API ensures that the current bookmark is valid and if not then updates the bookmark to a valid point
     * 1) If bookmark module does not exist, then return null
     * 2) If module is not active then set bookmark to next active module
     * 3) If module is not active and no other active module left,then build course completion bookmark
     * 4) If module is active but bookmark does not point to a chapter then build bookmark from first active chapter of the module
     * 5) otherwise update bookmark chapter
     * @param bookmark
     * @param courseDto
     * @return
     */
    public BookmarkDto update(Bookmark bookmark, CourseDto courseDto) {
        ModuleDto moduleDto = courseDto.getModule(bookmark.getModule().getContentId());
        if (moduleDto == null) {
            return null;
        }
        if (!moduleDto.isActive()) {
            ModuleDto nextActiveModuleDto = courseDto.getNextActiveModuleAfter(moduleDto.getContentId());
            if (nextActiveModuleDto != null) {
                return courseBookmarkBuilder.buildBookmarkFromFirstActiveContent(bookmark.getExternalId(), courseDto, nextActiveModuleDto);
            }
            return courseBookmarkBuilder.buildCourseCompletionBookmark(bookmark.getExternalId(), courseDto);
        }

        if (!bookmark.hasChapter()) {
            return courseBookmarkBuilder.buildBookmarkFromFirstActiveContent(bookmark.getExternalId(), courseDto, moduleDto);
        }

        return bookmarkChapterUpdater.update(bookmark, courseDto, moduleDto);
    }
}
