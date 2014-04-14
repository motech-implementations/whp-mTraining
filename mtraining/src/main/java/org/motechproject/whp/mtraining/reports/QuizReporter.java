package org.motechproject.whp.mtraining.reports;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mtraining.dto.AnswerSheetDto;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.QuestionResultDto;
import org.motechproject.mtraining.dto.QuizAnswerSheetDto;
import org.motechproject.mtraining.dto.QuizResultSheetDto;
import org.motechproject.mtraining.exception.InvalidQuestionException;
import org.motechproject.mtraining.exception.InvalidQuizException;
import org.motechproject.mtraining.service.BookmarkService;
import org.motechproject.mtraining.service.CourseProgressService;
import org.motechproject.mtraining.service.QuizService;
import org.motechproject.whp.mtraining.reports.domain.QuestionHistory;
import org.motechproject.whp.mtraining.reports.domain.QuizHistory;
import org.motechproject.whp.mtraining.repository.AllQuestionHistories;
import org.motechproject.whp.mtraining.web.domain.BasicResponse;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.QuestionRequest;
import org.motechproject.whp.mtraining.web.domain.QuizReportRequest;
import org.motechproject.whp.mtraining.web.domain.QuizReportResponse;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.collections.CollectionUtils.find;
import static org.motechproject.mtraining.util.ISODateTimeUtil.parse;

@Component
public class QuizReporter {
    private BookmarkService bookmarkService;
    private CourseProgressService courseProgressService;
    private QuizService quizService;
    private AllQuestionHistories allQuestionHistories;

    @Autowired
    public QuizReporter(BookmarkService bookmarkService, CourseProgressService courseProgressService, QuizService quizService, AllQuestionHistories allQuestionHistories) {
        this.bookmarkService = bookmarkService;
        this.courseProgressService = courseProgressService;
        this.quizService = quizService;
        this.allQuestionHistories = allQuestionHistories;
    }

    public MotechResponse processAndLogQuiz(String remediId, QuizReportRequest quizReportRequest) {
        QuizResultSheetDto quizResult = null;
        try {
            quizResult = quizService.getResult(toQuizAnswerSheetDto(remediId, quizReportRequest));
            logQuizResult(remediId, quizReportRequest, quizResult);
        } catch (InvalidQuestionException ex) {
            return new BasicResponse(quizReportRequest.getCallerId(), quizReportRequest.getSessionId(), quizReportRequest.getUniqueId(), ResponseStatus.INVALID_QUESTION);
        } catch (InvalidQuizException ex) {
            return new BasicResponse(quizReportRequest.getCallerId(), quizReportRequest.getSessionId(), quizReportRequest.getUniqueId(), ResponseStatus.INVALID_QUIZ);
        }
        updateBookmark(remediId, quizResult, quizReportRequest);
        return new QuizReportResponse(quizReportRequest.getCallerId(), quizReportRequest.getSessionId(), quizReportRequest.getUniqueId(),
                quizResult.getScore(), quizResult.isPassed(), ResponseStatus.OK);

    }

    private void logQuizResult(String remediId, QuizReportRequest quizReportRequest, QuizResultSheetDto quizResult) {
        QuizHistory quizHistory = new QuizHistory(remediId, quizReportRequest.getCallerId(), quizReportRequest.getUniqueId(), quizReportRequest.getSessionId(),
                quizReportRequest.getQuizDto().getContentId(), quizReportRequest.getQuizDto().getVersion(), parse(quizReportRequest.getStartTime()), parse(quizReportRequest.getEndTime()), quizResult.isPassed(), quizResult.getScore(), quizReportRequest.IsIncompleteAttempt());
        List<QuestionHistory> questionHistories = new ArrayList<>();
        for (QuestionResultDto questionResultDto : quizResult.getQuestionResultDtos()) {
            QuestionRequest questionRequest = (QuestionRequest) findContentByContentId(quizReportRequest.getQuestionRequests(), questionResultDto.getQuestionId());
            questionHistories.add(new QuestionHistory(quizHistory, questionResultDto.getQuestionId(), questionResultDto.getVersion(),
                    StringUtils.join(questionRequest.getInvalidInputs(), ';'), questionRequest.getSelectedOption(), questionResultDto.isCorrect(), questionRequest.getInvalidAttempt(), questionRequest.getTimeOut()));
        }
        allQuestionHistories.bulkAdd(questionHistories);
    }

    private void updateBookmark(String remediId, QuizResultSheetDto quizResult, QuizReportRequest quizReportRequest) {
        ContentIdentifierDto courseDto = quizReportRequest.getCourseDto();
        ContentIdentifierDto moduleDto = quizReportRequest.getModuleDto();
        ContentIdentifierDto chapterDto = quizReportRequest.getChapterDto();
        if (quizReportRequest.IsIncompleteAttempt()) {
            bookmarkService.setBookmarkToQuizOfAChapter(remediId, courseDto, moduleDto, chapterDto);
        } else {
            if (quizResult.isPassed()) {
                BookmarkDto nextBookmark = bookmarkService.getNextBookmark(remediId, courseDto, moduleDto, chapterDto);
                if (nextBookmark.getModule() == null && nextBookmark.getChapter() == null) {
                    courseProgressService.markCourseAsComplete(remediId, quizReportRequest.getStartTime(), courseDto);
                }
            } else {
                bookmarkService.setBookmarkToFirstActiveContentOfAChapter(remediId, courseDto, moduleDto, chapterDto);
            }
        }
    }

    private QuizAnswerSheetDto toQuizAnswerSheetDto(String remediId, QuizReportRequest quizReportRequest) {
        return new QuizAnswerSheetDto(remediId, quizReportRequest.getQuizDto(), toAnswerSheetDtos(quizReportRequest.getQuestionRequests()));
    }

    private List<AnswerSheetDto> toAnswerSheetDtos(List<QuestionRequest> questionRequests) {

        List<AnswerSheetDto> answerSheetDtos = new ArrayList<>();
        for (QuestionRequest questionRequest : questionRequests) {
            answerSheetDtos.add(new AnswerSheetDto(questionRequest.getQuestionId(), questionRequest.getVersion(), questionRequest.getSelectedOption()));
        }
        return answerSheetDtos;
    }

    private static Object findContentByContentId(List<QuestionRequest> contents, final UUID contentId) {
        return find(contents, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                QuestionRequest questionRequest = (QuestionRequest) o;
                return contentId.equals(questionRequest.getQuestionId());
            }
        });
    }
}