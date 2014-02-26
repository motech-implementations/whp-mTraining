package org.motechproject.whp.mtraining.web.domain;

import org.motechproject.whp.mtraining.domain.Location;

public class BookmarkResponse implements MotechResponse {

    private String sessionId;
    private String uniqueId;
    private Long callerId;
    private String responseStatusMessage;
    private int responseStatusCode;
    private Location location;

    //For tests
    public BookmarkResponse() {
    }

    public BookmarkResponse(Long callerId, String sessionId, String uniqueId) {
        this.callerId = callerId;
        this.sessionId = sessionId;
        this.uniqueId = uniqueId;
        this.responseStatusCode = ResponseStatus.OK.getCode();
        this.responseStatusMessage = ResponseStatus.OK.getMessage();
    }


    public String getSessionId() {
        return sessionId;
    }

    public Long getCallerId() {
        return callerId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public int getResponseStatusCode() {
        return responseStatusCode;
    }

    @Override
    public String getResponseStatusMessage() {
        return responseStatusMessage;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


}
