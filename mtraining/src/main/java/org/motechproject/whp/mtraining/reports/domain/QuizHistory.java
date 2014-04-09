package org.motechproject.whp.mtraining.reports.domain;

import org.joda.time.DateTime;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.UUID;

@PersistenceCapable(table = "quiz_history", identityType = IdentityType.APPLICATION)
public class QuizHistory {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;
    @Persistent(column = "provider_remedy_id")
    private String remedyId;
    @Persistent(column = "caller_id")
    private Long callerId;
    @Persistent(column = "unique_id")
    private String uniqueId;
    @Persistent(column = "session_id")
    private String sessionId;
    @Persistent(column = "quiz_id")
    private UUID quizId;
    @Persistent(column = "quiz_version")
    private Integer quizVersion;
    @Persistent(column = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private DateTime startTime;
    @Persistent(column = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private DateTime endTime;
    @Persistent
    private Boolean status;
    @Persistent
    private Double score;
    @Persistent(column = "incomplete_attempt")
    private Boolean incompleteAttempt;

    public QuizHistory(String remedyId, Long callerId, String uniqueId, String sessionId, UUID quizId,
                       Integer quizVersion, DateTime startTime, DateTime endTime, Boolean status, Double score, Boolean incompleteAttempt) {
        this.remedyId = remedyId;
        this.callerId = callerId;
        this.uniqueId = uniqueId;
        this.sessionId = sessionId;
        this.quizId = quizId;
        this.quizVersion = quizVersion;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.score = score;
        this.incompleteAttempt = incompleteAttempt;
    }

    public Long getId() {
        return id;
    }
}
