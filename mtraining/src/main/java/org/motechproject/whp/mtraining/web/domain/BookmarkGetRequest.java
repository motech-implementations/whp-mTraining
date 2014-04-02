package org.motechproject.whp.mtraining.web.domain;

public class BookmarkGetRequest extends IVRRequest {

    public BookmarkGetRequest() {
    }

    public BookmarkGetRequest(Long callerId, String sessionId, String uniqueId) {
        super(callerId, sessionId, uniqueId);
    }

    /**
     * Session id is optional
     *
     * @return
     */
    @Override
    protected boolean isSessionIdMissing() {
        return false;
    }
}
