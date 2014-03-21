package org.motechproject.whp.mtraining.web.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.util.ISODateTimeUtil;

public class Bookmark {


    @JsonProperty("course")
    private ContentIdentifierDto courseIdentifierDto;
    @JsonProperty("module")
    private ContentIdentifierDto moduleIdentifierDto;
    @JsonProperty("chapter")
    private ContentIdentifierDto chapterIdentifierDto;
    @JsonProperty("message")
    private ContentIdentifierDto messageIdentifierDto;
    @JsonProperty
    private String dateModified;

    //For JSON parsing
    public Bookmark() {
    }

    public Bookmark(ContentIdentifierDto courseIdentifierDto, ContentIdentifierDto moduleIdentifierDto, ContentIdentifierDto chapterIdentifierDto, ContentIdentifierDto messageIdentifierDto) {
        this.courseIdentifierDto = courseIdentifierDto;
        this.moduleIdentifierDto = moduleIdentifierDto;
        this.chapterIdentifierDto = chapterIdentifierDto;
        this.messageIdentifierDto = messageIdentifierDto;
        this.dateModified = ISODateTimeUtil.nowAsStringInTimeZoneUTC();
    }

    public Bookmark(BookmarkDto bookmarkDto) {
        this.courseIdentifierDto = bookmarkDto.getCourse();
        this.moduleIdentifierDto = bookmarkDto.getModule();
        this.chapterIdentifierDto = bookmarkDto.getChapter();
        this.messageIdentifierDto = bookmarkDto.getMessage();
        this.dateModified = bookmarkDto.getDateModified();
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

    public String getDateModified() {
        return dateModified;
    }
}
