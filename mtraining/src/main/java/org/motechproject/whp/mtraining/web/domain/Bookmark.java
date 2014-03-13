package org.motechproject.whp.mtraining.web.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.mtraining.dto.ContentIdentifierDto;

public class Bookmark {


    @JsonProperty("course")
    private ContentIdentifierDto courseIdentifierDto;
    @JsonProperty("module")
    private ContentIdentifierDto moduleIdentifierDto;
    @JsonProperty("chapter")
    private ContentIdentifierDto chapterIdentifierDto;
    @JsonProperty("message")
    private ContentIdentifierDto messageIdentifierDto;

    public Bookmark() {
    }

    public Bookmark(ContentIdentifierDto courseIdentifierDto, ContentIdentifierDto moduleIdentifierDto, ContentIdentifierDto chapterIdentifierDto, ContentIdentifierDto messageIdentifierDto) {
        this.courseIdentifierDto = courseIdentifierDto;
        this.moduleIdentifierDto = moduleIdentifierDto;
        this.chapterIdentifierDto = chapterIdentifierDto;
        this.messageIdentifierDto = messageIdentifierDto;
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
}
