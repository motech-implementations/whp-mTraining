package org.motechproject.whp.mtraining.web.domain;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuestionRequest {
    @JsonProperty
    private UUID questionId;
    @JsonProperty
    private Integer questionVersion;
    @JsonProperty
    private List<String> invalidInputs;
    @JsonProperty
    private String selectedOption;
    @JsonProperty
    private Boolean timeOut;
    @JsonProperty
    private Boolean invalidAttempt;

    public QuestionRequest() {
    }

    public QuestionRequest(UUID questionId, Integer questionVersion, List<String> invalidInputs, String selectedOption, Boolean timeOut, Boolean invalidAttempt) {
        this.questionId = questionId;
        this.questionVersion = questionVersion;
        this.invalidInputs = invalidInputs;
        this.selectedOption = selectedOption;
        this.timeOut = timeOut;
        this.invalidAttempt = invalidAttempt;
    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        if (questionId == null)
            errors.add(new ValidationError(ResponseStatus.MISSING_QUESTION_ID));
        return errors;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public List<String> getInvalidInputs() {
        return invalidInputs;
    }

    public Integer getQuestionVersion() {
        return questionVersion;
    }

    public Boolean getTimeOut() {
        return timeOut;
    }

    public Boolean getInvalidAttempt() {
        return invalidAttempt;
    }
}
