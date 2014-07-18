//package org.motechproject.whp.mtraining.builder;
//
//import org.joda.time.DateTime;
//import org.motechproject.whp.mtraining.domain.Bookmark;
//import org.motechproject.whp.mtraining.domain.ContentIdentifier;
//import org.motechproject.whp.mtraining.dto.BookmarkDto;
//import org.motechproject.whp.mtraining.dto.ContentIdentifierDto;
//import org.motechproject.whp.mtraining.dto.CourseDto;
//import org.motechproject.whp.mtraining.dto.EnrolleeCourseProgressDto;
//import org.motechproject.whp.mtraining.service.CourseService;
//import org.motechproject.whp.mtraining.util.ISODateTimeUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * Updater to re-validate and set the course in a Bookmark against the provided Course Structure for a given enrollee.
// * In cases where the {@link org.motechproject.mtraining.domain.Bookmark} already refers to a valid course, then jump to {@link org.motechproject.mtraining.builder.BookmarkModuleUpdater}
// * @see org.motechproject.mtraining.builder.BookmarkBuilder
// */
//
//@Component
//public class CourseProgressUpdater {
//
//    private CourseService courseService;
//    private BookmarkModuleUpdater bookmarkModuleUpdater;
//    private BookmarkBuilder bookmarkBuilder;
//
//    @Autowired
//    public CourseProgressUpdater(CourseService courseService, BookmarkModuleUpdater bookmarkModuleUpdater, BookmarkBuilder bookmarkBuilder) {
//        this.courseService = courseService;
//        this.bookmarkModuleUpdater = bookmarkModuleUpdater;
//        this.bookmarkBuilder = bookmarkBuilder;
//    }
//
//    /**
//     * Given enrolleeCourseProgressDto the API ensures that the current bookmark is valid and if not then updates the bookmark to a valid point
//     * 1) If course is not valid or inactive, then return null
//     * 2) If current bookmark does not point to a module, then set bookmark to first active message/quiz (of first active chapter) of first active module
//     * 3) If current bookmark has a module, update the bookmark (if required) by delegating the job to BookmarkModuleUpdater
//     * 4) If moduleUpdater returns null (which can happen when the existing bookmark is invalid or course does not have any active content), then null is returned
//     * 5) In case the current bookmark points to content which has now been inactivated and rest of the content is also inactive then course is marked as complete.
//     * @param enrolleeCourseProgressDto
//     * @return
//     */
//    public EnrolleeCourseProgressDto update(EnrolleeCourseProgressDto enrolleeCourseProgressDto) {
//        BookmarkDto bookmarkDto = enrolleeCourseProgressDto.getBookmarkDto();
//        Bookmark bookmark = toBookmark(bookmarkDto);
//        CourseDto courseDto = courseService.getLatestPublishedCourse(bookmark.getCourse().getContentId());
//        if (courseDto == null) {
//            return null;
//        }
//        if (!bookmark.hasModule()) {
//            BookmarkDto firstActiveBookmark = bookmarkBuilder.buildBookmarkFromFirstActiveContent(bookmark.getExternalId(), courseDto);
//            if (firstActiveBookmark == null) {
//                return null;
//            }
//            enrolleeCourseProgressDto.setBookmarkDto(firstActiveBookmark);
//            return enrolleeCourseProgressDto;
//        }
//        BookmarkDto updatedBookmark = bookmarkModuleUpdater.update(bookmark, courseDto);
//        if (updatedBookmark == null) {
//            return null;
//        }
//
//        if (isCourseCompleted(updatedBookmark)) {
//            BookmarkDto lastBookmarkOfCourse = bookmarkBuilder.buildBookmarkFromLastActiveContent(enrolleeCourseProgressDto.getExternalId(), courseDto);
//            enrolleeCourseProgressDto.setBookmarkDto(lastBookmarkOfCourse);
//            enrolleeCourseProgressDto.markComplete();
//            return enrolleeCourseProgressDto;
//        }
//        enrolleeCourseProgressDto.setBookmarkDto(updatedBookmark);
//        return enrolleeCourseProgressDto;
//    }
//
//    private boolean isCourseCompleted(BookmarkDto bookmarkDto) {
//        return bookmarkDto.getModule() == null && bookmarkDto.getChapter() == null;
//    }
//
//    private Bookmark toBookmark(BookmarkDto bookmark) {
//        String externalId = bookmark.getExternalId();
//        ContentIdentifierDto course = bookmark.getCourse();
//        ContentIdentifierDto module = bookmark.getModule();
//        ContentIdentifierDto chapter = bookmark.getChapter();
//        ContentIdentifierDto message = bookmark.getMessage();
//        ContentIdentifierDto quiz = bookmark.getQuiz();
//        DateTime dateModified = ISODateTimeUtil.parseWithTimeZoneUTC(bookmark.getDateModified());
//        return new Bookmark(externalId, createContentIdentifier(course), createContentIdentifier(module), createContentIdentifier(chapter), createContentIdentifier(message),
//                createContentIdentifier(quiz), dateModified);
//    }
//
//    private ContentIdentifier createContentIdentifier(ContentIdentifierDto contentIdentifierDto) {
//        if (contentIdentifierDto == null) {
//            return null;
//        }
//        return new ContentIdentifier(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion());
//    }
//}
