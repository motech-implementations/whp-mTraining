package org.motechproject.whp.mtraining.dto;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.util.JSONUtil;

/**
 * Contract object representing a Bookmark.
 * Chapter
 * + externalId : user's identification (user id)
 * + course : course identifier for which the user is enrolled
 * + module : module identifier points to the module at which the user is currently at.
 * + chapter : chapter identifier points to the chapter at which the user is currently at.
 * + message : message identifier points to the message at which the user is currently at.
 * + quiz : quiz identifier points to the quiz at which the user is currently at.
 * + dateModified : it reflects the last modified date of the bookmark
 */

public class BookmarkDto {

    @JsonIgnore
    private String externalId;

    @JsonProperty(value = "course")
    private ContentIdentifierDto course;

    @JsonProperty(value = "module")
    private ContentIdentifierDto module;

    @JsonProperty(value = "chapter")
    private ContentIdentifierDto chapter;

    @JsonProperty(value = "message")
    private ContentIdentifierDto message;

    @JsonProperty(value = "quiz")
    private ContentIdentifierDto quiz;

    @JsonProperty
    private String dateModified;

    public BookmarkDto() {
    }

    public BookmarkDto(String externalId, ContentIdentifierDto course, ContentIdentifierDto module, ContentIdentifierDto chapter, ContentIdentifierDto message, ContentIdentifierDto quiz,
                       DateTime dateModified) {
        this.externalId = externalId;
        this.course = course;
        this.module = module;
        this.chapter = chapter;
        this.message = message;
        this.quiz = quiz;
        this.dateModified = ISODateTimeUtil.asStringInTimeZoneUTC(dateModified);
    }

    public BookmarkDto(String externalId, ContentIdentifierDto course) {
        this.externalId = externalId;
        this.course = course;
    }

    public String getExternalId() {
        return externalId;
    }

    public ContentIdentifierDto getCourse() {
        return course;
    }

    public ContentIdentifierDto getModule() {
        return module;
    }

    public ContentIdentifierDto getChapter() {
        return chapter;
    }

    public ContentIdentifierDto getMessage() {
        return message;
    }

    public ContentIdentifierDto getQuiz() {
        return quiz;
    }

    public String getDateModified() {
        return dateModified;
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }

    /**
     * it checks the contained course, module, chapter and message or quiz are valid
     * it also check that bookmark should contain either quiz or message but not both
     * @return
     */
    public boolean isValid() {
        if (isNotValid(course) || isNotValid(module) || isNotValid(chapter)) {
            return false;
        }
        if (areBothQuizAndMessageInvalid()) {
            return false;
        }
        if (areBothQuizAndMessagePresent()) {
            return false;
        }
        return true;
    }

    private boolean areBothQuizAndMessageInvalid() {
        return isNotValid(quiz) && isNotValid(message);
    }

    private boolean areBothQuizAndMessagePresent() {
        return quiz != null && message != null;
    }

    /**
     * it checks that contentIdentifier of contract object should not be null.
     * it should contain both version and contentId
     * @param contentIdentifierDto
     * @return
     */
    private boolean isNotValid(ContentIdentifierDto contentIdentifierDto) {
        return contentIdentifierDto == null || !course.hasContentIdAndVersion();
    }
}
