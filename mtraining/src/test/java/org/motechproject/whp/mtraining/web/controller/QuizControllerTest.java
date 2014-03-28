package org.motechproject.whp.mtraining.web.controller;


import org.apache.commons.lang.StringUtils;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.service.QuizService;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.service.impl.ProviderServiceImpl;
import org.motechproject.whp.mtraining.web.Sessions;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.ProviderStatus;
import org.motechproject.whp.mtraining.web.domain.QuizResponse;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;

import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_CALLER_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_UNIQUE_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.UNKNOWN_PROVIDER;

@RunWith(MockitoJUnitRunner.class)
public class QuizControllerTest {

    @Mock
    private ProviderServiceImpl providerService;
    @Mock
    private Sessions sessions;
    @Mock
    private QuizService quizService;

    private QuizController quizController;

    @Before
    public void before() {
        quizController = new QuizController(quizService, providerService, sessions);
    }

    @Test
    public void shouldMarkCallerAsUnidentifiedIfCallerIdNotRegistered() {
        long callerId = 76465464L;
        when(providerService.byCallerId(callerId)).thenReturn(null);

        MotechResponse response = quizController.getQuestionsForQuiz(callerId, "uuid", null, UUID.randomUUID(), 1).getBody();

        assertThat(response.getResponseCode(), is(UNKNOWN_PROVIDER.getCode()));
        verify(providerService).byCallerId(callerId);
    }

    @Test
    public void shouldGenerateSessionIdIfNotProvided() {
        long callerId = 76465464L;
        String uniqueId = "uuid";
        when(sessions.create()).thenReturn("7868jhgjg");

        MotechResponse response = quizController.getQuestionsForQuiz(callerId, uniqueId, null, UUID.randomUUID(), 1).getBody();

        assertThat(StringUtils.isBlank(response.getSessionId()), Is.is(false));
        verify(sessions).create();
    }

    @Test
    public void shouldMarkErrorIfCallerIdIsMissing() {
        MotechResponse response = quizController.getQuestionsForQuiz(null, "uni", null, UUID.randomUUID(), 1).getBody();
        assertThat(response.getResponseCode(), Is.is(MISSING_CALLER_ID.getCode()));
    }

    @Test
    public void shouldMarkErrorIfUniqueIdIsMissing() {
        MotechResponse response = quizController.getQuestionsForQuiz(123l, "", "ssn001", UUID.randomUUID(), 1).getBody();
        assertThat(response.getResponseCode(), Is.is(MISSING_UNIQUE_ID.getCode()));
    }

    @Test
    public void shouldMarkErrorIfProviderIsNotValid() {
        long callerId = 76465464L;
        Provider provider = new Provider("remediId", callerId, ProviderStatus.NOT_WORKING_PROVIDER, new Location("block", "district", "state"));
        when(providerService.byCallerId(callerId)).thenReturn(provider);

        MotechResponse response = quizController.getQuestionsForQuiz(callerId, "uuid", null, UUID.randomUUID(), 1).getBody();

        assertThat(response.getResponseCode(), is(ResponseStatus.NOT_WORKING_PROVIDER.getCode()));
        verify(providerService).byCallerId(callerId);
    }

    @Test
    public void shouldGetQuestionsForTheGivenQuizDetails() {
        long callerId = 76465464L;
        Provider provider = new Provider("remediId", callerId, ProviderStatus.WORKING_PROVIDER, new Location("block", "district", "state"));
        UUID quizId = UUID.randomUUID();
        int quizVersion = 1;
        when(providerService.byCallerId(callerId)).thenReturn(provider);
        ContentIdentifierDto question = new ContentIdentifierDto(UUID.randomUUID(), 1);
        when(quizService.getQuestionsForQuiz(new ContentIdentifierDto(quizId, quizVersion))).thenReturn(newArrayList(question));

        QuizResponse response = (QuizResponse) quizController.getQuestionsForQuiz(callerId, "uuid", null, quizId, quizVersion).getBody();

        verify(providerService).byCallerId(callerId);
        assertEquals(ResponseStatus.OK.getCode(), response.getResponseCode());
        assertEquals(1, response.getQuestions().size());
    }
}
