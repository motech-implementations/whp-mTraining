package org.motechproject.whp.mtraining.web.domain;

import org.joda.time.DateTime;
import org.motechproject.whp.mtraining.util.JSONUtil;

public class BookmarkPostRequest {

    private Long callerId;
    private String sessionId;
    private Bookmark bookmark;
    private String uniqueId;
    private DateTime dateModified;

    public BookmarkPostRequest() {
    }

    public BookmarkPostRequest(Long callerId, String uniqueId, String sessionId, Bookmark bookmark) {
        this.callerId = callerId;
        this.uniqueId = uniqueId;
        this.sessionId = sessionId;
        this.bookmark = bookmark;
        this.dateModified = DateTime.now();
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

    public DateTime getDateModified() {
        return dateModified;
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }
}


