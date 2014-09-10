package org.motechproject.whp.mtraining.dto;

import org.joda.time.DateTime;

public class BookmarkReportDto extends MdsEntityDto {

    private ContentIdentifierDto courseIdentifier;

    private ContentIdentifierDto moduleIdentifier;

    private ContentIdentifierDto chapterIdentifier;

    private ContentIdentifierDto messageIdentifier;

    private ContentIdentifierDto quizIdentifier;

    private DateTime dateModified;

    public BookmarkReportDto(long id, DateTime creationDate, DateTime modificationDate, ContentIdentifierDto courseIdentifier, ContentIdentifierDto moduleIdentifier,
                             ContentIdentifierDto chapterIdentifier, ContentIdentifierDto messageIdentifier, ContentIdentifierDto quizIdentifier, DateTime dateModified) {
        super(id, creationDate, modificationDate);
        this.courseIdentifier = courseIdentifier;
        this.moduleIdentifier = moduleIdentifier;
        this.chapterIdentifier = chapterIdentifier;
        this.messageIdentifier = messageIdentifier;
        this.quizIdentifier = quizIdentifier;
        this.dateModified = dateModified;
    }

    public ContentIdentifierDto getCourseIdentifier() {
        return courseIdentifier;
    }

    public void setCourseIdentifier(ContentIdentifierDto courseIdentifier) {
        this.courseIdentifier = courseIdentifier;
    }

    public ContentIdentifierDto getModuleIdentifier() {
        return moduleIdentifier;
    }

    public void setModuleIdentifier(ContentIdentifierDto moduleIdentifier) {
        this.moduleIdentifier = moduleIdentifier;
    }

    public ContentIdentifierDto getChapterIdentifier() {
        return chapterIdentifier;
    }

    public void setChapterIdentifier(ContentIdentifierDto chapterIdentifier) {
        this.chapterIdentifier = chapterIdentifier;
    }

    public ContentIdentifierDto getMessageIdentifier() {
        return messageIdentifier;
    }

    public void setMessageIdentifier(ContentIdentifierDto messageIdentifier) {
        this.messageIdentifier = messageIdentifier;
    }

    public ContentIdentifierDto getQuizIdentifier() {
        return quizIdentifier;
    }

    public void setQuizIdentifier(ContentIdentifierDto quizIdentifier) {
        this.quizIdentifier = quizIdentifier;
    }

    public DateTime getDateModified() {
        return dateModified;
    }

    public void setDateModified(DateTime dateModified) {
        this.dateModified = dateModified;
    }
}
