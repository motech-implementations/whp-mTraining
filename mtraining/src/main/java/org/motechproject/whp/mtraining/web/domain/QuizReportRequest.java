package org.motechproject.whp.mtraining.web.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.mtraining.dto.ContentIdentifierDto;

import java.util.List;

import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_NODE;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.OK;

public class QuizReportRequest extends CallDetailsRequest {

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
    private DateTime startTime;
    @JsonProperty
    private DateTime endTime;

    public QuizReportRequest() {
    }

    public QuizReportRequest(Long callerId, String uniqueId, String sessionId, ContentIdentifierDto courseDto, ContentIdentifierDto moduleDto, ContentIdentifierDto chapterDto, ContentIdentifierDto quizDto, List<QuestionRequest> questionRequests, DateTime startTime, DateTime endTime) {
        super(callerId, uniqueId, sessionId);
        this.courseDto = courseDto;
        this.moduleDto = moduleDto;
        this.chapterDto = chapterDto;
        this.quizDto = quizDto;
        this.questionRequests = questionRequests;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public ResponseStatus validate() {
        ResponseStatus validationStatus = super.validate();
        if (!validationStatus.isValid())
            return validationStatus;
        if (courseDto.getContentId() == null || courseDto.getVersion() == null)
            return MISSING_NODE.appendMessage("for content: Course");
        if (moduleDto.getContentId() == null || moduleDto.getVersion() == null)
            return MISSING_NODE.appendMessage("for content: Module");
        if (chapterDto.getContentId() == null || chapterDto.getVersion() == null)
            return MISSING_NODE.appendMessage("for content: Chapter");
        if (quizDto.getContentId() == null || quizDto.getVersion() == null)
            return MISSING_NODE.appendMessage("for content: Quiz");
        if (questionRequests.isEmpty())
            return ResponseStatus.MISSING_QUESTION;
        for (QuestionRequest questionRequest : questionRequests) {
            ResponseStatus validate = questionRequest.validate();
            if (!validate.isValid()) return validate;
        }
        return OK;
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

    public DateTime getStartTime() {
        return startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }
}