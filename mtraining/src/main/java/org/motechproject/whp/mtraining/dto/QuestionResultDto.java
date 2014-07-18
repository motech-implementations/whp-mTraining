package org.motechproject.whp.mtraining.dto;

import java.util.UUID;

/**
 * Contract object representing a QuestionResult. It do not resides as an entity.
 * questionId : question Id which enrollee has attempted
 * version :  version of the question which enrollee has attempted
 * selectedOption : option selected by the enrollee from the given options
 * correct : boolean value to hold whether the answer given by enrollee is correct or not
 */

public class QuestionResultDto {
    private UUID questionId;
    private Integer version;
    private String selectedOption;
    private Boolean correct;

    public QuestionResultDto(UUID questionId, Integer version, String selectedOption, Boolean correct) {
        this.questionId = questionId;
        this.version = version;
        this.selectedOption = selectedOption;
        this.correct = correct;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public Integer getVersion() {
        return version;
    }

    public Boolean isCorrect() {
        return this.correct;
    }
}
