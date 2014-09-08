package org.motechproject.whp.mtraining.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mtraining.domain.Bookmark;
import org.motechproject.mtraining.domain.MdsEntity;
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
@JsonIgnoreProperties({"id", "creationDate", "modificationDate", "creator", "owner", "modifiedBy"})
public class Flag extends MdsEntity {

    @Field
    @JsonIgnore
    private String externalId;

    @Field
    @JsonProperty("course")
    private ContentIdentifier courseIdentifier;

    @Field
    @JsonProperty("module")
    private ContentIdentifier moduleIdentifier;

    @Field
    @JsonProperty("chapter")
    private ContentIdentifier chapterIdentifier;

    @Field
    @JsonProperty("message")
    private ContentIdentifier lessonIdentifier;

    @Field
    @JsonProperty("quiz")
    private ContentIdentifier quizIdentifier;

    @Field
    private String dateModified;

    /**
     * Open, extensible map object field to let implementation store more details relevant
     * to flag
     */
    @Field
    @JsonIgnore
    private Map<String, Object> progress;

    public Flag() { }

    public Flag(String externalId, ContentIdentifier courseIdentifier, ContentIdentifier moduleIdentifier, ContentIdentifier chapterIdentifier,
                ContentIdentifier lessonIdentifier, ContentIdentifier quizIdentifier) {
        this.externalId = externalId;
        this.courseIdentifier = courseIdentifier;
        this.moduleIdentifier = moduleIdentifier;
        this.chapterIdentifier = chapterIdentifier;
        this.lessonIdentifier = lessonIdentifier;
        this.quizIdentifier = quizIdentifier;
    }

    public Flag(ContentIdentifier courseIdentifier, ContentIdentifier moduleIdentifier, ContentIdentifier chapterIdentifier,
                ContentIdentifier lessonIdentifier, ContentIdentifier quizIdentifier, String dateModified) {
        this.dateModified = dateModified;
        this.courseIdentifier = courseIdentifier;
        this.moduleIdentifier = moduleIdentifier;
        this.chapterIdentifier = chapterIdentifier;
        this.lessonIdentifier = lessonIdentifier;
        this.quizIdentifier = quizIdentifier;
    }

    public ContentIdentifier getModuleIdentifier() {
        return moduleIdentifier;
    }

    public void setModuleIdentifier(ContentIdentifier moduleIdentifier) {
        this.moduleIdentifier = moduleIdentifier;
    }

    public ContentIdentifier getQuizIdentifier() {
        return quizIdentifier;
    }

    public void setQuizIdentifier(ContentIdentifier quizIdentifier) {
        this.quizIdentifier = quizIdentifier;
    }

    public ContentIdentifier getChapterIdentifier() {
        return chapterIdentifier;
    }

    public void setChapterIdentifier(ContentIdentifier chapterIdentifier) {
        this.chapterIdentifier = chapterIdentifier;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public void setCourseIdentifier(ContentIdentifier courseIdentifier) {
        this.courseIdentifier = courseIdentifier;
    }

    public ContentIdentifier getCourseIdentifier() {
        return courseIdentifier;
    }

    public ContentIdentifier getLessonIdentifier() {
        return lessonIdentifier;
    }

    public void setLessonIdentifier(ContentIdentifier lessonIdentifier) {
        this.lessonIdentifier = lessonIdentifier;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
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
        if (getDateModified() == null || isBlank(getDateModified().toString()) || !ISODateTimeUtil.validate(getDateModified().toString()))
            validationErrors.add(new ValidationError(ResponseStatus.INVALID_DATE_TIME));
        return validationErrors;
    }

    private ValidationError errorMessage(ResponseStatus status, String content) {
        String message = status.getMessage().concat(" for: " + content);
        return new ValidationError(status.getCode(), message);
    }

}