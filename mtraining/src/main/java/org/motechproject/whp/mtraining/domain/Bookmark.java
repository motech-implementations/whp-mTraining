package org.motechproject.whp.mtraining.domain;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.util.JSONUtil;

import java.util.UUID;

/**
 * Couch document object representing a Bookmark content.
 * This will be the bookmark for the enrollee, in the course where it was stopped due to various reasons.
 * + externalId : enrollee identification (enrollee id)
 * + course : course identifier for which the enrollee is enrolled
 * + module : module identifier points to the module at which the enrollee is currently at.
 * + chapter : chapter identifier points to the chapter at which the enrollee is currently at.
 * + message : message identifier points to the message at which the enrollee is currently at.
 * + quiz : quiz identifier points to the quiz at which the enrollee is currently at.
 * + dateModified : it reflects the last modified date of the bookmark
 *
 * Note : A Bookmark will always point to either one of Message or Quiz and never both.
 * If the value of the message is set then that of quiz will be NULL and vice-versa
 */
@TypeDiscriminator("doc.type === 'Bookmark'")
public class Bookmark extends MotechBaseDataObject {

    private String externalId;

    private ContentIdentifier course;

    private ContentIdentifier module;

    private ContentIdentifier chapter;

    private ContentIdentifier message;

    private ContentIdentifier quiz;

    private DateTime dateModified;

    @JsonCreator
    public Bookmark(@JsonProperty("externalId") String externalId,
                    @JsonProperty("course") ContentIdentifier course,
                    @JsonProperty("module") ContentIdentifier module,
                    @JsonProperty("chapter") ContentIdentifier chapter,
                    @JsonProperty("message") ContentIdentifier message,
                    @JsonProperty("quiz") ContentIdentifier quiz,
                    @JsonProperty("dateModified") DateTime dateModified) {
        this.externalId = externalId;
        this.course = course;
        this.module = module;
        this.chapter = chapter;
        this.message = message;
        this.quiz = quiz;
        this.dateModified = dateModified;
    }

    public Bookmark(String externalId, ContentIdentifier course) {
        this(externalId, course, null, null, null, null, ISODateTimeUtil.nowInTimeZoneUTC());
    }

    public String getExternalId() {
        return externalId;
    }


    public ContentIdentifier getCourse() {
        return course;
    }

    public ContentIdentifier getModule() {
        return module;
    }

    public ContentIdentifier getChapter() {
        return chapter;
    }

    public ContentIdentifier getMessage() {
        return message;
    }

    public ContentIdentifier getQuiz() {
        return quiz;
    }

    public DateTime getDateModified() {
        return dateModified;
    }


    public void updateCourse(UUID contentId, Integer version) {
        course.update(contentId, version);
    }

    public void updateModule(UUID contentId, Integer version) {
        if (this.hasInvalidModule()) {
            this.quiz = new ContentIdentifier(contentId, version);
        } else {
            module.update(contentId, version);
        }
    }

    public void updateChapter(UUID contentId, Integer version) {
        if (this.hasInvalidChapter()) {
            this.quiz = new ContentIdentifier(contentId, version);
        } else {
            chapter.update(contentId, version);
        }
    }

    public void updateMessage(UUID contentId, Integer version) {
        if (this.hasInvalidMessage()) {
            this.message = new ContentIdentifier(contentId, version);
        } else {
            message.update(contentId, version);
        }
    }

    public void updateQuiz(UUID contentId, Integer version) {
        if (this.hasInvalidQuiz()) {
            this.quiz = new ContentIdentifier(contentId, version);
        } else {
            quiz.update(contentId, version);
        }

    }

    public void update(ContentIdentifier course, ContentIdentifier module, ContentIdentifier chapter, ContentIdentifier message, ContentIdentifier quiz, DateTime dateModified) {
        this.course = course;
        this.module = module;
        this.chapter = chapter;
        this.message = message;
        this.quiz = quiz;
        this.dateModified = dateModified;
    }

    public void updateCourseVersion(Integer version) {
        course.setVersion(version);
    }

    public void updateModuleVersion(Integer version) {
        module.setVersion(version);
    }

    public void updateChapterVersion(Integer version) {
        chapter.setVersion(version);
    }

    public void updateMessageVersion(Integer version) {
        message.setVersion(version);
    }

    public void updateQuizVersion(Integer version) {
        quiz.setVersion(version);
    }


    public void invalidateChapter() {
        chapter = null;
    }

    public void invalidateModule() {
        module = null;
    }

    public void invalidateMessage() {
        message = null;
    }


    public void invalidateQuiz() {
        quiz = null;
    }

    private boolean hasInvalidModule() {
        return module == null;
    }

    public boolean hasChapter() {
        return chapter != null && chapter.hasIdAndVersion();
    }

    @JsonIgnore
    public boolean isForMessage() {
        return message != null && message.hasIdAndVersion();
    }

    @JsonIgnore
    public boolean isForQuiz() {
        return quiz != null && quiz.hasIdAndVersion();
    }

    public boolean hasInvalidChapter() {
        return chapter == null;
    }

    public boolean hasInvalidMessage() {
        return message == null;
    }

    public boolean hasInvalidQuiz() {
        return quiz == null;
    }

    public boolean wasModifiedAfter(DateTime dateTime) {
        return dateModified.isAfter(dateTime);
    }


    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }


    public boolean hasModule() {
        return module != null && module.hasIdAndVersion();
    }


}
