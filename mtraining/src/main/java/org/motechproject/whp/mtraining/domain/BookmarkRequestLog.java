package org.motechproject.whp.mtraining.domain;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.UUID;

@PersistenceCapable(table = "bookmark_request_log", identityType = IdentityType.APPLICATION)
public class BookmarkRequestLog {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;
    @Persistent(column = "caller_id")
    private Long callerId;
    @Persistent(column = "unique_id")
    private String uniqueId;
    @Persistent(column = "session_id")
    private String sessionId;
    @Persistent(column = "response_code")
    private int responseCode;
    @Persistent(column = "response_message")
    private String responseMessage;
    @Persistent(column = "created_on")
    @Temporal(TemporalType.TIMESTAMP)
    private DateTime createdOn;
    @Persistent(column = "provider_remedy_id")
    private String providerRemedyId;
    @Persistent(column = "course_id")
    private UUID courseId;
    @Persistent(column = "course_version")
    private Integer courseVersion;
    @Persistent(column = "module_id")
    private UUID moduleId;
    @Persistent(column = "module_version")
    private Integer moduleVersion;
    @Persistent(column = "chapter_id")
    private UUID chapterId;
    @Persistent(column = "chapter_version")
    private Integer chapterVersion;
    @Persistent(column = "message_id")
    private UUID messageId;
    @Persistent(column = "message_version")
    private Integer messageVersion;


    public BookmarkRequestLog(Long callerId, String uniqueId, String sessionId, ResponseStatus responseStatus, String providerRemedyId, BookmarkDto bookmark) {
        this.callerId = callerId;
        this.uniqueId = uniqueId;
        this.sessionId = sessionId;
        this.providerRemedyId = providerRemedyId;
        this.responseCode = responseStatus.getCode();
        this.responseMessage = responseStatus.getMessage();
        this.createdOn = DateTime.now().withZone(DateTimeZone.UTC);
        setCourse(bookmark.getCourse());
        setModule(bookmark.getModule());
        setChapter(bookmark.getChapter());
        setMessage(bookmark.getMessage());
    }

    public BookmarkRequestLog(Long callerId, String uniqueId, String sessionId, ResponseStatus responseStatus) {
        this.callerId = callerId;
        this.uniqueId = uniqueId;
        this.sessionId = sessionId;
        this.responseCode = responseStatus.getCode();
        this.responseMessage = responseStatus.getMessage();
    }

    public Long getCallerId() {
        return callerId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getSessionId() {
        return sessionId;
    }

    private void setCourse(ContentIdentifierDto course) {
        this.courseId = course.getContentId();
        this.courseVersion = course.getVersion();
    }

    private void setModule(ContentIdentifierDto module) {
        this.moduleId = module.getContentId();
        this.moduleVersion = module.getVersion();
    }

    private void setChapter(ContentIdentifierDto chapter) {
        this.chapterId = chapter.getContentId();
        this.chapterVersion = chapter.getVersion();
    }

    private void setMessage(ContentIdentifierDto message) {
        this.messageId = message.getContentId();
        this.messageVersion = message.getVersion();
    }


}