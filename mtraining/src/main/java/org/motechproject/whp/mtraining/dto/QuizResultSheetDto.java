package org.motechproject.whp.mtraining.dto;

import org.motechproject.mds.annotations.Field;
import org.motechproject.mtraining.domain.MdsEntity;
import org.motechproject.whp.mtraining.domain.ContentIdentifier;
import org.motechproject.mds.annotations.Entity;
import java.util.List;

@Entity
public class QuizResultSheetDto extends MdsEntity {

    @Field
    private ContentIdentifier quizIdentifier;
    @Field
    private List<QuestionResultDto> questionResultDtos;
    @Field
    private Double score;
    @Field
    private Boolean passed;

    public QuizResultSheetDto(ContentIdentifier quizIdentifier, List<QuestionResultDto> questionResultDtos, Double score, Boolean passed) {
        this.quizIdentifier = quizIdentifier;
        this.questionResultDtos = questionResultDtos;
        this.score = score;
        this.passed = passed;
    }

    public ContentIdentifier getQuizIdentifier() {
        return quizIdentifier;
    }

    public void setQuizIdentifier(ContentIdentifier quizIdentifier) {
        this.quizIdentifier = quizIdentifier;
    }

    public List<QuestionResultDto> getQuestionResultDtos() {
        return questionResultDtos;
    }

    public void setQuestionResultDtos(List<QuestionResultDto> questionResultDtos) {
        this.questionResultDtos = questionResultDtos;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Boolean isPassed() {
        return passed;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
    }
}
