package org.motechproject.whp.mtraining.builder;

import org.motechproject.mtraining.domain.CourseUnitState;
import org.motechproject.whp.mtraining.domain.Flag;
import org.motechproject.whp.mtraining.dto.ChapterDto;
import org.motechproject.whp.mtraining.dto.LessonDto;
import org.motechproject.whp.mtraining.dto.ModuleDto;
import org.motechproject.whp.mtraining.dto.QuizDto;
import org.motechproject.whp.mtraining.service.ContentOperationService;
import org.motechproject.whp.mtraining.domain.ContentIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Factory class to create/modify a {@link org.motechproject.whp.mtraining.domain.Flag} for an enrollee from a {@link org.motechproject.mtraining.domain.Course}.
 * Used to create Initial flag in {@link org.motechproject.whp.mtraining.service.impl.FlagServiceImpl}.
 * Also Used by Updaters in package {@link org.motechproject.whp.mtraining.builder}.
 * It is assumed that an active course/module/chapter passed as an argument will have at least one active child content.
 * This class will not throw any exception if an active content does not have any active child content.
 */

@Component
public class FlagBuilder {

    @Autowired
    private ContentOperationService contentOperationService;

    /**
     * Build flag from the first active chapter of a course for the given parameter
     * @param externalId
     * @param course
     * @return
     */
    public Flag buildFlagFromFirstActiveMetadata(String externalId, ModuleDto course) {
        ChapterDto chapter = BuilderHelper.findFirstActive(course.getChapters());
        return buildFlagFromFirstActiveMetadata(externalId, course, chapter);
    }

    //TODO Add method with module
    /**
     * Build flag from the first active Lesson of a chapter in a given course
     * If chapter does not have active lesson then try building  from  the chapter quiz
     * @param externalId
     * @param course
     * @param chapter
     * @return
     */
    public Flag buildFlagFromFirstActiveMetadata(String externalId, ModuleDto course, ChapterDto chapter) {
        if (chapter == null) {
            return null;
        }
        LessonDto firstActiveLesson = BuilderHelper.findFirstActive(chapter.getLessons());
        if (firstActiveLesson != null) {
            return buildFlagFrom(externalId, course, chapter, firstActiveLesson);
        }
        return null;
    }

    /**
     * Build flag from the provided lesson in a given chapter,course
     * @param externalId
     * @param course
     * @param chapter
     * @param lesson
     * @return
     */
    public Flag buildFlagFrom(String externalId, ModuleDto course, ChapterDto chapter, LessonDto lesson) {
        if (lesson == null) {
            return null;
        }
        return new Flag(externalId, contentOperationService.codeContentIdentifierIntoString(new ContentIdentifier(course.getId(), course.getContentId())),
                null, contentOperationService.codeContentIdentifierIntoString(new ContentIdentifier(chapter.getId(), chapter.getContentId())),
                contentOperationService.codeContentIdentifierIntoString(new ContentIdentifier(lesson.getId(), lesson.getContentId())), null, null);
    }

    /**
     * Build flag from the provided quiz in a given chapter,course
     * @param externalId
     * @param course
     * @param chapter
     * @param quiz
     * @return
     */
    public Flag buildFlagFrom(String externalId, ModuleDto course, ChapterDto chapter, QuizDto quiz) {
        if (quiz == null) {
            return null;
        }
        return new Flag(externalId, contentOperationService.codeContentIdentifierIntoString(new ContentIdentifier(course.getId(), course.getContentId())), null,
                contentOperationService.codeContentIdentifierIntoString(new ContentIdentifier(chapter.getId(), chapter.getContentId())),
                null, new ContentIdentifier(quiz.getId(), quiz.getContentId()), null);
    }

    /**
     * Build a flag that implies course completion
     * A course completed flag points only to the course with other values as null
     * @param externalId
     * @param course
     * @return
     */
    public Flag buildCourseCompletionFlag(String externalId, ModuleDto course) {
        return new Flag(externalId, Objects.toString(course.getId()), null, null, null, null, null);
    }

    /**
     * Build flag from last active metadata of a given course
     * if a course has both active lesson and quiz, then build flag from the quiz
     * otherwise build flag from the last active lesson
     * @param externalId
     * @param course
     * @return
     */
    public Flag buildFlagFromLastActiveMetadata(String externalId, ModuleDto course) {
        ChapterDto lastActiveChapter = BuilderHelper.findLastActive(course.getChapters());
        LessonDto lastActiveLesson = BuilderHelper.findLastActive(lastActiveChapter.getLessons());
        if (lastActiveLesson != null) {
            if (lastActiveChapter.getQuiz() != null && lastActiveChapter.getQuiz().getState() == CourseUnitState.Active){
                buildFlagFrom(externalId, course, lastActiveChapter, lastActiveChapter.getQuiz());
            }
            return buildFlagFrom(externalId, course, lastActiveChapter, lastActiveLesson);
        }
        return buildFlagFrom(externalId, course, lastActiveChapter, lastActiveChapter.getQuiz());
    }
}
