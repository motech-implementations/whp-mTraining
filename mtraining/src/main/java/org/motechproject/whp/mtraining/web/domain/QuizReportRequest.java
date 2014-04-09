package org.motechproject.whp.mtraining.web.domain;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.util.ISODateTimeUtil;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_DATE_TIME;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_NODE;

public class QuizReportRequest extends IVRRequest {

    @JsonProperty("course")
    private ContentIdentifierDto courseDto;
    @JsonProperty("module")
    private ContentIdentifierDto moduleDto;
    @JsonProperty("chapter")
    private ContentIdentifierDto chapterDto;
    @JsonProperty("quiz")
    private ContentIdentifierDto quizDto;
    @JsonProperty("questions")
    private List<QuestionRequest> questionRequests;
    @JsonProperty
    private String startTime;
    @JsonProperty
    private String endTime;
    @JsonProperty
    private Boolean incompleteAttempt;

    public QuizReportRequest() {
    }

    public QuizReportRequest(Long callerId, String uniqueId, String sessionId, ContentIdentifierDto courseDto, ContentIdentifierDto moduleDto, ContentIdentifierDto chapterDto, ContentIdentifierDto quizDto,
                             List<QuestionRequest> questionRequests, String startTime, String endTime, Boolean incompleteAttempt) {
        super(callerId, uniqueId, sessionId);
        this.courseDto = courseDto;
        this.moduleDto = moduleDto;
        this.chapterDto = chapterDto;
        this.quizDto = quizDto;
        this.questionRequests = questionRequests;
        this.startTime = startTime;
        this.endTime = endTime;
        this.incompleteAttempt = incompleteAttempt;
    }

    public List<ValidationError> validate() {
        List<ValidationError> validationErrors = super.validate();
        if (isNotEmpty(validationErrors))
            return validationErrors;
        if (courseDto.getContentId() == null || courseDto.getVersion() == null)
            validationErrors.add(errorMessage(MISSING_NODE, "Course"));
        if (moduleDto.getContentId() == null || moduleDto.getVersion() == null)
            validationErrors.add(errorMessage(MISSING_NODE, "Module"));
        if (chapterDto.getContentId() == null || chapterDto.getVersion() == null)
            validationErrors.add(errorMessage(MISSING_NODE, "Chapter"));
        if (quizDto.getContentId() == null || quizDto.getVersion() == null)
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

    public ContentIdentifierDto getCourseDto() {
        return courseDto;
    }

    public ContentIdentifierDto getModuleDto() {
        return moduleDto;
    }

    public ContentIdentifierDto getChapterDto() {
        return chapterDto;
    }

    public ContentIdentifierDto getQuizDto() {
        return quizDto;
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