package org.motechproject.whp.mtraining.web.domain;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.UUID;

import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_QUIZ;

public class QuizRequest extends CallDetailsRequest {

    @JsonProperty
    private UUID quizId;
    @JsonProperty
    private Integer quizVersion;

    public QuizRequest() {
    }

    public QuizRequest(Long callerId, String uniqueId, String sessionId, UUID quizId, Integer quizVersion) {
        super(callerId, uniqueId, sessionId);
        this.quizId = quizId;
        this.quizVersion = quizVersion;
    }

    public ResponseStatus validate() {
        ResponseStatus validationStatus = super.validate();
        if (!validationStatus.isValid())
            return validationStatus;
        if (quizId == null || quizVersion == null)
            return MISSING_QUIZ;
        return ResponseStatus.OK;
    }
}