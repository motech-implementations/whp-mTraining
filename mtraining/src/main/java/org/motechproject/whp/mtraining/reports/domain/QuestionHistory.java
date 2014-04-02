package org.motechproject.whp.mtraining.reports.domain;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.UUID;

@PersistenceCapable(table = "question_history", identityType = IdentityType.APPLICATION)
public class QuestionHistory {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;
    @Persistent(column = "quiz_history_id")
    private QuizHistory quizHistoryId;
    @Persistent(column = "question_id")
    private UUID questionId;
    @Persistent(column = "question_version")
    private Integer questionVersion;
    @Persistent(column = "invalid_inputs")
    private String invalidInputs;
    @Persistent(column = "selected_option")
    private String selectedOption;
    @Persistent
    private Boolean status;

    public QuestionHistory(QuizHistory quizHistoryId, UUID questionId, Integer questionVersion,
                           String invalidInputs, String selectedOption, Boolean status) {
        this.quizHistoryId = quizHistoryId;
        this.questionId = questionId;
        this.questionVersion = questionVersion;
        this.invalidInputs = invalidInputs;
        this.selectedOption = selectedOption;
        this.status = status;
    }

    public void setQuizHistoryId(QuizHistory quizHistoryId) {
        this.quizHistoryId = quizHistoryId;
    }
}

