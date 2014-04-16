package org.motechproject.whp.mtraining.reports.domain;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.UUID;

@PersistenceCapable(table = "question_attempt", identityType = IdentityType.APPLICATION)
public class QuestionAttempt {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;
    @Persistent(column = "quiz_attempt_id")
    private QuizAttempt quizAttemptId;
    @Persistent(column = "question_id")
    private UUID questionId;
    @Persistent(column = "question_version")
    private Integer questionVersion;
    @Persistent(column = "invalid_inputs")
    private String invalidInputs;
    @Persistent(column = "selected_option")
    private String selectedOption;
    @Persistent(column = "correct_answer")
    private Boolean isCorrectAnswer;
    @Persistent(column = "invalid_attempt")
    private Boolean invalidAttempt;
    @Persistent(column = "time_out")
    private Boolean timeOut;

    public QuestionAttempt(QuizAttempt quizAttemptId, UUID questionId, Integer questionVersion,
                           String invalidInputs, String selectedOption, Boolean isCorrectAnswer, Boolean invalidAttempt, Boolean timeOut) {
        this.quizAttemptId = quizAttemptId;
        this.questionId = questionId;
        this.questionVersion = questionVersion;
        this.invalidInputs = invalidInputs;
        this.selectedOption = selectedOption;
        this.isCorrectAnswer = isCorrectAnswer;
        this.invalidAttempt = invalidAttempt;
        this.timeOut = timeOut;
    }

    public Long getId() {
        return id;
    }

    public String getInvalidInputs() {
        return invalidInputs;
    }

    public Boolean isCorrectAnswer() {
        return isCorrectAnswer;
    }
}

