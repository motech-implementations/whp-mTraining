package org.motechproject.whp.mtraining.domain;

import org.joda.time.DateTime;
import org.motechproject.mtraining.dto.QuestionDto;
import org.motechproject.mtraining.dto.QuizDto;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@PersistenceCapable(table = "quiz", identityType = IdentityType.APPLICATION)
public class Quiz {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;

    @Persistent
    private String name;
    @Persistent(column = "content_id")
    private UUID contentId;
    @Persistent
    private Integer version;
    @Persistent(column = "created_by")
    private String createdBy;
    @Persistent(column = "created_on")
    private DateTime createdOn;

    @Persistent(column = "is_active")
    private boolean isActive;

    @Element(column = "quiz_id")
    @Order(column = "question_order")
    @Persistent(defaultFetchGroup = "true", dependentElement = "true")
    private List<Question> questions = new ArrayList<>();

    @Persistent(column = "pass_percentage")
    private Long passPercentage;

    @Persistent(column = "number_of_questions")
    private Integer numberOfQuestions = 0;

    public Quiz(String name, UUID contentId, Integer version, String createdBy, DateTime createdOn, Long passPercentage, boolean isActive) {
        this.name = name;
        this.passPercentage = passPercentage;
        this.contentId = contentId;
        this.version = version;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
        this.passPercentage = passPercentage;
        this.isActive = isActive;
    }

    public Quiz(QuizDto quizDto) {
        this(quizDto.getName(), quizDto.getContentId(), quizDto.getVersion(), quizDto.getCreatedBy(), quizDto.getCreatedOn(), quizDto.getPassPercentage(), quizDto.isActive());
        setQuestions(quizDto.getQuestions());
    }

    public void addQuestion(Question question) {
        questions.add(question);
        incrementNumberOfQuestions();
    }

    public Long getPassPercentage() {
        return passPercentage;
    }

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    private void setQuestions(List<QuestionDto> questionDtos) {
        for (QuestionDto questionDto : questionDtos) {
            addQuestion(new Question(questionDto));
        }
    }

    private void incrementNumberOfQuestions() {
        numberOfQuestions = numberOfQuestions + 1;
    }

}
