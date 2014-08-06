package org.motechproject.whp.mtraining.dto;

/**
 * DTO Representation for Question class
 */
public class QuestionDto {

    private String question;

    private String answer;

    public QuestionDto() {
    }

    public QuestionDto(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
