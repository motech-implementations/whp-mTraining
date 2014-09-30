package org.motechproject.whp.mtraining.reports.domain;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mtraining.domain.MdsEntity;
import org.motechproject.whp.mtraining.domain.ContentIdentifier;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.whp.mtraining.util.CustomDateDeserializer;
import org.motechproject.whp.mtraining.util.CustomDateSerializer;

import javax.jdo.annotations.Persistent;
import java.util.List;

@Entity
public class QuizAttempt extends MdsEntity {

    @Field
    private String remedyId;

    @Field
    private Long callerId;

    @Field
    private String uniqueId;

    @Field
    private String sessionId;

    @Field
    private ContentIdentifier courseIdentifier;

    @Field
    private ContentIdentifier moduleIdentifier;

    @Field
    private ContentIdentifier chapterIdentifier;

    @Field
    private ContentIdentifier quizIdentifier;

    @Field
    private DateTime startTime;

    @Field
    private DateTime endTime;

    @Field
    private Boolean isPassed;

    @Field
    private Double score;

    @Field
    private Boolean incompleteAttempt;

    @Field
    @Persistent(defaultFetchGroup = "true")
    private List<QuestionAttempt> questionAttempts;

    public QuizAttempt(String remedyId, Long callerId, String uniqueId, String sessionId, ContentIdentifier courseIdentifier, ContentIdentifier moduleIdentifier, ContentIdentifier chapterIdentifier, ContentIdentifier quizIdentifier, DateTime startTime, DateTime endTime, Boolean isPassed, Double score, Boolean incompleteAttempt) {
        this.remedyId = remedyId;
        this.callerId = callerId;
        this.uniqueId = uniqueId;
        this.sessionId = sessionId;
        this.courseIdentifier = courseIdentifier;
        this.moduleIdentifier = moduleIdentifier;
        this.chapterIdentifier = chapterIdentifier;
        this.quizIdentifier = quizIdentifier;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isPassed = isPassed;
        this.score = score;
        this.incompleteAttempt = incompleteAttempt;
    }

    public ContentIdentifier getModuleIdentifier() {
        return moduleIdentifier;
    }

    public void setModuleIdentifier(ContentIdentifier moduleIdentifier) {
        this.moduleIdentifier = moduleIdentifier;
    }

    public String getRemedyId() {
        return remedyId;
    }

    public void setRemedyId(String remedyId) {
        this.remedyId = remedyId;
    }

    public Long getCallerId() {
        return callerId;
    }

    public void setCallerId(Long callerId) {
        this.callerId = callerId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public ContentIdentifier getCourseIdentifier() {
        return courseIdentifier;
    }

    public void setCourseIdentifier(ContentIdentifier courseIdentifier) {
        this.courseIdentifier = courseIdentifier;
    }

    public ContentIdentifier getChapterIdentifier() {
        return chapterIdentifier;
    }

    public void setChapterIdentifier(ContentIdentifier chapterIdentifier) {
        this.chapterIdentifier = chapterIdentifier;
    }

    public ContentIdentifier getQuizIdentifier() {
        return quizIdentifier;
    }

    public void setQuizIdentifier(ContentIdentifier quizIdentifier) {
        this.quizIdentifier = quizIdentifier;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public Boolean getIsPassed() {
        return isPassed;
    }

    public void setIsPassed(Boolean isPassed) {
        this.isPassed = isPassed;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Boolean getIncompleteAttempt() {
        return incompleteAttempt;
    }

    public void setIncompleteAttempt(Boolean incompleteAttempt) {
        this.incompleteAttempt = incompleteAttempt;
    }

    public List<QuestionAttempt> getQuestionAttempts() {
        return questionAttempts;
    }

    public void setQuestionAttempts(List<QuestionAttempt> questionAttempts) {
        this.questionAttempts = questionAttempts;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    public DateTime getCreationDate() {
        return super.getCreationDate();
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    public DateTime getModificationDate() {
        return super.getModificationDate();
    }
}
