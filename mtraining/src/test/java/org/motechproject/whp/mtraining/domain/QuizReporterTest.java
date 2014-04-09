package org.motechproject.whp.mtraining.domain;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.QuestionResultDto;
import org.motechproject.mtraining.dto.QuizAnswerSheetDto;
import org.motechproject.mtraining.dto.QuizResultSheetDto;
import org.motechproject.mtraining.exception.InvalidQuestionException;
import org.motechproject.mtraining.exception.InvalidQuizException;
import org.motechproject.mtraining.service.BookmarkService;
import org.motechproject.mtraining.service.QuizService;
import org.motechproject.whp.mtraining.reports.QuizReporter;
import org.motechproject.whp.mtraining.reports.domain.QuestionHistory;
import org.motechproject.whp.mtraining.reports.domain.QuizHistory;
import org.motechproject.whp.mtraining.repository.AllQuestionHistories;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.QuestionRequest;
import org.motechproject.whp.mtraining.web.domain.QuizReportRequest;
import org.motechproject.whp.mtraining.web.domain.QuizReportResponse;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.mtraining.util.ISODateTimeUtil.nowAsStringInTimeZoneUTC;

@RunWith(MockitoJUnitRunner.class)
public class QuizReporterTest {

    private QuizReporter quizReporter;

    @Mock
    private BookmarkService bookmarkService;
    @Mock
    private AllQuestionHistories allQuestionHistories;
    @Mock
    private QuizService quizService;

    private UUID questionId;
    private ContentIdentifierDto courseContentIdentifier;
    private ContentIdentifierDto chapterContentIdentifier;
    private ContentIdentifierDto messageContentIdentifier;
    private ContentIdentifierDto quizContentIdentifier;
    private ContentIdentifierDto moduleContentIdentifier;
    private String startTime;
    private String endTime;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        initMocks(this);
        questionId = UUID.randomUUID();
        startTime = nowAsStringInTimeZoneUTC();
        endTime = nowAsStringInTimeZoneUTC();
        quizReporter = new QuizReporter(bookmarkService, quizService, allQuestionHistories);
        courseContentIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        moduleContentIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        chapterContentIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        quizContentIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
    }

    @Test
    public void shouldLogInQuestionAttemptsAfterGettingResultFromQuizServiceAndUpdateToFirstActiveMessageOfNextChapterIfPassed() {
        List<QuestionRequest> questionRequests = newArrayList(new QuestionRequest(questionId, 1, newArrayList("a", "b"), "c", false, false));
        QuizReportRequest quizReportRequest = new QuizReportRequest(1L, "someId", "sessionId", courseContentIdentifier,
                moduleContentIdentifier, chapterContentIdentifier, quizContentIdentifier, questionRequests, startTime, endTime, false);
        QuestionResultDto questionResultDto = new QuestionResultDto(questionId, 1, "c", true);
        QuizResultSheetDto quizResultSheetDto = new QuizResultSheetDto("externalId", quizContentIdentifier, newArrayList(questionResultDto), 100.0, true);
        QuizHistory quizHistory = new QuizHistory("externalId", 1L, "someId", "sessionId", courseContentIdentifier.getContentId(),
                courseContentIdentifier.getVersion(), null, null, true, 100.0, false);
        QuestionHistory questionHistory = new QuestionHistory(quizHistory, questionId, 1, "a;b", "c", true, false, false);
        when(quizService.getResult(any(QuizAnswerSheetDto.class))).thenReturn(quizResultSheetDto);

        QuizReportResponse response = (QuizReportResponse) quizReporter.processAndLogQuiz("remediId", quizReportRequest);

        assertTrue(response.getPassed());
        assertEquals(Double.valueOf(100.0), response.getQuizScore());
        Class<List<QuestionHistory>> listOfQuestionHistoryClass = (Class<List<QuestionHistory>>) (Class) ArrayList.class;
        ArgumentCaptor<List<QuestionHistory>> argument = ArgumentCaptor.forClass(listOfQuestionHistoryClass);
        verify(allQuestionHistories).bulkAdd(argument.capture());
        List<QuestionHistory> questionHistoryListCaptureValue = argument.getValue();
        assertEquals(questionHistory.getInvalidInputs(), questionHistoryListCaptureValue.get(0).getInvalidInputs());
        assertTrue(questionHistoryListCaptureValue.get(0).getStatus());
        assertEquals("OK", response.getResponseMessage());
        verify(bookmarkService).setToNextBookmark(anyString(), any(ContentIdentifierDto.class), any(ContentIdentifierDto.class), any(ContentIdentifierDto.class));

    }

    @Test
    public void shouldLogInQuestionAttemptsAfterGettingResultFromQuizServiceAndResetToQuizOfChapterIfIncompleteAttempt() {
        List<QuestionRequest> questionRequests = newArrayList(new QuestionRequest(questionId, 1, newArrayList("a", "b"), "c", false, false));
        QuizReportRequest quizReportRequest = new QuizReportRequest(1L, "someId", "sessionId", courseContentIdentifier,
                moduleContentIdentifier, chapterContentIdentifier, quizContentIdentifier, questionRequests, startTime, endTime, true);
        QuestionResultDto questionResultDto = new QuestionResultDto(questionId, 1, "c", true);
        QuizResultSheetDto quizResultSheetDto = new QuizResultSheetDto("externalId", quizContentIdentifier, newArrayList(questionResultDto), 100.0, false);
        QuizHistory quizHistory = new QuizHistory("externalId", 1L, "someId", "sessionId", courseContentIdentifier.getContentId(),
                courseContentIdentifier.getVersion(), null, null, true, 100.0, false);
        QuestionHistory questionHistory = new QuestionHistory(quizHistory, questionId, 1, "a;b", "c", true, false, false);
        when(quizService.getResult(any(QuizAnswerSheetDto.class))).thenReturn(quizResultSheetDto);

        QuizReportResponse response = (QuizReportResponse) quizReporter.processAndLogQuiz("remediId", quizReportRequest);

        assertFalse(response.getPassed());
        assertEquals(Double.valueOf(100.0), response.getQuizScore());
        Class<List<QuestionHistory>> listOfQuestionHistoryClass = (Class<List<QuestionHistory>>) (Class) ArrayList.class;
        ArgumentCaptor<List<QuestionHistory>> argument = ArgumentCaptor.forClass(listOfQuestionHistoryClass);
        verify(allQuestionHistories).bulkAdd(argument.capture());
        List<QuestionHistory> questionHistoryListCaptureValue = argument.getValue();
        assertEquals(questionHistory.getInvalidInputs(), questionHistoryListCaptureValue.get(0).getInvalidInputs());
        assertTrue(questionHistoryListCaptureValue.get(0).getStatus());
        assertEquals("OK", response.getResponseMessage());
        verify(bookmarkService).resetBookmark(anyString(), any(ContentIdentifierDto.class), any(ContentIdentifierDto.class), any(ContentIdentifierDto.class));

    }

    @Test
    public void shouldLogInQuestionAttemptsAfterGettingResultFromQuizServiceAndUpdateToFirstActiveMessageOfChapterIfFailed() {
        List<QuestionRequest> questionRequests = newArrayList(new QuestionRequest(questionId, 1, newArrayList("a", "b"), "c", false, false));
        QuizReportRequest quizReportRequest = new QuizReportRequest(1L, "someId", "sessionId", courseContentIdentifier,
                moduleContentIdentifier, chapterContentIdentifier, quizContentIdentifier, questionRequests, startTime, endTime, false);
        QuestionResultDto questionResultDto = new QuestionResultDto(questionId, 1, "c", true);
        QuizResultSheetDto quizResultSheetDto = new QuizResultSheetDto("externalId", quizContentIdentifier, newArrayList(questionResultDto), 40.0, false);
        QuizHistory quizHistory = new QuizHistory("externalId", 1L, "someId", "sessionId", courseContentIdentifier.getContentId(),
                courseContentIdentifier.getVersion(), null, null, true, 100.0, false);
        QuestionHistory questionHistory = new QuestionHistory(quizHistory, questionId, 1, "a;b", "c", true, false, false);
        when(quizService.getResult(any(QuizAnswerSheetDto.class))).thenReturn(quizResultSheetDto);

        QuizReportResponse response = (QuizReportResponse) quizReporter.processAndLogQuiz("remediId", quizReportRequest);

        assertFalse(response.getPassed());
        assertEquals(Double.valueOf(40.0), response.getQuizScore());
        Class<List<QuestionHistory>> listOfQuestionHistoryClass = (Class<List<QuestionHistory>>) (Class) ArrayList.class;
        ArgumentCaptor<List<QuestionHistory>> argument = ArgumentCaptor.forClass(listOfQuestionHistoryClass);
        verify(allQuestionHistories).bulkAdd(argument.capture());
        List<QuestionHistory> questionHistoryListCaptureValue = argument.getValue();
        assertEquals(questionHistory.getInvalidInputs(), questionHistoryListCaptureValue.get(0).getInvalidInputs());
        assertTrue(questionHistoryListCaptureValue.get(0).getStatus());
        assertEquals("OK", response.getResponseMessage());
        verify(bookmarkService).resetBookmarkToFirstMessageOfAChapter(anyString(), any(ContentIdentifierDto.class), any(ContentIdentifierDto.class), any(ContentIdentifierDto.class));

    }

    @Test
    public void shouldNotLogInQuestionAttemptsIfExceptionThrownByQuizServiceForInvalidQuizId() {
        ArgumentCaptor<QuizAnswerSheetDto> quizAnswerSheetDtoArgumentCaptor = ArgumentCaptor.forClass(QuizAnswerSheetDto.class);
        List<QuestionRequest> questionRequests = newArrayList(new QuestionRequest(questionId, 1, newArrayList("a", "b"), "c", false, false));
        QuizReportRequest quizReportRequest = new QuizReportRequest(1L, "someId", "sessionId", courseContentIdentifier,
                moduleContentIdentifier, chapterContentIdentifier, quizContentIdentifier, questionRequests, startTime, endTime, false);

        doThrow(new InvalidQuizException(UUID.randomUUID())).when(quizService).getResult(quizAnswerSheetDtoArgumentCaptor.capture());

        MotechResponse response = quizReporter.processAndLogQuiz("remediId", quizReportRequest);
        assertEquals(ResponseStatus.INVALID_QUIZ.getCode(), response.getResponseCode());

    }

    @Test
    public void shouldNotLogInQuestionAttemptsIfExceptionThrownByQuizServiceForInvalidQuestionId() {
        ArgumentCaptor<QuizAnswerSheetDto> quizAnswerSheetDtoArgumentCaptor = ArgumentCaptor.forClass(QuizAnswerSheetDto.class);
        List<QuestionRequest> questionRequests = newArrayList(new QuestionRequest(questionId, 1, newArrayList("a", "b"), "c", false, false));
        QuizReportRequest quizReportRequest = new QuizReportRequest(1L, "someId", "sessionId", courseContentIdentifier,
                moduleContentIdentifier, chapterContentIdentifier, quizContentIdentifier, questionRequests, startTime, endTime, false);

        doThrow(new InvalidQuestionException(UUID.randomUUID(), UUID.randomUUID())).when(quizService).getResult(quizAnswerSheetDtoArgumentCaptor.capture());

        MotechResponse response = quizReporter.processAndLogQuiz("remediId", quizReportRequest);
        assertEquals(ResponseStatus.INVALID_QUESTION.getCode(), response.getResponseCode());

    }
}
