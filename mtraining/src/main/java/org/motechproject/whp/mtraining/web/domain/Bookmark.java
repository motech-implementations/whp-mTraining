package org.motechproject.whp.mtraining.web.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.whp.mtraining.domain.ContentIdentifier;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_NODE;

public class Bookmark {


    @JsonProperty("course")
    private ContentIdentifier courseIdentifier;
    @JsonProperty("chapter")
    private ContentIdentifier chapterIdentifier;
    @JsonProperty("lesson")
    private ContentIdentifier lessonIdentifier;
    @JsonProperty("quiz")
    private ContentIdentifier quizIdentifier;

    @JsonProperty
    private String dateModified;

    //For JSON parsing
    public Bookmark() {
    }

    public Bookmark(ContentIdentifier courseIdentifier, ContentIdentifier chapterIdentifier,
                    ContentIdentifier lessonIdentifier, ContentIdentifier quizIdentifier, String dateModified) {
        this.courseIdentifier = courseIdentifier;
        this.chapterIdentifier = chapterIdentifier;
        this.lessonIdentifier = lessonIdentifier;
        this.quizIdentifier = quizIdentifier;
        this.dateModified = dateModified;
    }

    public ContentIdentifier getCourseIdentifier() {
        return courseIdentifier;
    }

    public ContentIdentifier getChapterIdentifier() {
        return chapterIdentifier;
    }

    public ContentIdentifier getMessageIdentifier() {
        return lessonIdentifier;
    }

    public ContentIdentifier getQuizIdentifier() {
        return quizIdentifier;
    }

    public String getDateModified() {
        return dateModified;
    }


    public List<ValidationError> validate() {
        List<ValidationError> validationErrors = newArrayList();
        if (courseIdentifier == null || courseIdentifier.getContentId() == null || courseIdentifier.getVersion() == 0)
            validationErrors.add(errorMessage(MISSING_NODE, "Course"));
        if (chapterIdentifier == null || chapterIdentifier.getContentId() == null || chapterIdentifier.getVersion() == 0)
            validationErrors.add(errorMessage(MISSING_NODE, "Chapter"));
        if ((lessonIdentifier == null || lessonIdentifier.getContentId() == null || lessonIdentifier.getVersion() == 0) &&
                (quizIdentifier == null || quizIdentifier.getContentId() == null || quizIdentifier.getVersion() == 0))
            validationErrors.add(new ValidationError(MISSING_NODE.getCode(), "Quiz or Message should be present"));
        if (isBlank(dateModified) || !ISODateTimeUtil.validate(dateModified))
            validationErrors.add(new ValidationError(ResponseStatus.INVALID_DATE_TIME));
        return validationErrors;
    }

    private ValidationError errorMessage(ResponseStatus status, String content) {
        String message = status.getMessage().concat(" for: " + content);
        return new ValidationError(status.getCode(), message);
    }
}
