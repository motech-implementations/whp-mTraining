package org.motechproject.whp.mtraining.web.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.util.ISODateTimeUtil;

import static org.apache.commons.lang.StringUtils.isBlank;

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

    //For JSON parsing
    public Bookmark() {
    }

    public Bookmark(ContentIdentifierDto courseIdentifierDto, ContentIdentifierDto moduleIdentifierDto, ContentIdentifierDto chapterIdentifierDto, ContentIdentifierDto messageIdentifierDto, ContentIdentifierDto quizIdentifierDto) {
        this.courseIdentifierDto = courseIdentifierDto;
        this.moduleIdentifierDto = moduleIdentifierDto;
        this.chapterIdentifierDto = chapterIdentifierDto;
        this.messageIdentifierDto = messageIdentifierDto;
        this.quizIdentifierDto = quizIdentifierDto;
        this.dateModified = ISODateTimeUtil.nowAsStringInTimeZoneUTC();
    }

    public Bookmark(ContentIdentifierDto courseIdentifierDto, ContentIdentifierDto moduleIdentifierDto, ContentIdentifierDto chapterIdentifierDto, ContentIdentifierDto messageIdentifierDto, ContentIdentifierDto quizIdentifierDto, String dateModified) {
        this.courseIdentifierDto = courseIdentifierDto;
        this.moduleIdentifierDto = moduleIdentifierDto;
        this.chapterIdentifierDto = chapterIdentifierDto;
        this.messageIdentifierDto = messageIdentifierDto;
        this.quizIdentifierDto = quizIdentifierDto;
        this.dateModified = dateModified;
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

    public boolean hasValidModifiedDate() {
        if (isBlank(dateModified)) {
            return false;
        }
        return ISODateTimeUtil.validate(dateModified);
    }
}
