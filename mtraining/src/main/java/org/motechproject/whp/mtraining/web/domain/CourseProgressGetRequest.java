package org.motechproject.whp.mtraining.web.domain;

public class CourseProgressGetRequest extends IVRRequest {

    public CourseProgressGetRequest() {
    }

    public CourseProgressGetRequest(Long callerId, String sessionId, String uniqueId) {
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
