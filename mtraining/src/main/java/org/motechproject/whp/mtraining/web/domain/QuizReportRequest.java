package org.motechproject.whp.mtraining.web.domain;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.whp.mtraining.domain.ContentIdentifier;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_DATE_TIME;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_NODE;

public class QuizReportRequest extends IVRRequest {

    @JsonProperty("course")
    private ContentIdentifier course;
    @JsonProperty("module")
    private ContentIdentifier module;
    @JsonProperty("chapter")
    private ContentIdentifier chapter;
    @JsonProperty("quiz")
    private ContentIdentifier quiz;
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

    public QuizReportRequest(long callerId, String uniqueId, String sessionId, ContentIdentifier course, ContentIdentifier module, ContentIdentifier chapter, ContentIdentifier quiz,
                             List<QuestionRequest> questionRequests, String startTime, String endTime, Boolean incompleteAttempt) {
        super(callerId, uniqueId, sessionId);
        this.course = course;
        this.module = module;
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
        if (course == null)// || course.getVersion() == null)
            validationErrors.add(errorMessage(MISSING_NODE, "Course"));
        if (chapter == null)// || chapter.getVersion() == null)
            validationErrors.add(errorMessage(MISSING_NODE, "Chapter"));
        if (quiz == null)// || quiz.getVersion() == null)
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

    public ContentIdentifier getCourse() {
        return course;
    }

    public ContentIdentifier getModule() {
        return module;
    }

    public ContentIdentifier getChapter() {
        return chapter;
    }

    public ContentIdentifier getQuiz() {
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