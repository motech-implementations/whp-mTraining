package org.motechproject.whp.mtraining.web.domain;

public class QuizReportResponse implements MotechResponse {

    private Long callerId;
    private String sessionId;
    private String uniqueId;
    private String responseMessage;
    private int responseCode;
    private Double quizScore;
    private Boolean passed;

    //For JSON parsing
    public QuizReportResponse() {

    }

    public QuizReportResponse(Long callerId, String sessionId, String uniqueId, Double quizScore, Boolean passed, ResponseStatus status) {
        this.callerId = callerId;
        this.sessionId = sessionId;
        this.uniqueId = uniqueId;
        this.quizScore = quizScore;
        this.passed = passed;
        this.responseCode = status.getCode();
        this.responseMessage = status.getMessage();
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

    public Double getQuizScore() {
        return quizScore;
    }

    public Boolean getPassed() {
        return passed;
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