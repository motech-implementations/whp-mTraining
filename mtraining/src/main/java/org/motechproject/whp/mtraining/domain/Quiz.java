package org.motechproject.whp.mtraining.domain;

import org.joda.time.DateTime;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@PersistenceCapable(table = "quiz", identityType = IdentityType.APPLICATION)
public class Quiz extends CourseContent {

    @Element(column = "quiz_id")
    @Order(column = "question_order")
    @Persistent(defaultFetchGroup = "true", dependentElement = "true")
    private List<Question> questions = new ArrayList<>();

    @Persistent(column = "pass_percentage")
    private Long passPercentage;

    @Persistent(column = "number_of_questions")
    private Integer numberOfQuestions = 0;

    public Quiz(String name, UUID contentId, Integer version, String description, String modifiedBy, DateTime dateModified, Long passPercentage) {
        super(name, contentId, version, description, modifiedBy, dateModified);
        this.passPercentage = passPercentage;
    }

    public void addQuestions(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            return;
        }
        this.questions.addAll(questions);
        setNumberOfQuestions(questions.size());
    }

    public Long getPassPercentage() {
        return passPercentage;
    }

    private void setNumberOfQuestions(Integer numberOfQuestion) {
        numberOfQuestions = numberOfQuestion;
    }

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
