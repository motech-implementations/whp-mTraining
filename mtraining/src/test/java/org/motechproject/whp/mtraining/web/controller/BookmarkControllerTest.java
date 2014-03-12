package org.motechproject.whp.mtraining.web.controller;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.service.BookmarkService;
import org.motechproject.whp.mtraining.domain.BookmarkRequestLog;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.repository.BookmarkRequestLogs;
import org.motechproject.whp.mtraining.repository.Courses;
import org.motechproject.whp.mtraining.repository.Providers;
import org.motechproject.whp.mtraining.web.Sessions;
import org.motechproject.whp.mtraining.web.domain.ActivationStatus;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookmarkControllerTest {

    private Providers providers;
    private BookmarkController bookmarkController;
    private Sessions sessions;
    private BookmarkRequestLogs callLogs;

    @Mock
    private BookmarkService bookmarkService;
    @Mock
    private Courses courses;

    @Before
    public void before() {
        providers = mock(Providers.class);
        sessions = mock(Sessions.class);
        callLogs = mock(BookmarkRequestLogs.class);
        bookmarkController = new BookmarkController(providers, sessions, callLogs, bookmarkService, courses);
    }

    @Test
    public void shouldMarkCallerAsUnidentifiedIfCallerIdNotRegistered() {
        long callerId = 76465464L;

        when(providers.getByCallerId(callerId)).thenReturn(null);

        MotechResponse response = bookmarkController.getBookmark(callerId, "uuid", null);
        assertThat(response.getResponseStatusCode(), is(ResponseStatus.UNKNOWN_PROVIDER.getCode()));

        verify(providers).getByCallerId(callerId);
        verify(callLogs).record(any(BookmarkRequestLog.class));
    }

    @Test
    public void shouldMarkCallerAsIdentifiedIfCallerIdRegistered() {
        Long callerId = 76465464L;
        Provider provider = mock(Provider.class);
        Long providerId = 1234L;
        when(provider.getId()).thenReturn(providerId);
        when(providers.getByCallerId(callerId)).thenReturn(provider);
        when(provider.getActivationStatus()).thenReturn(ActivationStatus.ACTIVE_RHP.getStatus());
        ContentIdentifierDto contentId = new ContentIdentifierDto(UUID.randomUUID(), 1);
        BookmarkDto bookmarkDto = new BookmarkDto(callerId.toString(), contentId, contentId, contentId, contentId, DateTime.now());
        when(bookmarkService.getBookmark(provider.getId().toString())).thenReturn(bookmarkDto);

        MotechResponse response = bookmarkController.getBookmark(callerId, "uuid", null);

        assertThat(response.getResponseStatusCode(), is(ResponseStatus.OK.getCode()));
        verify(providers).getByCallerId(callerId);
        verify(callLogs).record(any(BookmarkRequestLog.class));
        verify(bookmarkService).getBookmark(provider.getId().toString());
    }

    @Test
    public void shouldGenerateSessionIdIfNotProvided() {
        long callerId = 76465464L;
        String uniqueId = "uuid";

        when(sessions.create()).thenReturn("7868jhgjg");

        MotechResponse bookmark = bookmarkController.getBookmark(callerId, uniqueId, null);

        assertThat(StringUtils.isBlank(bookmark.getSessionId()), Is.is(false));
        verify(sessions).create();
        verify(callLogs).record(any(BookmarkRequestLog.class));
    }

    @Test
    public void shouldMarkErrorIfCallerIdIsMissing() {
        MotechResponse response = bookmarkController.getBookmark(null, "uni", "ssn001");
        assertThat(response.getResponseStatusCode(), Is.is(ResponseStatus.MISSING_CALLER_ID.getCode()));
    }

    @Test
    public void shouldMarkErrorIfUniqueIdIsMissing() {
        MotechResponse response = bookmarkController.getBookmark(123l, "", "ssn001");
        assertThat(response.getResponseStatusCode(), Is.is(ResponseStatus.MISSING_UNIQUE_ID.getCode()));
    }

    @Test
    public void shouldMarkErrorIfProviderIsNotValid() {
        long callerId = 76465464L;
        Provider provider = new Provider(callerId, null, ActivationStatus.ELIMINATED_RHP);
        when(providers.getByCallerId(callerId)).thenReturn(provider);

        MotechResponse response = bookmarkController.getBookmark(callerId, "uuid", null);

        assertThat(response.getResponseStatusCode(), is(ResponseStatus.NOT_WORKING_PROVIDER.getCode()));
        verify(providers).getByCallerId(callerId);
        verify(callLogs).record(any(BookmarkRequestLog.class));
    }
}