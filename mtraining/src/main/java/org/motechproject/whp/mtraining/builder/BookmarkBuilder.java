package org.motechproject.whp.mtraining.builder;

import org.joda.time.DateTime;
import org.motechproject.whp.mtraining.dto.*;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;
import org.springframework.stereotype.Component;

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
     * Build bookmark from the first active content of a course for the given parameter
     * @param externalId
     * @param courseDto
     * @return
     */
    public BookmarkDto buildBookmarkFromFirstActiveContent(String externalId, CourseDto courseDto) {
        ModuleDto moduleDto = courseDto.firstActiveModule();
        return buildBookmarkFromFirstActiveContent(externalId, courseDto, moduleDto);
    }

    /**
     * Build bookmark for a given enrollee from the first active content of a module in a given course
     * @param externalId
     * @param courseDto
     * @param moduleDto
     * @return
     */
    public BookmarkDto buildBookmarkFromFirstActiveContent(String externalId, CourseDto courseDto, ModuleDto moduleDto) {
        if (moduleDto == null) {
            return null;
        }
        return buildBookmarkFromFirstActiveContent(externalId, courseDto, moduleDto, moduleDto.findFirstActiveChapter());
    }


    /**
     * Build bookmark from the first active message of a chapter in a given module,course
     * If chapter does not have active message then try building  from  the chapter quiz
     * @param externalId
     * @param courseDto
     * @param moduleDto
     * @param chapterDto
     * @return
     */
    public BookmarkDto buildBookmarkFromFirstActiveContent(String externalId, CourseDto courseDto, ModuleDto moduleDto, ChapterDto chapterDto) {
        if (chapterDto == null) {
            return null;
        }
        MessageDto firstActiveMessage = chapterDto.findFirstActiveMessage();
        if (firstActiveMessage != null) {
            return buildBookmarkFrom(externalId, courseDto, moduleDto, chapterDto, firstActiveMessage);
        }
        return buildBookmarkFrom(externalId, courseDto, moduleDto, chapterDto, chapterDto.getQuiz());
    }


    /**
     * Build bookmark from the provided message in a given chapter,module,course
     * @param externalId
     * @param courseDto
     * @param moduleDto
     * @param chapterDto
     * @param messageDto
     * @return
     */
    public BookmarkDto buildBookmarkFrom(String externalId, CourseDto courseDto, ModuleDto moduleDto, ChapterDto chapterDto, MessageDto messageDto) {
        if (messageDto == null) {
            return null;
        }
        ContentIdentifierDto course = new ContentIdentifierDto(courseDto.getContentId(), courseDto.getVersion());
        ContentIdentifierDto module = new ContentIdentifierDto(moduleDto.getContentId(), moduleDto.getVersion());
        ContentIdentifierDto chapter = new ContentIdentifierDto(chapterDto.getContentId(), chapterDto.getVersion());
        ContentIdentifierDto message = new ContentIdentifierDto(messageDto.getContentId(), messageDto.getVersion());
        return new BookmarkDto(externalId, course, module, chapter, message, null, now());
    }

    /**
     * Build bookmark from the provided quiz in a given chapter,module,course
     * @param externalId
     * @param courseDto
     * @param moduleDto
     * @param chapterDto
     * @param quizDto
     * @return
     */
    public BookmarkDto buildBookmarkFrom(String externalId, CourseDto courseDto, ModuleDto moduleDto, ChapterDto chapterDto, QuizDto quizDto) {
        if (quizDto == null) {
            return null;
        }
        ContentIdentifierDto course = new ContentIdentifierDto(courseDto.getContentId(), courseDto.getVersion());
        ContentIdentifierDto module = new ContentIdentifierDto(moduleDto.getContentId(), moduleDto.getVersion());
        ContentIdentifierDto chapter = new ContentIdentifierDto(chapterDto.getContentId(), chapterDto.getVersion());
        ContentIdentifierDto quiz = new ContentIdentifierDto(quizDto.getContentId(), quizDto.getVersion());
        return new BookmarkDto(externalId, course, module, chapter, null, quiz, now());
    }


    /**
     * Build a bookmark that implies course completion
     * A course completed bookmark points only to the course with other values as null
     * @param externalId
     * @param courseDto
     * @return
     */
    public BookmarkDto buildCourseCompletionBookmark(String externalId, CourseDto courseDto) {
        return new BookmarkDto(externalId, courseDto.toContentIdentifierDto());
    }


    /**
     * Build bookmark from last active content of a given course
     * if a course has both active message and quiz , then build bookmark from the quiz
     * otherwise build bookmark from the last active message
     * @param externalId
     * @param courseDto
     * @return
     */
    public BookmarkDto buildBookmarkFromLastActiveContent(String externalId, CourseDto courseDto) {
        ModuleDto lastActiveModule = courseDto.lastActiveModule();
        ChapterDto lastActiveChapter = lastActiveModule.lastActiveChapter();
        MessageDto lastActiveMessage = lastActiveChapter.lastActiveMessage();
        if (lastActiveMessage != null) {
            if (lastActiveChapter.hasActiveQuiz()) {
                return buildBookmarkFrom(externalId, courseDto, lastActiveModule, lastActiveChapter, lastActiveChapter.getQuiz(), ISODateTimeUtil.nowInTimeZoneUTC());
            }
            return buildBookmarkFrom(externalId, courseDto, lastActiveModule, lastActiveChapter, lastActiveMessage, ISODateTimeUtil.nowInTimeZoneUTC());
        }
        return buildBookmarkFrom(externalId, courseDto, lastActiveModule, lastActiveChapter, lastActiveChapter.getQuiz(), ISODateTimeUtil.nowInTimeZoneUTC());
    }

    /**
     * Builds a bookmark from the given params with quiz as null
     * @param externalId
     * @param courseDto
     * @param moduleDto
     * @param chapterDto
     * @param messageDto
     * @param dateModified
     * @return
     */
    public BookmarkDto buildBookmarkFrom(String externalId, CourseDto courseDto, ModuleDto moduleDto, ChapterDto chapterDto,
                                         MessageDto messageDto, DateTime dateModified) {
        return new BookmarkDto(externalId, courseDto.toContentIdentifierDto(),
                moduleDto.toContentIdentifierDto(),
                chapterDto.toContentIdentifierDto(),
                messageDto.toContentIdentifierDto(), null, dateModified);
    }

    /**
     * Builds a bookmark from the given params with message as null
     * @param externalId
     * @param courseDto
     * @param moduleDto
     * @param chapterDto
     * @param quizDto
     * @param dateModified
     * @return
     */
    public BookmarkDto buildBookmarkFrom(String externalId, CourseDto courseDto, ModuleDto moduleDto, ChapterDto chapterDto,
                                         QuizDto quizDto, DateTime dateModified) {
        return new BookmarkDto(externalId, courseDto.toContentIdentifierDto(),
                moduleDto.toContentIdentifierDto(),
                chapterDto.toContentIdentifierDto(),
                null, quizDto.toContentIdentifierDto(), dateModified);
    }


    private DateTime now() {
        return ISODateTimeUtil.nowInTimeZoneUTC();
    }
}
