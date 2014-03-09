package org.motechproject.whp.mtraining.web.domain;

public class ErrorResponse implements MotechResponse {
    private Long callerId;
    private String sessionId;
    private String uniqueId;
    private int responseStatusCode;
    private String responseStatusMessage;

    public ErrorResponse() {
    }

    public ErrorResponse(Long callerId, String sessionId, String uniqueId, ResponseStatus responseStatus) {
        this.callerId = callerId;
        this.sessionId = sessionId;
        this.uniqueId = uniqueId;
        this.responseStatusCode = responseStatus.getCode();
        this.responseStatusMessage = responseStatus.getMessage();
    }

    @Override
    public int getResponseStatusCode() {
        return responseStatusCode;
    }

    @Override
    public String getResponseStatusMessage() {
        return responseStatusMessage;
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


}
