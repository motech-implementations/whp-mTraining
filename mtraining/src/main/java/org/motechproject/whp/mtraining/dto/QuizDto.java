package org.motechproject.whp.mtraining.dto;

import org.joda.time.DateTime;
import org.motechproject.mtraining.domain.CourseUnitState;

import java.util.List;

/**
 * DTO representation for Quiz class
 */
public class QuizDto extends CourseUnitMetadataDto {

    private List<QuestionDto> questions;

    private double passPercentage;

    public QuizDto() {
    }

    public QuizDto(Integer id, String name, String description, CourseUnitState state, String filename,
                   DateTime creationDate, DateTime modificationDate, List<QuestionDto> questions) {
        super(id, name, description, state, filename, creationDate, modificationDate);
        this.questions = questions;
    }

    public List<QuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDto> questions) {
        this.questions = questions;
    }

    public double getPassPercentage() {
        return passPercentage;
    }

    public void setPassPercentage(double passPercentage) {
        this.passPercentage = passPercentage;
    }

    public QuizDto(long id, String name, CourseUnitState state, DateTime creationDate, DateTime modificationDate,
                   double passPercentage, List<QuestionDto> questions) {
        super(id, name, state, creationDate, modificationDate);
        setPassPercentage(passPercentage);
        setQuestions(questions);
    }
}
