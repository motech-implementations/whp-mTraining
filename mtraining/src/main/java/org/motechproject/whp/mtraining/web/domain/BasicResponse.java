package org.motechproject.whp.mtraining.web.domain;

public class BasicResponse implements MotechResponse {
    private Long callerId;
    private String sessionId;
    private String uniqueId;
    private int responseCode;
    private String responseMessage;

    public BasicResponse() {
    }

    public BasicResponse(Long callerId, String sessionId, String uniqueId) {
        this.callerId = callerId;
        this.sessionId = sessionId;
        this.uniqueId = uniqueId;
    }

    public BasicResponse(Long callerId, String sessionId, String uniqueId, ResponseStatus responseStatus) {
        this.callerId = callerId;
        this.sessionId = sessionId;
        this.uniqueId = uniqueId;
        this.responseCode = responseStatus.getCode();
        this.responseMessage = responseStatus.getMessage();
    }

    @Override
    public Integer getResponseCode() {
        return responseCode;
    }

    @Override
    public String getResponseMessage() {
        return responseMessage;
    }

    @Override
    public Long getCallerId() {
        return callerId;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    public BasicResponse withResponse(ResponseStatus status) {
        this.responseCode = status.getCode();
        this.responseMessage = status.getMessage();
        return this;
    }

    public BasicResponse withResponse(Integer responseCode, String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        return this;
    }
}
