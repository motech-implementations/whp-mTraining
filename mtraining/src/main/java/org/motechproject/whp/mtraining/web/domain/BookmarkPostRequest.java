package org.motechproject.whp.mtraining.web.domain;

import org.motechproject.whp.mtraining.util.JSONUtil;

public class BookmarkPostRequest {

    private Long callerId;
    private String sessionId;
    private String uniqueId;
    private Bookmark bookmark;

    public BookmarkPostRequest() {
    }

    public BookmarkPostRequest(Long callerId, String uniqueId, String sessionId, Bookmark bookmark) {
        this.callerId = callerId;
        this.uniqueId = uniqueId;
        this.sessionId = sessionId;
        this.bookmark = bookmark;
    }

    public Long getCallerId() {
        return callerId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Bookmark getBookmark() {
        return bookmark;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }
}


