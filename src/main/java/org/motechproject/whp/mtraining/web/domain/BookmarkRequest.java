package org.motechproject.whp.mtraining.web.domain;

public class BookmarkRequest {

    private Long callerId;

    private String uniqueId;

    private String sessionId;

    public BookmarkRequest() {
    }

    public BookmarkRequest(Long callerId, String uniqueId, String sessionId) {
        this.callerId = callerId;
        this.uniqueId = uniqueId;
        this.sessionId = sessionId;
    }

    public Long getCallerId() {
        return callerId;
    }

    public void setCallerId(Long callerId) {
        this.callerId = callerId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void validate() {
    }
}
