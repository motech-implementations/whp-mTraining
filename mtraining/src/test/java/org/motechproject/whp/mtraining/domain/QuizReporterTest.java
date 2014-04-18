package org.motechproject.whp.mtraining.domain;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.constants.CourseStatus;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.CourseProgressDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.dto.QuestionDto;
import org.motechproject.mtraining.dto.QuestionResultDto;
import org.motechproject.mtraining.dto.QuizAnswerSheetDto;
import org.motechproject.mtraining.dto.QuizDto;
import org.motechproject.mtraining.dto.QuizResultSheetDto;
import org.motechproject.mtraining.exception.InvalidQuestionException;
import org.motechproject.mtraining.exception.InvalidQuizException;
import org.motechproject.mtraining.service.BookmarkService;
import org.motechproject.mtraining.service.CourseProgressService;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.mtraining.service.QuizService;
import org.motechproject.whp.mtraining.reports.QuizReporter;
import org.motechproject.whp.mtraining.reports.domain.QuestionAttempt;
import org.motechproject.whp.mtraining.reports.domain.QuizAttempt;
import org.motechproject.whp.mtraining.repository.AllCoursePublicationAttempts;
import org.motechproject.whp.mtraining.repository.AllQuestionAttempts;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.QuestionRequest;
import org.motechproject.whp.mtraining.web.domain.QuizReportRequest;
import org.motechproject.whp.mtraining.web.domain.QuizReportResponse;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.mtraining.util.ISODateTimeUtil.nowAsStringInTimeZoneUTC;

@RunWith(MockitoJUnitRunner.class)
public class QuizReporterTest {

    private QuizReporter quizReporter;

    @Mock
    private CourseProgressService courseProgressService;
    @Mock
    private AllQuestionAttempts allQuestionAttempts;
    @Mock
    private QuizService quizService;
    @Mock
    private BookmarkService bookmarkService;
    @Mock
    private CourseService courseService;
    @Mock
    private AllCoursePublicationAttempts allCoursePublicationAttempts;

    private UUID questionId;
    private ContentIdentifierDto courseContentIdentifier;
    private ContentIdentifierDto chapterContentIdentifier;
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
        quizReporter = new QuizReporter(bookmarkService, courseProgressService, quizService, allQuestionAttempts, courseService, allCoursePublicationAttempts);
        courseContentIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        moduleContentIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        chapterContentIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        quizContentIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        QuizDto quizDto = new QuizDto(quizContentIdentifier.getContentId(), 1, true, "quiz", "externalId", newArrayList(new QuestionDto()), 2, 60.0, null);

        ChapterDto chapterDto = new ChapterDto(chapterContentIdentifier.getContentId(), 1, true, "chapter1", "desc", "externalId", null, null, quizDto);

        ModuleDto moduleDto = new ModuleDto(moduleContentIdentifier.getContentId(), 1, true, "module1", "desc", "externalId", null, newArrayList(chapterDto));
        CourseDto courseDto = new CourseDto(courseContentIdentifier.getContentId(), 1, true, "course1", "desc", "externalId", null, newArrayList(moduleDto));
        when(courseService.getCourse(courseContentIdentifier)).thenReturn(courseDto);
    }

    @Test
    public void shouldLogInQuestionAttemptsAfterGettingResultFromQuizServiceAndUpdateToFirstActiveMessageOfNextChapterIfPassed() {
        List<QuestionRequest> questionRequests = newArrayList(new QuestionRequest(questionId, 1, newArrayList("a", "b"), "c", false, false));
        QuizReportRequest quizReportRequest = new QuizReportRequest(1L, "someId", "sessionId", courseContentIdentifier,
                moduleContentIdentifier, chapterContentIdentifier, quizContentIdentifier, questionRequests, startTime, endTime, false);
        QuestionResultDto questionResultDto = new QuestionResultDto(questionId, 1, "c", true);
        QuizResultSheetDto quizResultSheetDto = new QuizResultSheetDto("externalId", quizContentIdentifier, newArrayList(questionResultDto), 100.0, true);
        QuizAttempt quizAttempt = new QuizAttempt("externalId", 1L, "someId", "sessionId", courseContentIdentifier.getContentId(),
                courseContentIdentifier.getVersion(), moduleContentIdentifier.getContentId(), moduleContentIdentifier.getVersion(),
                chapterContentIdentifier.getContentId(), chapterContentIdentifier.getVersion(), courseContentIdentifier.getContentId(),
                courseContentIdentifier.getVersion(), null, null, true, 100.0, false);
        QuestionAttempt questionAttempt = new QuestionAttempt(quizAttempt, questionId, 1, "a;b", "c", true, false, false);
        BookmarkDto nextBookmark = new BookmarkDto("remediId", courseContentIdentifier, courseContentIdentifier, courseContentIdentifier, courseContentIdentifier, courseContentIdentifier, DateTime.now());
        when(quizService.getResult(any(QuizAnswerSheetDto.class))).thenReturn(quizResultSheetDto);
        when(bookmarkService.getNextBookmark(anyString(), any(ContentIdentifierDto.class), any(ContentIdentifierDto.class), any(ContentIdentifierDto.class))).thenReturn(nextBookmark);
        CourseProgressDto courseProgressDto = new CourseProgressDto("someId", DateTime.now(), null, CourseStatus.ONGOING);
        when(courseProgressService.getCourseProgressForEnrollee("remediId")).thenReturn(courseProgressDto);

        QuizReportResponse response = (QuizReportResponse) quizReporter.processAndLogQuiz("remediId", quizReportRequest);

        assertTrue(response.getPassed());
        assertEquals(Double.valueOf(100.0), response.getQuizScore());
        Class<List<QuestionAttempt>> listOfQuestionHistoryClass = (Class<List<QuestionAttempt>>) (Class) ArrayList.class;
        ArgumentCaptor<List<QuestionAttempt>> argument = ArgumentCaptor.forClass(listOfQuestionHistoryClass);
        verify(allQuestionAttempts).bulkAdd(argument.capture());
        List<QuestionAttempt> questionAttemptListCaptureValue = argument.getValue();
        assertEquals(questionAttempt.getInvalidInputs(), questionAttemptListCaptureValue.get(0).getInvalidInputs());
        assertTrue(questionAttemptListCaptureValue.get(0).isCorrectAnswer());
        assertEquals("OK", response.getResponseMessage());
        verify(bookmarkService).getNextBookmark(anyString(), any(ContentIdentifierDto.class), any(ContentIdentifierDto.class), any(ContentIdentifierDto.class));
        verify(courseProgressService).addOrUpdateCourseProgress(courseProgressDto);
    }

    @Test
    public void shouldLogInQuestionAttemptsAfterGettingResultAndUpdateToMarkCompletionIfNoNextActiveChapterFoundWhenPassed() {
        List<QuestionRequest> questionRequests = newArrayList(new QuestionRequest(questionId, 1, newArrayList("a", "b"), "c", false, false));
        QuizReportRequest quizReportRequest = new QuizReportRequest(1L, "someId", "sessionId", courseContentIdentifier,
                moduleContentIdentifier, chapterContentIdentifier, quizContentIdentifier, questionRequests, startTime, endTime, false);
        QuestionResultDto questionResultDto = new QuestionResultDto(questionId, 1, "c", true);
        QuizResultSheetDto quizResultSheetDto = new QuizResultSheetDto("externalId", quizContentIdentifier, newArrayList(questionResultDto), 100.0, true);
        QuizAttempt quizAttempt = new QuizAttempt("externalId", 1L, "someId", "sessionId", courseContentIdentifier.getContentId(),
                courseContentIdentifier.getVersion(), moduleContentIdentifier.getContentId(), moduleContentIdentifier.getVersion(),
                chapterContentIdentifier.getContentId(), chapterContentIdentifier.getVersion(), courseContentIdentifier.getContentId(),
                courseContentIdentifier.getVersion(), null, null, true, 100.0, false);
        QuestionAttempt questionAttempt = new QuestionAttempt(quizAttempt, questionId, 1, "a;b", "c", true, false, false);
        BookmarkDto nextBookmark = new BookmarkDto("remediId", courseContentIdentifier, null, null, null, courseContentIdentifier, DateTime.now());
        when(quizService.getResult(any(QuizAnswerSheetDto.class))).thenReturn(quizResultSheetDto);
        when(bookmarkService.getNextBookmark(anyString(), any(ContentIdentifierDto.class), any(ContentIdentifierDto.class), any(ContentIdentifierDto.class))).thenReturn(nextBookmark);
        when(courseProgressService.getCourseProgressForEnrollee("remediId")).thenReturn(new CourseProgressDto("someId", DateTime.now(), null, CourseStatus.ONGOING));
        QuizReportResponse response = (QuizReportResponse) quizReporter.processAndLogQuiz("remediId", quizReportRequest);

        assertTrue(response.getPassed());
        assertEquals(Double.valueOf(100.0), response.getQuizScore());
        Class<List<QuestionAttempt>> listOfQuestionHistoryClass = (Class<List<QuestionAttempt>>) (Class) ArrayList.class;
        ArgumentCaptor<List<QuestionAttempt>> argument = ArgumentCaptor.forClass(listOfQuestionHistoryClass);
        verify(allQuestionAttempts).bulkAdd(argument.capture());
        List<QuestionAttempt> questionAttemptListCaptureValue = argument.getValue();
        assertEquals(questionAttempt.getInvalidInputs(), questionAttemptListCaptureValue.get(0).getInvalidInputs());
        assertTrue(questionAttemptListCaptureValue.get(0).isCorrectAnswer());
        assertEquals("OK", response.getResponseMessage());
        verify(bookmarkService).getNextBookmark(anyString(), any(ContentIdentifierDto.class), any(ContentIdentifierDto.class), any(ContentIdentifierDto.class));
        verify(courseProgressService).markCourseAsComplete(anyString(), any(String.class), any(ContentIdentifierDto.class));

    }

    @Test
    public void shouldLogInQuestionAttemptsAfterGettingResultFromQuizServiceAndResetToQuizOfChapterIfIncompleteAttempt() {
        List<QuestionRequest> questionRequests = newArrayList(new QuestionRequest(questionId, 1, newArrayList("a", "b"), "c", false, false));
        QuizReportRequest quizReportRequest = new QuizReportRequest(1L, "someId", "sessionId", courseContentIdentifier,
                moduleContentIdentifier, chapterContentIdentifier, quizContentIdentifier, questionRequests, startTime, endTime, true);
        QuestionResultDto questionResultDto = new QuestionResultDto(questionId, 1, "c", true);
        QuizResultSheetDto quizResultSheetDto = new QuizResultSheetDto("externalId", quizContentIdentifier, newArrayList(questionResultDto), 100.0, false);
        QuizAttempt quizAttempt = new QuizAttempt("externalId", 1L, "someId", "sessionId", courseContentIdentifier.getContentId(),
                courseContentIdentifier.getVersion(), moduleContentIdentifier.getContentId(), moduleContentIdentifier.getVersion(),
                chapterContentIdentifier.getContentId(), chapterContentIdentifier.getVersion(), courseContentIdentifier.getContentId(),
                courseContentIdentifier.getVersion(), null, null, true, 100.0, false);
        QuestionAttempt questionAttempt = new QuestionAttempt(quizAttempt, questionId, 1, "a;b", "c", true, false, false);
        when(quizService.getResult(any(QuizAnswerSheetDto.class))).thenReturn(quizResultSheetDto);
        CourseProgressDto courseProgressDto = new CourseProgressDto("someId", DateTime.now(), null, CourseStatus.ONGOING);
        when(courseProgressService.getCourseProgressForEnrollee("remediId")).thenReturn(courseProgressDto);

        QuizReportResponse response = (QuizReportResponse) quizReporter.processAndLogQuiz("remediId", quizReportRequest);

        assertFalse(response.getPassed());
        assertEquals(Double.valueOf(100.0), response.getQuizScore());
        Class<List<QuestionAttempt>> listOfQuestionHistoryClass = (Class<List<QuestionAttempt>>) (Class) ArrayList.class;
        ArgumentCaptor<List<QuestionAttempt>> argument = ArgumentCaptor.forClass(listOfQuestionHistoryClass);
        verify(allQuestionAttempts).bulkAdd(argument.capture());
        List<QuestionAttempt> questionAttemptListCaptureValue = argument.getValue();
        assertEquals(questionAttempt.getInvalidInputs(), questionAttemptListCaptureValue.get(0).getInvalidInputs());
        assertTrue(questionAttemptListCaptureValue.get(0).isCorrectAnswer());
        assertEquals("OK", response.getResponseMessage());
        verify(bookmarkService).getBookmarkForQuizOfAChapter(anyString(), any(ContentIdentifierDto.class), any(ContentIdentifierDto.class), any(ContentIdentifierDto.class));
        verify(courseProgressService).addOrUpdateCourseProgress(courseProgressDto);

    }

    @Test
    public void shouldLogInQuestionAttemptsAfterGettingResultFromQuizServiceAndUpdateToFirstActiveMessageOfChapterIfFailed() {
        List<QuestionRequest> questionRequests = newArrayList(new QuestionRequest(questionId, 1, newArrayList("a", "b"), "c", false, false));
        QuizReportRequest quizReportRequest = new QuizReportRequest(1L, "someId", "sessionId", courseContentIdentifier,
                moduleContentIdentifier, chapterContentIdentifier, quizContentIdentifier, questionRequests, startTime, endTime, false);
        QuestionResultDto questionResultDto = new QuestionResultDto(questionId, 1, "c", true);
        QuizResultSheetDto quizResultSheetDto = new QuizResultSheetDto("externalId", quizContentIdentifier, newArrayList(questionResultDto), 40.0, false);
        QuizAttempt quizAttempt = new QuizAttempt("externalId", 1L, "someId", "sessionId", courseContentIdentifier.getContentId(),
                courseContentIdentifier.getVersion(), moduleContentIdentifier.getContentId(), moduleContentIdentifier.getVersion(),
                chapterContentIdentifier.getContentId(), chapterContentIdentifier.getVersion(), courseContentIdentifier.getContentId(),
                courseContentIdentifier.getVersion(), null, null, true, 100.0, false);
        QuestionAttempt questionAttempt = new QuestionAttempt(quizAttempt, questionId, 1, "a;b", "c", true, false, false);
        when(quizService.getResult(any(QuizAnswerSheetDto.class))).thenReturn(quizResultSheetDto);

        QuizReportResponse response = (QuizReportResponse) quizReporter.processAndLogQuiz("remediId", quizReportRequest);

        assertFalse(response.getPassed());
        assertEquals(Double.valueOf(40.0), response.getQuizScore());
        Class<List<QuestionAttempt>> listOfQuestionHistoryClass = (Class<List<QuestionAttempt>>) (Class) ArrayList.class;
        ArgumentCaptor<List<QuestionAttempt>> argument = ArgumentCaptor.forClass(listOfQuestionHistoryClass);
        verify(allQuestionAttempts).bulkAdd(argument.capture());
        List<QuestionAttempt> questionAttemptListCaptureValue = argument.getValue();
        assertEquals(questionAttempt.getInvalidInputs(), questionAttemptListCaptureValue.get(0).getInvalidInputs());
        assertTrue(questionAttemptListCaptureValue.get(0).isCorrectAnswer());
        assertEquals("OK", response.getResponseMessage());
        verify(bookmarkService).setBookmarkToFirstActiveContentOfAChapter(anyString(), any(ContentIdentifierDto.class), any(ContentIdentifierDto.class), any(ContentIdentifierDto.class));

    }

    @Test
    public void shouldNotLogInQuestionAttemptsIfExceptionThrownByQuizServiceForInvalidQuizId() {
        ArgumentCaptor<QuizAnswerSheetDto> quizAnswerSheetDtoArgumentCaptor = ArgumentCaptor.forClass(QuizAnswerSheetDto.class);
        List<QuestionRequest> questionRequests = newArrayList(new QuestionRequest(questionId, 1, newArrayList("a", "b"), "c", false, false));
        QuizReportRequest quizReportRequest = new QuizReportRequest(1L, "someId", "sessionId", courseContentIdentifier,
                moduleContentIdentifier, chapterContentIdentifier, quizContentIdentifier, questionRequests, startTime, endTime, false);

        doThrow(new InvalidQuizException(UUID.randomUUID())).when(quizService).getResult(quizAnswerSheetDtoArgumentCaptor.capture());

        MotechResponse response = quizReporter.processAndLogQuiz("remediId", quizReportRequest);
        assertEquals(ResponseStatus.QUIZ_NOT_FOUND.getCode(), response.getResponseCode());

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

    @Test
    public void shouldReturnResponseAsInvalidCourseForInvalidCourseId() {
        List<QuestionRequest> questionRequests = newArrayList(new QuestionRequest(questionId, 1, newArrayList("a", "b"), "c", false, false));
        QuizReportRequest quizReportRequest = new QuizReportRequest(1L, "someId", "sessionId", new ContentIdentifierDto(),
                moduleContentIdentifier, chapterContentIdentifier, quizContentIdentifier, questionRequests, startTime, endTime, false);

        MotechResponse response = quizReporter.processAndLogQuiz("remediId", quizReportRequest);
        assertEquals(ResponseStatus.INVALID_COURSE.getCode(), response.getResponseCode());
    }

    @Test
    public void shouldReturnResponseAsInvalidModuleForInvalidModuleId() {
        List<QuestionRequest> questionRequests = newArrayList(new QuestionRequest(questionId, 1, newArrayList("a", "b"), "c", false, false));
        QuizReportRequest quizReportRequest = new QuizReportRequest(1L, "someId", "sessionId", courseContentIdentifier,
                new ContentIdentifierDto(), chapterContentIdentifier, quizContentIdentifier, questionRequests, startTime, endTime, false);

        MotechResponse response = quizReporter.processAndLogQuiz("remediId", quizReportRequest);
        assertEquals(ResponseStatus.INVALID_MODULE.getCode(), response.getResponseCode());
    }

    @Test
    public void shouldReturnResponseAsInvalidChapterForInvalidChapterId() {
        List<QuestionRequest> questionRequests = newArrayList(new QuestionRequest(questionId, 1, newArrayList("a", "b"), "c", false, false));
        QuizReportRequest quizReportRequest = new QuizReportRequest(1L, "someId", "sessionId", courseContentIdentifier,
                moduleContentIdentifier, new ContentIdentifierDto(), quizContentIdentifier, questionRequests, startTime, endTime, false);

        MotechResponse response = quizReporter.processAndLogQuiz("remediId", quizReportRequest);
        assertEquals(ResponseStatus.INVALID_CHAPTER.getCode(), response.getResponseCode());
    }

    @Test
    public void shouldReturnResponseAsInvalidQuizForInvalidQuizId() {
        List<QuestionRequest> questionRequests = newArrayList(new QuestionRequest(questionId, 1, newArrayList("a", "b"), "c", false, false));
        QuizReportRequest quizReportRequest = new QuizReportRequest(1L, "someId", "sessionId", courseContentIdentifier,
                moduleContentIdentifier, chapterContentIdentifier, new ContentIdentifierDto(), questionRequests, startTime, endTime, false);

        MotechResponse response = quizReporter.processAndLogQuiz("remediId", quizReportRequest);
        assertEquals(ResponseStatus.QUIZ_NOT_FOUND.getCode(), response.getResponseCode());
    }
}
