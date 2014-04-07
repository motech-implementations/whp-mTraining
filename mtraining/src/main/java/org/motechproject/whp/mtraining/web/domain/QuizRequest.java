package org.motechproject.whp.mtraining.web.domain;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.UUID;

public class QuizRequest extends IVRRequest {

    @JsonProperty
    private UUID quizId;
    @JsonProperty
    private Integer quizVersion;

    public QuizRequest() {
    }

    public QuizRequest(Long callerId, String uniqueId, String sessionId, UUID quizId, Integer quizVersion) {
        super(callerId, sessionId, uniqueId);
        this.quizId = quizId;
        this.quizVersion = quizVersion;
    }

    public List<ValidationError> validate() {
        List<ValidationError> validationErrors = super.validate();
        if (!validationErrors.isEmpty()) {
            return validationErrors;
        }
        if (quizId == null || quizVersion == null) {
            validationErrors.add(new ValidationError(ResponseStatus.MISSING_QUIZ));
        }
        return validationErrors;
    }
}