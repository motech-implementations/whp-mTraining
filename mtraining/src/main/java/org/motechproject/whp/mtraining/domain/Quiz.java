package org.motechproject.whp.mtraining.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.mtraining.dto.QuestionDto;
import org.motechproject.mtraining.dto.QuizDto;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@PersistenceCapable(table = "quiz", identityType = IdentityType.APPLICATION)
public class Quiz extends CourseContent implements CourseContentHolder {


    @Element(column = "quiz_id")
    @Order(column = "question_order")
    @Persistent(defaultFetchGroup = "true", dependentElement = "true")
    private List<Question> questions = new ArrayList<>();

    @Persistent(column = "pass_percentage")
    private Long passPercentage;

    @Persistent(column = "number_of_questions")
    private Integer numberOfQuestions = 0;

    public Quiz(String name, UUID contentId, Integer version, String createdBy, DateTime createdOn, Long passPercentage, boolean isActive, List<Question> questions) {
        super(name, contentId, version, createdBy, createdOn, isActive);
        this.passPercentage = passPercentage;
        this.questions = questions;
        this.numberOfQuestions = (questions == null) ? 0 : questions.size();
    }

    public Quiz(QuizDto quizDto) {
        this(quizDto.getName(), quizDto.getContentId(), quizDto.getVersion(), quizDto.getCreatedBy(), quizDto.getCreatedOn(), quizDto.getPassPercentage(),
                quizDto.isActive(),
                mapToQuestions(quizDto.getQuestions()));
    }

    public void addQuestion(Question question) {
        questions.add(question);
        incrementNumberOfQuestions();
    }

    public void removeInactiveContent() {
        filter(questions);
    }

    private static List<Question> mapToQuestions(List<QuestionDto> questionDtoList) {
        List<Question> questions = new ArrayList<>();
        if (isBlank(questionDtoList)) {
            return questions;
        }
        for (QuestionDto questionDto : questionDtoList) {
            questions.add(new Question(questionDto));
        }
        return questions;
    }

    public Long getPassPercentage() {
        return passPercentage;
    }

    @JsonProperty("noOfQuestionsToBePlayed")
    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public List<Question> getQuestions() {
        return questions;
    }


    private void incrementNumberOfQuestions() {
        numberOfQuestions = numberOfQuestions + 1;
    }

}
