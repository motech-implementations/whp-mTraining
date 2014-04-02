package org.motechproject.whp.mtraining.web.domain;

import org.motechproject.whp.mtraining.util.JSONUtil;

public class BookmarkPostRequest extends IVRRequest {

    private Bookmark bookmark;

    public BookmarkPostRequest() {
    }

    public BookmarkPostRequest(Long callerId, String uniqueId, String sessionId, Bookmark bookmark) {
        super(callerId, sessionId, uniqueId);
        this.bookmark = bookmark;
    }

    public Bookmark getBookmark() {
        return bookmark;
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }
}


