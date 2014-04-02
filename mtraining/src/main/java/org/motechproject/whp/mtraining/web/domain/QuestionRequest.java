package org.motechproject.whp.mtraining.web.domain;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.UUID;

import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_QUESTION_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.OK;

public class QuestionRequest {
    @JsonProperty
    private UUID questionId;
    @JsonProperty
    private List<String> invalidInputs;
    @JsonProperty
    private String selectedOption;
    @JsonProperty
    private Boolean timeOut;
    @JsonProperty
    private Boolean userSkipped;

    public QuestionRequest() {
    }

    public QuestionRequest(UUID questionId, List<String> invalidInputs, String selectedOption, Boolean timeOut, Boolean userSkipped) {
        this.questionId = questionId;
        this.invalidInputs = invalidInputs;
        this.selectedOption = selectedOption;
        this.timeOut = timeOut;
        this.userSkipped = userSkipped;
    }

    public ResponseStatus validate() {
        if (questionId == null)
            return MISSING_QUESTION_ID;
        return OK;
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
}
