package org.motechproject.whp.mtraining.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.QuizDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.mtraining.service.QuizService;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.reports.QuizReporter;
import org.motechproject.whp.mtraining.service.impl.ProviderServiceImpl;
import org.motechproject.whp.mtraining.web.Sessions;
import org.motechproject.whp.mtraining.web.domain.BasicResponse;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.QuestionRequest;
import org.motechproject.whp.mtraining.web.domain.QuizReportRequest;
import org.motechproject.whp.mtraining.web.domain.QuizReportResponse;
import org.motechproject.whp.mtraining.web.domain.QuizResponse;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.mtraining.util.ISODateTimeUtil.nowAsStringInTimeZoneUTC;
import static org.motechproject.whp.mtraining.web.domain.ProviderStatus.WORKING_PROVIDER;

@RunWith(MockitoJUnitRunner.class)
public class QuizControllerTest {

    @Mock
    private ProviderServiceImpl providerService;
    @Mock
    private Sessions sessions;
    @Mock
    private QuizService quizService;
    @Mock
    private CourseService courseService;
    @Mock
    private QuizReporter quizReporter;
    private QuizController quizController;

    @Before
    public void before() {
        quizController = new QuizController(quizService, providerService, quizReporter);
    }

    @Test
    public void shouldValidateRequest() {
        long callerId = 76465464L;
        UUID quizId = UUID.randomUUID();
        int quizVersion = 1;
        new ContentIdentifierDto(UUID.randomUUID(), 1);

        BasicResponse response = (BasicResponse) quizController.getQuestionsForQuiz(callerId, "uuid", null, quizId, quizVersion).getBody();

        verify(providerService, never()).validateProvider(callerId);
        verify(quizService, never()).getQuestionsForQuiz(any(ContentIdentifierDto.class));
        assertNotSame(ResponseStatus.OK.getCode(), response.getResponseCode());
    }

    @Test
    public void shouldGetQuestionsForTheGivenQuizDetails() {
        long callerId = 76465464L;
        UUID quizId = UUID.randomUUID();
        int quizVersion = 1;
        when(providerService.validateProvider(callerId)).thenReturn(ResponseStatus.OK);
        ContentIdentifierDto question = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto quizIdentifier = new ContentIdentifierDto(quizId, quizVersion);
        when(quizService.getQuestionsForQuiz(quizIdentifier)).thenReturn(newArrayList(question));

        QuizResponse response = (QuizResponse) quizController.getQuestionsForQuiz(callerId, "uuid", "sessionId", quizId, quizVersion).getBody();

        verify(providerService).validateProvider(callerId);
        verify(quizService).getQuestionsForQuiz(quizIdentifier);
        assertEquals(ResponseStatus.OK.getCode(), response.getResponseCode());
        assertEquals(1, response.getQuestions().size());
    }

    @Test
    public void shouldMarkErrorWhenTheQuizIdIsInvalid() {
        long callerId = 76465464L;
        UUID quizId = UUID.randomUUID();
        int quizVersion = 1;
        when(providerService.validateProvider(callerId)).thenReturn(ResponseStatus.OK);
        ContentIdentifierDto quizIdentifier = new ContentIdentifierDto(quizId, quizVersion);

        when(quizService.getQuestionsForQuiz(quizIdentifier)).thenThrow(new IllegalStateException());

        BasicResponse response = (BasicResponse) quizController.getQuestionsForQuiz(callerId, "uuid", "sessionId", quizId, quizVersion).getBody();

        verify(quizService).getQuestionsForQuiz(quizIdentifier);
        assertEquals(ResponseStatus.INVALID_QUIZ.getCode(), response.getResponseCode());
    }

    @Test
    public void shouldReturnMissingQuizErrorIfQuestionsNotFound() {
        long callerId = 76465464L;
        UUID quizId = UUID.randomUUID();
        int quizVersion = 1;
        when(providerService.validateProvider(callerId)).thenReturn(ResponseStatus.OK);
        ContentIdentifierDto quizIdentifier = new ContentIdentifierDto(quizId, quizVersion);
        when(quizService.getQuestionsForQuiz(quizIdentifier)).thenReturn(null);

        BasicResponse response = (BasicResponse) quizController.getQuestionsForQuiz(callerId, "uuid", "sessionId", quizId, quizVersion).getBody();

        verify(quizService).getQuestionsForQuiz(quizIdentifier);
        assertEquals(ResponseStatus.MISSING_QUIZ.getCode(), response.getResponseCode());
    }

    @Test
    public void shouldSubmitQuizResults() {
        long callerId = 76465464L;
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);
        QuizReportRequest quizReportRequest = new QuizReportRequest(callerId, "someId", "sessionId", contentIdentifierDto, contentIdentifierDto, contentIdentifierDto, contentIdentifierDto,
                newArrayList(new QuestionRequest(UUID.randomUUID(), Collections.<String>emptyList(), "a", false, false)), nowAsStringInTimeZoneUTC(), nowAsStringInTimeZoneUTC());
        QuizDto quizDto = new QuizDto();
        QuizReportResponse quizReportResponse = new QuizReportResponse(callerId, "someId", "sessionId", null, null, ResponseStatus.OK);
        when(quizService.getQuiz(contentIdentifierDto)).thenReturn(quizDto);
        when(quizReporter.validateAndProcessQuiz(quizDto, quizReportRequest)).thenReturn(quizReportResponse);
        when(providerService.byCallerId(callerId)).thenReturn(new Provider("remediId", callerId, WORKING_PROVIDER, null));

        ResponseEntity<? extends MotechResponse> response = quizController.submitQuizResults(quizReportRequest);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(ResponseStatus.OK.getCode(), response.getBody().getResponseCode());
    }
}
