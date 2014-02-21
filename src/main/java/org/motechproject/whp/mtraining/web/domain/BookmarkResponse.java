package org.motechproject.whp.mtraining.web.domain;

import org.motechproject.whp.mtraining.domain.Location;

public class BookmarkResponse {

    private ErrorCode errorCode;
    private String sessionId;
    private String uniqueId;
    private Long callerId;

    private String state;
    private String block;
    private String district;

    //For tests
    public BookmarkResponse() {
    }

    public BookmarkResponse(Long callerId, String sessionId, String uniqueId) {
        this.callerId = callerId;
        this.sessionId = sessionId;
        this.uniqueId = uniqueId;
    }

    public String getErrorCode() {
        return errorCode == null ? null : errorCode.name();
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Long getCallerId() {
        return callerId;
    }


    public boolean hasError() {
        return errorCode != null;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getState() {
        return state;
    }


    public String getBlock() {
        return block;
    }

    public String getDistrict() {
        return district;
    }


    public void setLocation(Location location) {
        if (location != null) {
            this.state = location.getState();
            this.block = location.getBlock();
            this.district = location.getDistrict();
        }
    }
}
