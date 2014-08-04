package org.motechproject.whp.mtraining.builder;

import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Quiz;
import org.motechproject.mtraining.domain.CourseUnitState;
import org.motechproject.whp.mtraining.domain.Flag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Updater to re-validate and set the quiz in a Flag against the provided Course Structure for a given enrollee.
 * Switches Flag to next chapter if unable to set a quiz in the Flag for the given chapter.
 * @see FlagBuilder
 */

@Component
public class FlagQuizUpdater {

    private FlagBuilder courseFlagBuilder;

    @Autowired
    public FlagQuizUpdater(FlagBuilder flagBuilder) {
        this.courseFlagBuilder = flagBuilder;
    }

    /**
     * Given flag the API ensures that the current flag is valid and if not then updates the flag to a valid point
     * 1) If flag quiz does not exist, then build flag from the chapter
     * 2) If quiz is not active then set flag to next active chapter
     * 5) If no next active chapter is left in the module then build flag from next active module
     * 5) If no next active module is left in the course then build course completion flag
     * @param flag
     * @param course
     * @param chapter
     * @return
     */
    public Flag update(Flag flag, Course course, Chapter chapter) {
        Quiz quiz = chapter.getQuiz();
        String externalId = flag.getExternalId();
        
        if (quiz == null) {
            return courseFlagBuilder.buildFlagFromFirstActiveMetadata(externalId, course, chapter);
        }
        
        if (quiz.getState() != CourseUnitState.Active) {
            Chapter nextActiveChapter = BuilderHelper.getNextActive(chapter, course.getChapters());
            if (nextActiveChapter != null) {
                return courseFlagBuilder.buildFlagFromFirstActiveMetadata(externalId, course, nextActiveChapter);
            }
            return courseFlagBuilder.buildCourseCompletionFlag(externalId, course);
        }
        return courseFlagBuilder.buildFlagFromFirstActiveMetadata(externalId, course, chapter);
    }

}
