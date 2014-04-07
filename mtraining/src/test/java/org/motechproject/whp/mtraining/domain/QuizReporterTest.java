package org.motechproject.whp.mtraining.domain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.mtraining.dto.AnswerDto;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.dto.QuestionDto;
import org.motechproject.mtraining.dto.QuizDto;
import org.motechproject.mtraining.service.BookmarkService;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.whp.mtraining.reports.QuizReporter;
import org.motechproject.whp.mtraining.repository.AllQuestionHistories;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.ProviderStatus;
import org.motechproject.whp.mtraining.web.domain.QuestionRequest;
import org.motechproject.whp.mtraining.web.domain.QuizReportRequest;
import org.motechproject.whp.mtraining.web.domain.QuizReportResponse;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.mtraining.util.ISODateTimeUtil.nowAsStringInTimeZoneUTC;

public class QuizReporterTest {

    private QuizReporter quizReporter;

    @Mock
    private ProviderService providerService;
    @Mock
    private BookmarkService bookmarkService;
    @Mock
    private CourseService courseService;
    @Mock
    private AllQuestionHistories allQuestionHistories;

    private UUID questionId;
    private ContentIdentifierDto courseContentIdentifier;
    private ContentIdentifierDto chapterContentIdentifier;
    private ContentIdentifierDto messageContentIdentifier;
    private ContentIdentifierDto quizContentIdentifier;
    private List<QuestionRequest> questionRequests;
    private ContentIdentifierDto moduleContentIdentifier;
    private List<QuestionDto> questions;
    private QuizDto quizDto;
    private UUID newChapterContentId;
    private List<MessageDto> messageDto;
    private List<ChapterDto> chapters;
    private List<ModuleDto> modules;
    private UUID newModuleContentId;
    private String startTime;
    private String endTime;

    @Before
    public void setUp() {
        initMocks(this);
        questionId = UUID.randomUUID();
        startTime = nowAsStringInTimeZoneUTC();
        endTime = nowAsStringInTimeZoneUTC();
        quizReporter = new QuizReporter(providerService, courseService, bookmarkService, allQuestionHistories);
        courseContentIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        moduleContentIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        chapterContentIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        messageContentIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        quizContentIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        questionRequests = newArrayList(new QuestionRequest(questionId, Collections.EMPTY_LIST, "b", false, false));
        questions = newArrayList(new QuestionDto(questionId, 1, true, "q1", "", "someId",
                new AnswerDto("b", null), Collections.EMPTY_LIST, null));
        quizDto = new QuizDto(quizContentIdentifier.getContentId(), 1, true, "some quiz,", questions, 1, 50L, null);
        newChapterContentId = UUID.randomUUID();
        newModuleContentId = UUID.randomUUID();
        messageDto = newArrayList(new MessageDto(messageContentIdentifier.getContentId(), messageContentIdentifier.getVersion(), true,
                "message1", "someFile", null, null));
        ChapterDto chapter1 = new ChapterDto(chapterContentIdentifier.getContentId(), chapterContentIdentifier.getVersion(), true,
                "chapter1", "", "", null, quizDto);
        ChapterDto chapter2 = new ChapterDto(newChapterContentId, chapterContentIdentifier.getVersion(), true,
                "chapter2", "", "", messageDto, quizDto);
        chapters = newArrayList(chapter1, chapter2);
        modules = newArrayList(new ModuleDto(moduleContentIdentifier.getContentId(), moduleContentIdentifier.getVersion(), true,
                "module1", "", null, chapters), new ModuleDto(newModuleContentId, moduleContentIdentifier.getVersion(), true,
                "module2", "", null, newArrayList(chapter2)));
        CourseDto courseDto = new CourseDto(courseContentIdentifier.getContentId(), courseContentIdentifier.getVersion(), true, "course1",
                "", "", modules);
        Provider provider = new Provider("someId", 1L, ProviderStatus.WORKING_PROVIDER, null);
        when(providerService.byCallerId(1L)).thenReturn(provider);
        when(courseService.getCourse(courseContentIdentifier)).thenReturn(courseDto);

    }

    @Test
    public void shouldReturnResponseAsInvalidQuestionIfQuestionNotFound() {
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto();
        List<QuestionRequest> questionRequests = newArrayList(new QuestionRequest(UUID.randomUUID(), Collections.EMPTY_LIST, "", false, false));
        QuizReportRequest quizReportRequest = new QuizReportRequest(1L, "someId", "sessionId", contentIdentifierDto,
                contentIdentifierDto, contentIdentifierDto,
                contentIdentifierDto, questionRequests, startTime, endTime);

        MotechResponse response = quizReporter.validateAndProcessQuiz(quizDto, quizReportRequest);

        assertEquals(ResponseStatus.INVALID_QUESTION.getCode(), response.getResponseCode());
    }

    @Test
    public void shouldResetBookmarkTOQuizOfChapterAndReturnResultAsNotPassedWhenUserHasScoredLessThanPassPercentageAndChapterHasNoMessage() {
        List<QuestionRequest> questionRequests = newArrayList(new QuestionRequest(questionId, Collections.EMPTY_LIST, "c", false, false));
        QuizReportRequest quizReportRequest = new QuizReportRequest(1L, "someId", "sessionId", courseContentIdentifier,
                moduleContentIdentifier, chapterContentIdentifier, quizContentIdentifier, questionRequests, startTime, endTime);

        QuizReportResponse response = (QuizReportResponse) quizReporter.validateAndProcessQuiz(quizDto, quizReportRequest);

        assertFalse(response.getPassed());
        ArgumentCaptor<BookmarkDto> bookmarkArgumentCaptor = ArgumentCaptor.forClass(BookmarkDto.class);
        verify(bookmarkService).addOrUpdate(bookmarkArgumentCaptor.capture());
        BookmarkDto actualBookmark = bookmarkArgumentCaptor.getValue();
        assertEquals(courseContentIdentifier.getContentId(), actualBookmark.getCourse().getContentId());
        assertEquals(moduleContentIdentifier.getContentId(), actualBookmark.getModule().getContentId());
        assertEquals(chapterContentIdentifier.getContentId(), actualBookmark.getChapter().getContentId());
        assertEquals(quizDto.getContentId(), actualBookmark.getQuiz().getContentId());
        assertNotNull(actualBookmark.getQuiz());
    }

    @Test
    public void shouldResetBookmarkToFirstMessageOfChapterAndReturnResultAsNotPassedWhenUserHasScoredLessThanPassPercentage() {
        List<QuestionRequest> questionRequests = newArrayList(new QuestionRequest(questionId, Collections.EMPTY_LIST, "c", false, false));
        QuizReportRequest quizReportRequest = new QuizReportRequest(1L, "someId", "sessionId", courseContentIdentifier,
                moduleContentIdentifier, new ContentIdentifierDto(newChapterContentId, 1), quizContentIdentifier, questionRequests, startTime, endTime);

        QuizReportResponse response = (QuizReportResponse) quizReporter.validateAndProcessQuiz(quizDto, quizReportRequest);

        assertFalse(response.getPassed());
        ArgumentCaptor<BookmarkDto> bookmarkArgumentCaptor = ArgumentCaptor.forClass(BookmarkDto.class);
        verify(bookmarkService).addOrUpdate(bookmarkArgumentCaptor.capture());
        BookmarkDto actualBookmark = bookmarkArgumentCaptor.getValue();
        assertEquals(courseContentIdentifier.getContentId(), actualBookmark.getCourse().getContentId());
        assertEquals(moduleContentIdentifier.getContentId(), actualBookmark.getModule().getContentId());
        assertEquals(newChapterContentId, actualBookmark.getChapter().getContentId());
        assertEquals(messageContentIdentifier.getContentId(), actualBookmark.getMessage().getContentId());
        assertNotNull(actualBookmark.getMessage());
    }

    @Test
    public void shouldUpdateBookmarkAndReturnResultAsPassedWhenUserHasScoredMoreThanPassPercentage() {
        QuizReportRequest quizReportRequest = new QuizReportRequest(1L, "someId", "sessionId", courseContentIdentifier,
                moduleContentIdentifier, chapterContentIdentifier, quizContentIdentifier, questionRequests, startTime, endTime);

        QuizReportResponse response = (QuizReportResponse) quizReporter.validateAndProcessQuiz(quizDto, quizReportRequest);

        assertTrue(response.getPassed());
        ArgumentCaptor<BookmarkDto> bookmarkArgumentCaptor = ArgumentCaptor.forClass(BookmarkDto.class);
        verify(bookmarkService).addOrUpdate(bookmarkArgumentCaptor.capture());
        BookmarkDto actualBookmark = bookmarkArgumentCaptor.getValue();
        assertEquals(courseContentIdentifier.getContentId(), actualBookmark.getCourse().getContentId());
        assertEquals(moduleContentIdentifier.getContentId(), actualBookmark.getModule().getContentId());
        assertEquals(newChapterContentId, actualBookmark.getChapter().getContentId());
        assertEquals(messageContentIdentifier.getContentId(), actualBookmark.getMessage().getContentId());
        assertNull(actualBookmark.getQuiz());
    }

    @Test
    public void shouldUpdateBookmarkWithNextModuleAndReturnResultAsPassedWhenUserHasScoredMoreThanPassPercentage() {
        QuizReportRequest quizReportRequest = new QuizReportRequest(1L, "someId", "sessionId", courseContentIdentifier,
                moduleContentIdentifier, new ContentIdentifierDto(newChapterContentId, 1), quizContentIdentifier, questionRequests, startTime, endTime);

        QuizReportResponse response = (QuizReportResponse) quizReporter.validateAndProcessQuiz(quizDto, quizReportRequest);

        assertTrue(response.getPassed());
        ArgumentCaptor<BookmarkDto> bookmarkArgumentCaptor = ArgumentCaptor.forClass(BookmarkDto.class);
        verify(bookmarkService).addOrUpdate(bookmarkArgumentCaptor.capture());
        BookmarkDto actualBookmark = bookmarkArgumentCaptor.getValue();
        assertEquals(courseContentIdentifier.getContentId(), actualBookmark.getCourse().getContentId());
        assertEquals(newModuleContentId, actualBookmark.getModule().getContentId());
        assertEquals(newChapterContentId, actualBookmark.getChapter().getContentId());
        assertEquals(messageContentIdentifier.getContentId(), actualBookmark.getMessage().getContentId());
        assertNull(actualBookmark.getQuiz());
    }
}
