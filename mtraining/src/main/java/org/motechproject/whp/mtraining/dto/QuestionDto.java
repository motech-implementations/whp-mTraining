package org.motechproject.whp.mtraining.dto;

/**
 * DTO Representation for Question class
 */
public class QuestionDto {

    private String name;

    private String description;

    private String correctAnswer;

    private String options;

    private String filename;

    private String explainingAnswerFilename;

    public QuestionDto() {
    }

    public QuestionDto(String name, String description, String correctAnswer, String options, String filename, String explainingAnswerFilename) {
        this.name = name;
        this.description = description;
        this.correctAnswer = correctAnswer;
        this.options = options;
        this.filename = filename;
        this.explainingAnswerFilename = explainingAnswerFilename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getExplainingAnswerFilename() {
        return explainingAnswerFilename;
    }

    public void setExplainingAnswerFilename(String explainingAnswerFilename) {
        this.explainingAnswerFilename = explainingAnswerFilename;
    }
}
