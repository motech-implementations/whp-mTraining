package org.motechproject.whp.mtraining.web.domain;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_DATE_TIME;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_NODE;

public class QuizReportRequest extends IVRRequest {

    @JsonProperty("course")
    private Long course;
    @JsonProperty("chapter")
    private Long chapter;
    @JsonProperty("quiz")
    private Long quiz;
    @JsonProperty("questions")
    private List<QuestionRequest> questionRequests;
    @JsonProperty
    private String startTime;
    @JsonProperty
    private String endTime;
    @JsonProperty
    private Boolean incompleteAttempt=false;

    public QuizReportRequest() {
    }

    public QuizReportRequest(Long callerId, String uniqueId, String sessionId, Long course, Long chapter, Long quiz,
                             List<QuestionRequest> questionRequests, String startTime, String endTime, Boolean incompleteAttempt) {
        super(callerId, uniqueId, sessionId);
        this.course = course;
        this.chapter = chapter;
        this.quiz = quiz;
        this.questionRequests = questionRequests;
        this.startTime = startTime;
        this.endTime = endTime;
        this.incompleteAttempt = incompleteAttempt;
    }

    public List<ValidationError> validate() {
        List<ValidationError> validationErrors = super.validate();
        if (isNotEmpty(validationErrors))
            return validationErrors;
        if (course == null)// || courseDto.getVersion() == null)
            validationErrors.add(errorMessage(MISSING_NODE, "Course"));
        if (chapter == null)// || chapterDto.getVersion() == null)
            validationErrors.add(errorMessage(MISSING_NODE, "Chapter"));
        if (quiz == null)// || quizDto.getVersion() == null)
            validationErrors.add(errorMessage(MISSING_NODE, "Quiz"));
        if (isBlank(startTime) || !ISODateTimeUtil.validate(startTime))
            validationErrors.add(errorMessage(INVALID_DATE_TIME, "Start Time"));
        if (isBlank(endTime) || !ISODateTimeUtil.validate(endTime))
            validationErrors.add(errorMessage(INVALID_DATE_TIME, "End Time"));

        if (CollectionUtils.isEmpty(questionRequests)) {
            validationErrors.add(new ValidationError(ResponseStatus.MISSING_QUESTION));
            return validationErrors;
        }
        for (QuestionRequest questionRequest : questionRequests) {
            List<ValidationError> errors = questionRequest.validate();
            validationErrors.addAll(errors);
        }
        return validationErrors;
    }

    public Long getCourse() {
        return course;
    }

    public Long getChapter() {
        return chapter;
    }

    public Long getQuiz() {
        return quiz;
    }

    public List<QuestionRequest> getQuestionRequests() {
        return questionRequests;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    private ValidationError errorMessage(ResponseStatus status, String content) {
        String message = status.getMessage().concat(" for: " + content);
        return new ValidationError(status.getCode(), message);
    }

    public Boolean IsIncompleteAttempt() {
        return incompleteAttempt;
    }
}