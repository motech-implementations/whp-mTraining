package org.motechproject.whp.mtraining.dto;

/**
 * Contract object representing an Answer content.
 * + externalContentId : Id that points to an external file or resource that is associated with the answer.For eg. an audio file that is played to the enrollee
 * + correctOption : correct option for the question asked
 */
public class AnswerDto {
    private String externalId;
    private String correctOption;

    public AnswerDto() {
    }

    public AnswerDto(String correctOption, String externalId) {
        this.externalId = externalId;
        this.correctOption = correctOption;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getCorrectOption() {
        return correctOption;
    }
}
