package org.motechproject.whp.mtraining.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mtraining.domain.Bookmark;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.motechproject.whp.mtraining.web.domain.ValidationError;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_NODE;

/**
 * Flag object to store the progress for the user
 *
 * Note : A Flag will always point to either one of Lesson or Quiz and never both.
 * If the value of the lesson is set then that of quiz will be NULL and vice-versa
 */
@Entity
public class Flag extends Bookmark {

    /**
     * module identifier
     */
    @Field
    private String moduleIdentifier;

    /**
     * quiz identifier
     */
    @Field
    private String quizIdentifier;

    /**
     * Open, extensible map object field to let implementation store more details relevant
     * to flag
     */
    @Field
    private Map<String, Object> progress;

    public Flag(String externalId, String courseIdentifier, String moduleIdentifier, String chapterIdentifier,
                String lessonIdentifier, String quizIdentifier, Map<String, Object> progress) {
        super(externalId, courseIdentifier,chapterIdentifier,lessonIdentifier, progress);
        this.moduleIdentifier = moduleIdentifier;
        this.quizIdentifier = quizIdentifier;
    }

    public String getModuleIdentifier() {
        return moduleIdentifier;
    }

    public void setModuleIdentifier(String moduleIdentifier) {
        this.moduleIdentifier = moduleIdentifier;
    }

    public String getQuizIdentifier() {
        return quizIdentifier;
    }

    public void setQuizIdentifier(String quizIdentifier) {
        this.quizIdentifier = quizIdentifier;
    }

    public List<ValidationError> validate() {
        List<ValidationError> validationErrors = newArrayList();
        if (getCourseIdentifier() == null)
            validationErrors.add(errorMessage(MISSING_NODE, "Course"));
        if (moduleIdentifier == null)
            validationErrors.add(errorMessage(MISSING_NODE, "Module"));
        if (getChapterIdentifier() == null)
            validationErrors.add(errorMessage(MISSING_NODE, "Chapter"));
        if ((getLessonIdentifier() == null && quizIdentifier == null))
            validationErrors.add(new ValidationError(MISSING_NODE.getCode(), "Quiz or Lesson should be present"));
        if (getModificationDate() == null || isBlank(getModificationDate().toString()) || !ISODateTimeUtil.validate(getModificationDate().toString()))
            validationErrors.add(new ValidationError(ResponseStatus.INVALID_DATE_TIME));
        return validationErrors;
    }

    private ValidationError errorMessage(ResponseStatus status, String content) {
        String message = status.getMessage().concat(" for: " + content);
        return new ValidationError(status.getCode(), message);
    }
}