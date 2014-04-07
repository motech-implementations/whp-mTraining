package org.motechproject.whp.mtraining.web.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.util.ISODateTimeUtil;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.mtraining.constants.CourseStatus.UNKNOWN;
import static org.motechproject.mtraining.constants.CourseStatus.enumFor;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_COURSE_STATUS;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_NODE;

public class Bookmark {


    @JsonProperty("course")
    private ContentIdentifierDto courseIdentifierDto;
    @JsonProperty("module")
    private ContentIdentifierDto moduleIdentifierDto;
    @JsonProperty("chapter")
    private ContentIdentifierDto chapterIdentifierDto;
    @JsonProperty("message")
    private ContentIdentifierDto messageIdentifierDto;
    @JsonProperty("quiz")
    private ContentIdentifierDto quizIdentifierDto;

    @JsonProperty
    private String dateModified;

    @JsonProperty
    private String courseStatus;

    //For JSON parsing
    public Bookmark() {
    }

    public Bookmark(ContentIdentifierDto courseIdentifierDto, ContentIdentifierDto moduleIdentifierDto, ContentIdentifierDto chapterIdentifierDto,
                    ContentIdentifierDto messageIdentifierDto, ContentIdentifierDto quizIdentifierDto, String dateModified, String courseStatus) {
        this.courseIdentifierDto = courseIdentifierDto;
        this.moduleIdentifierDto = moduleIdentifierDto;
        this.chapterIdentifierDto = chapterIdentifierDto;
        this.messageIdentifierDto = messageIdentifierDto;
        this.quizIdentifierDto = quizIdentifierDto;
        this.dateModified = dateModified;
        this.courseStatus = courseStatus;
    }

    public Bookmark(ContentIdentifierDto courseIdentifierDto, ContentIdentifierDto moduleIdentifierDto, ContentIdentifierDto chapterIdentifierDto,
                    ContentIdentifierDto messageIdentifierDto, ContentIdentifierDto quizIdentifierDto, String courseStatus) {
        this(courseIdentifierDto, moduleIdentifierDto, chapterIdentifierDto, messageIdentifierDto, quizIdentifierDto, now(), courseStatus);
    }

    private static String now() {
        return ISODateTimeUtil.nowAsStringInTimeZoneUTC();
    }

    public ContentIdentifierDto getCourseIdentifierDto() {
        return courseIdentifierDto;
    }

    public ContentIdentifierDto getModuleIdentifierDto() {
        return moduleIdentifierDto;
    }

    public ContentIdentifierDto getChapterIdentifierDto() {
        return chapterIdentifierDto;
    }

    public ContentIdentifierDto getMessageIdentifierDto() {
        return messageIdentifierDto;
    }

    public ContentIdentifierDto getQuizIdentifierDto() {
        return quizIdentifierDto;
    }

    public String getDateModified() {
        return dateModified;
    }

    public String getCourseStatus() {
        return courseStatus;
    }

    public List<ValidationError> validate() {
        List<ValidationError> validationErrors = newArrayList();
        if (courseIdentifierDto == null || courseIdentifierDto.getContentId() == null || chapterIdentifierDto.getVersion() == null)
            validationErrors.add(errorMessage(MISSING_NODE, "Course"));
        if (moduleIdentifierDto == null || moduleIdentifierDto.getContentId() == null || moduleIdentifierDto.getVersion() == null)
            validationErrors.add(errorMessage(MISSING_NODE, "Module"));
        if (chapterIdentifierDto == null || chapterIdentifierDto.getContentId() == null || chapterIdentifierDto.getVersion() == null)
            validationErrors.add(errorMessage(MISSING_NODE, "Chapter"));
        if ((messageIdentifierDto == null || messageIdentifierDto.getContentId() == null || messageIdentifierDto.getVersion() == null) &&
                (quizIdentifierDto == null || quizIdentifierDto.getContentId() == null || quizIdentifierDto.getVersion() == null))
            validationErrors.add(new ValidationError(MISSING_NODE.getCode(), "Quiz or Message should be present"));
        if (isBlank(dateModified) || !ISODateTimeUtil.validate(dateModified))
            validationErrors.add(new ValidationError(ResponseStatus.INVALID_DATE_TIME));
        if (isBlank(courseStatus) || enumFor(courseStatus).equals(UNKNOWN))
            validationErrors.add(new ValidationError(INVALID_COURSE_STATUS));
        return validationErrors;
    }

    private ValidationError errorMessage(ResponseStatus status, String content) {
        String message = status.getMessage().concat(" for: " + content);
        return new ValidationError(status.getCode(), message);
    }
}
