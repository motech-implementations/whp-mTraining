package org.motechproject.whp.mtraining.web.domain;

import org.motechproject.mtraining.domain.Question;
import org.motechproject.whp.mtraining.domain.ContentIdentifier;

import java.util.List;

public class QuizResponse implements MotechResponse {

    private Long callerId;
    private String sessionId;
    private String uniqueId;
    private List<ContentIdentifier> questions;
    private String responseMessage;
    private int responseCode;

    //For JSON parsing
    public QuizResponse() {

    }

    public QuizResponse(Long callerId, String sessionId, String uniqueId, List<ContentIdentifier> questions) {
        this.callerId = callerId;
        this.sessionId = sessionId;
        this.uniqueId = uniqueId;
        this.questions = questions;
        this.responseCode = ResponseStatus.OK.getCode();
        this.responseMessage = ResponseStatus.OK.getMessage();
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

    public List<ContentIdentifier> getQuestions() {
        return questions;
    }

    @Override
    public Integer getResponseCode() {
        return responseCode;
    }

    @Override
    public String getResponseMessage() {
        return responseMessage;
    }
}