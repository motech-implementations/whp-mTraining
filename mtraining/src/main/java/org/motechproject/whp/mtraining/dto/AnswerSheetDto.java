package org.motechproject.whp.mtraining.dto;

import java.util.UUID;

/**
 * Contract object representing an Answer Sheet content.
 * + questionId : question id which enrollee has attempted
 * + questionVersion : question version which enrollee has attempted
 * + selectedOption : selected option by an enrollee for the question
 */


public class AnswerSheetDto {
    private UUID questionId;
    private Integer questionVersion;
    private String selectedOption;

    public AnswerSheetDto(UUID questionId, Integer questionVersion, String selectedOption) {
        this.questionId = questionId;
        this.questionVersion = questionVersion;
        this.selectedOption = selectedOption;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public Integer getQuestionVersion() {
        return questionVersion;
    }
}
