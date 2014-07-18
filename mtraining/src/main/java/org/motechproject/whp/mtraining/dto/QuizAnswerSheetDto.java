package org.motechproject.whp.mtraining.dto;

import java.util.List;

/**
 * Contract object representing a QuizAnswerSheet. It do not resides as an entity.
 * externalId : enrollee id
 * quizDto :  quiz identifier, the enrollee has attempted
 * answerSheetDtos : list of answer sheet of contained quiz questions
 */


public class QuizAnswerSheetDto {
    private String externalId;
    private ContentIdentifierDto quizDto;
    private List<AnswerSheetDto> answerSheetDtos;

    public QuizAnswerSheetDto(String externalId, ContentIdentifierDto quizDto, List<AnswerSheetDto> answerSheetDtos) {
        this.externalId = externalId;
        this.quizDto = quizDto;
        this.answerSheetDtos = answerSheetDtos;
        this.answerSheetDtos = answerSheetDtos;
    }

    public ContentIdentifierDto getQuizDto() {
        return quizDto;
    }

    public List<AnswerSheetDto> getAnswerSheetDtos() {
        return answerSheetDtos;
    }

    public String getExternalId() {
        return externalId;
    }

}
