package org.motechproject.whp.mtraining.builder;

import org.motechproject.mtraining.domain.CourseUnitState;
import org.motechproject.whp.mtraining.domain.Flag;
import org.motechproject.whp.mtraining.dto.*;
import org.motechproject.whp.mtraining.service.ContentOperationService;
import org.motechproject.whp.mtraining.domain.ContentIdentifier;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
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

    /**
     * Build flag from the first active module of a course for the given parameter
     *
     * @param externalId
     * @param course
     * @return
     */
    public Flag buildFlagFromFirstActiveMetadata(String externalId, CoursePlanDto course) {
        ModuleDto module = BuilderHelper.findFirstActive(course.getModules());
        return buildFlagFromFirstActiveMetadata(externalId, course, module);
    }

    /**
     * Build flag from the first active chapter of a module for the given parameter
     *
     * @param externalId
     * @param course
     * @return
     */
    public Flag buildFlagFromFirstActiveMetadata(String externalId, CoursePlanDto course, ModuleDto module) {
        ChapterDto chapter = BuilderHelper.findFirstActive(module.getChapters());
        return buildFlagFromFirstActiveMetadata(externalId, course, module, chapter);
    }

    //TODO Add method with module

    /**
     * Build flag from the first active Lesson of a chapter in a given module
     * If chapter does not have active lesson then try building  from  the chapter quiz
     *
     * @param externalId
     * @param course
     * @param chapter
     * @return
     */
    public Flag buildFlagFromFirstActiveMetadata(String externalId, CoursePlanDto course, ModuleDto module, ChapterDto chapter) {
        if (chapter == null) {
            return null;
        }
        LessonDto firstActiveLesson = BuilderHelper.findFirstActive(chapter.getLessons());
        if (firstActiveLesson != null) {
            return buildFlagFrom(externalId, course, module, chapter, firstActiveLesson);
        }
        return null;
    }

    /**
     * Build flag from the provided lesson in a given chapter,course
     *
     * @param externalId
     * @param course
     * @param chapter
     * @param lesson
     * @return
     */
    public Flag buildFlagFrom(String externalId, CoursePlanDto course, ModuleDto module, ChapterDto chapter, LessonDto lesson) {
        if (lesson == null) {
            return null;
        }
        ContentIdentifier courseCI = (course.getContentId() == null) ? null : new ContentIdentifier(course.getId(), course.getContentId());
        ContentIdentifier moduleCI = (module.getContentId() == null) ? null : new ContentIdentifier(module.getId(), module.getContentId());
        ContentIdentifier chapterCI = (chapter.getContentId() == null) ? null : new ContentIdentifier(chapter.getId(), chapter.getContentId());
        ContentIdentifier lessonCI = (lesson.getContentId() == null) ? null : new ContentIdentifier(lesson.getId(), lesson.getContentId());
        return new Flag(externalId,
                courseCI,
                moduleCI,
                chapterCI,
                lessonCI,
                null);
    }

    /**
     * Build flag from the provided quiz in a given chapter,course
     *
     * @param externalId
     * @param course
     * @param chapter
     * @param quiz
     * @return
     */
    public Flag buildFlagFrom(String externalId, CoursePlanDto course, ModuleDto module, ChapterDto chapter, QuizDto quiz) {
        if (quiz == null) {
            return null;
        }
        return new Flag(externalId, new ContentIdentifier(course.getId(), course.getContentId()),
                new ContentIdentifier(module.getId(), module.getContentId()),
                new ContentIdentifier(chapter.getId(), chapter.getContentId()),
                null, new ContentIdentifier(quiz.getId(), quiz.getContentId()));
    }

    /**
     * Build a flag that implies course completion
     * A course completed flag points only to the course with other values as null
     *
     * @param externalId
     * @param course
     * @return
     */
    public Flag buildCourseCompletionFlag(String externalId, CoursePlanDto course) {
        return new Flag(externalId, new ContentIdentifier(course.getId(), course.getContentId()), null, null, null, null);
    }

    /**
     * Build flag from last active metadata of a given course
     * if a course has both active lesson and quiz, then build flag from the quiz
     * otherwise build flag from the last active lesson
     *
     * @param externalId
     * @param course
     * @return
     */
    public Flag buildFlagFromLastActiveMetadata(String externalId, CoursePlanDto course) {
        ModuleDto lastActiveModule = BuilderHelper.findLastActive(course.getModules());
        ChapterDto lastActiveChapter = BuilderHelper.findLastActive(lastActiveModule.getChapters());
        LessonDto lastActiveLesson = BuilderHelper.findLastActive(lastActiveChapter.getLessons());
        if (lastActiveLesson != null) {
            if (lastActiveChapter.getQuiz() != null && lastActiveChapter.getQuiz().getState() == CourseUnitState.Active) {
                buildFlagFrom(externalId, course, lastActiveModule, lastActiveChapter, lastActiveChapter.getQuiz());
            }
            return buildFlagFrom(externalId, course, lastActiveModule, lastActiveChapter, lastActiveLesson);
        }
        return buildFlagFrom(externalId, course, lastActiveModule, lastActiveChapter, lastActiveChapter.getQuiz());
    }
}
