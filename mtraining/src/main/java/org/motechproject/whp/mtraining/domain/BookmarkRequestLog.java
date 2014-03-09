package org.motechproject.whp.mtraining.domain;

import org.joda.time.DateTime;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;

import javax.jdo.annotations.*;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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

    public BookmarkRequestLog(Long callerId, String uniqueId, String sessionId, ResponseStatus responseStatusCode) {
        this.callerId = callerId;
        this.uniqueId = uniqueId;
        this.sessionId = sessionId;
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

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public DateTime getCreatedOn() {
        return createdOn;
    }
}