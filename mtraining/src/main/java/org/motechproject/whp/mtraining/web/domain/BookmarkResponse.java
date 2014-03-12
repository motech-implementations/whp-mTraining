package org.motechproject.whp.mtraining.web.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.whp.mtraining.domain.Location;

public class BookmarkResponse implements MotechResponse {

    private Long callerId;
    private String sessionId;
    private String uniqueId;
    private Location location;
    private String responseStatusMessage;
    private int responseStatusCode;
    @JsonProperty
    private BookmarkDto bookmark;

    //For JSON parsing
    public BookmarkResponse() {

    }

    public BookmarkResponse(Long callerId, String sessionId, String uniqueId, Location location, BookmarkDto bookmark) {
        this.callerId = callerId;
        this.sessionId = sessionId;
        this.uniqueId = uniqueId;
        this.location = location;
        this.bookmark = bookmark;
        this.responseStatusCode = ResponseStatus.OK.getCode();
        this.responseStatusMessage = ResponseStatus.OK.getMessage();
    }

    public Long getCallerId() {
        return callerId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public Location getLocation() {
        return location;
    }


    @Override
    public int getResponseStatusCode() {
        return responseStatusCode;
    }

    @Override
    public String getResponseStatusMessage() {
        return responseStatusMessage;
    }
}