package org.motechproject.whp.mtraining.builder;

import org.motechproject.mtraining.domain.CourseUnitState;

import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.domain.Flag;
import org.motechproject.whp.mtraining.dto.ChapterDto;
import org.motechproject.whp.mtraining.dto.CoursePlanDto;
import org.motechproject.whp.mtraining.dto.LessonDto;
import org.motechproject.whp.mtraining.dto.ModuleDto;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Updater to re-validate and set the lesson in a Flag against the provided ModuleDto Structure for a given enrollee.
 * In cases where a valid lesson cannot be set, build the Flag with a valid Quiz.
 * @see FlagBuilder
 */

@Component
public class FlagLessonUpdater {

    private FlagBuilder courseFlagBuilder;

    @Autowired
    private MTrainingService mTrainingService;

    @Autowired
    private DtoFactoryService dtoFactoryService;

    @Autowired
    public FlagLessonUpdater(FlagBuilder flagBuilder) {
        this.courseFlagBuilder = flagBuilder;
    }


    /**
     * Given flag the API ensures that the current flag is valid and if not then updates the flag to a valid point
     * 1) If flag lesson does not exist, then build flag from the chapter
     * 2) If lesson is not active then set flag to next active lesson
     * 3) If no active lesson is left in the chapter then build flag from chapter quiz
     * 4) If no active quiz is left in the chapter then build flag from next active chapter
     * 5) If no next active chapter is left in the module then build flag from next active module
     * 5) If no next active module is left in the course then build course completion flag
     * @param flag
     * @param course
     * @param module
     * @param chapter
     * @return
     */
    public Flag update(Flag flag, CoursePlanDto course, ModuleDto module, ChapterDto chapter) {
        LessonDto lesson = dtoFactoryService.getLessonDtoById(flag.getLessonIdentifier().getUnitId());
        String externalId = flag.getExternalId();
        if (lesson == null) {
            return courseFlagBuilder.buildFlagFromFirstActiveMetadata(externalId, course, module, chapter);
        }
        if (lesson.getState() != CourseUnitState.Active) {
            LessonDto nextActiveLesson = BuilderHelper.getNextActive(lesson, chapter.getLessons());
            if (nextActiveLesson != null) {
                return courseFlagBuilder.buildFlagFrom(externalId, course, module, chapter, nextActiveLesson);
            }
            ChapterDto nextActiveChapter = BuilderHelper.getNextActive(chapter, module.getChapters());
            if (nextActiveChapter != null) {
                return courseFlagBuilder.buildFlagFromFirstActiveMetadata(externalId, course, module, nextActiveChapter);
            }
            return courseFlagBuilder.buildCourseCompletionFlag(externalId, course);
        }
        return courseFlagBuilder.buildFlagFrom(externalId, course, module, chapter, lesson);
    }

}
