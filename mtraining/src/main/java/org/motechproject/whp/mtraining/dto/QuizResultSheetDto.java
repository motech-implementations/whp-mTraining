package org.motechproject.whp.mtraining.dto;

import java.util.List;

/**
 * Contract object representing a QuizResultSheet. It do not resides as an entity.
 * quizDto : quiz Identifier which enrollee has attempted
 * questionResultDtos : list of result of every question attempted in a quiz
 * score : percentage scored by the enrollee
 * passed : boolean value to hold whether the enrollee has passed the quiz or not
 */

public class QuizResultSheetDto {
    private String externalId;
    private ContentIdentifierDto quizDto;
    private List<QuestionResultDto> questionResultDtos;
    private Double score;
    private Boolean passed;

    public QuizResultSheetDto(String externalId, ContentIdentifierDto quizDto, List<QuestionResultDto> questionResultDtos, Double score, Boolean passed) {
        this.externalId = externalId;
        this.quizDto = quizDto;
        this.questionResultDtos = questionResultDtos;
        this.score = score;
        this.passed = passed;
    }

    public ContentIdentifierDto getQuizDto() {
        return quizDto;
    }

    public List<QuestionResultDto> getQuestionResultDtos() {
        return questionResultDtos;
    }

    public Double getScore() {
        return score;
    }

    public Boolean isPassed() {
        return passed;
    }

    public String getExternalId() {
        return externalId;
    }
}
