package org.motechproject.whp.mtraining.domain;

import org.joda.time.DateTime;
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
    @Persistent(column = "provider_id")
    private Long provider;
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


    public BookmarkRequestLog(Long callerId, String uniqueId, String sessionId, ResponseStatus responseStatusCode,
                              Long provider, UUID courseId, Integer courseVersion, UUID moduleId, Integer moduleVersion, UUID chapterId, Integer chapterVersion, UUID messageId, Integer messageVersion) {
        this.callerId = callerId;
        this.uniqueId = uniqueId;
        this.sessionId = sessionId;
        this.provider = provider;
        this.courseId = courseId;
        this.courseVersion = courseVersion;
        this.moduleId = moduleId;
        this.moduleVersion = moduleVersion;
        this.chapterId = chapterId;
        this.chapterVersion = chapterVersion;
        this.messageId = messageId;
        this.messageVersion = messageVersion;
        this.responseCode = responseStatusCode.getCode();
        this.responseMessage = responseStatusCode.getMessage();
        this.createdOn = DateTime.now();
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
}