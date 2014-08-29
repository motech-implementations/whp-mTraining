package org.motechproject.whp.mtraining.builder;

import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.CourseUnitState;
import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.mtraining.service.impl.MTrainingServiceImpl;
import org.motechproject.whp.mtraining.domain.Flag;
import org.motechproject.whp.mtraining.dto.ChapterDto;
import org.motechproject.whp.mtraining.dto.ModuleDto;
import org.motechproject.whp.mtraining.service.ContentOperationService;
import org.motechproject.whp.mtraining.service.DtoFactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Updater to re-validate and set the chapter in a Flag against the provided ModuleDto Structure for a given enrollee.
 * In cases where the {@link org.motechproject.whp.mtraining.domain.Flag} already refers to a valid chapter, then jump to {@link FlagLessonUpdater}
 * or {@link FlagQuizUpdater} depending on whether the {@link org.motechproject.whp.mtraining.domain.Flag} is for a lesson or a quiz.
 * @see FlagBuilder
 */

@Component
public class FlagChapterUpdater {
    private FlagBuilder flagBuilder;
    private FlagLessonUpdater flagLessonUpdater;
    private FlagQuizUpdater flagQuizUpdater;

    @Autowired
    private MTrainingService mTrainingService;

    @Autowired
    private DtoFactoryService dtoFactoryService;

    @Autowired
    ContentOperationService contentOperationService;

    @Autowired
    public FlagChapterUpdater(FlagBuilder flagBuilder, FlagLessonUpdater flagLessonUpdater,
                              FlagQuizUpdater flagQuizUpdater) {
        this.flagBuilder = flagBuilder;
        this.flagLessonUpdater = flagLessonUpdater;
        this.flagQuizUpdater = flagQuizUpdater;
    }

    /**
     * Given flag the API ensures that the current flag is valid and if not then updates the flag to a valid point
     * 1) If flag chapter does not exist, then build flag from the first active chapter of the module
     * 2) If chapter is not active then set flag to next active chapter
     * 3) If no active chapter is left in the module then build flag from next active module
     * 4) If no active module is left in the course then build course completion flag
     * 5) If flag is for lesson then update flag lesson
     * 6) If flag is for quiz then update flag quiz
     * 7) If flag does not have either quiz or lesson then build flag from first active lesson/quiz of chapter
     * @param flag
     * @param course
     * @return
     */
    public Flag update(Flag flag, ModuleDto course) {
        ChapterDto chapter = dtoFactoryService.getChapterDtoById(Integer.parseInt(flag.getChapterIdentifier()));
        String externalId = flag.getExternalId();
        if (chapter == null) {
            return flagBuilder.buildFlagFromFirstActiveMetadata(externalId, course);
        }
        if (chapter.getState() != CourseUnitState.Active) {
            ChapterDto nextActiveChapter = BuilderHelper.getNextActive(chapter, course.getChapters());
            if (nextActiveChapter != null) {
                return flagBuilder.buildFlagFromFirstActiveMetadata(externalId, course, nextActiveChapter);
            }
            return flagBuilder.buildCourseCompletionFlag(externalId, course);
        }
        if (flag.getLessonIdentifier() != null) {
            return flagLessonUpdater.update(flag, course, chapter);
        }
        if (flag.getQuizIdentifier() != null) {
            return flagQuizUpdater.update(flag, course, chapter);
        }
        return flagBuilder.buildFlagFromFirstActiveMetadata(externalId, course, chapter);
    }
}
