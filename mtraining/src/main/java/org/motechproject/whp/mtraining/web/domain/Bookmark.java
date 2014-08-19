package org.motechproject.whp.mtraining.web.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_NODE;

public class Bookmark {


    @JsonProperty("course")
    private Long courseIdentifier;
    @JsonProperty("chapter")
    private Long chapterIdentifier;
    @JsonProperty("lesson")
    private Long lessonIdentifier;
    @JsonProperty("quiz")
    private Long quizIdentifier;

    @JsonProperty
    private String dateModified;

    //For JSON parsing
    public Bookmark() {
    }

    public Bookmark(Long courseIdentifier, Long chapterIdentifier,
                    Long lessonIdentifier, Long quizIdentifier, String dateModified) {
        this.courseIdentifier = courseIdentifier;
        this.chapterIdentifier = chapterIdentifier;
        this.lessonIdentifier = lessonIdentifier;
        this.quizIdentifier = quizIdentifier;
        this.dateModified = dateModified;
    }

    public Long getCourseIdentifierDto() {
        return courseIdentifier;
    }

    public Long getChapterIdentifierDto() {
        return chapterIdentifier;
    }

    public Long getMessageIdentifierDto() {
        return lessonIdentifier;
    }

    public Long getQuizIdentifierDto() {
        return quizIdentifier;
    }

    public String getDateModified() {
        return dateModified;
    }


    public List<ValidationError> validate() {
        List<ValidationError> validationErrors = newArrayList();
        if (courseIdentifier == null || courseIdentifier == 0)// || courseIdentifier.getContentId() == null || courseIdentifier.getVersion() == null)
            validationErrors.add(errorMessage(MISSING_NODE, "Course"));
        if (chapterIdentifier == null || courseIdentifier == 0)// || chapterIdentifier.getContentId() == null || chapterIdentifier.getVersion() == null)
            validationErrors.add(errorMessage(MISSING_NODE, "Chapter"));
        if (((lessonIdentifier == null || courseIdentifier == 0) && // || lessonIdentifier.getContentId() == null || lessonIdentifier.getVersion() == null) &&
                (quizIdentifier == null || courseIdentifier == 0)))// || quizIdentifierDto.getContentId() == null || quizIdentifierDto.getVersion() == null))
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
