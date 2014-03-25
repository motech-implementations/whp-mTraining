package org.motechproject.whp.mtraining.web.controller;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.service.BookmarkService;
import org.motechproject.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.BookmarkBuilder;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.reports.domain.BookmarkRequest;
import org.motechproject.whp.mtraining.repository.AllBookmarkRequests;
import org.motechproject.whp.mtraining.repository.Courses;
import org.motechproject.whp.mtraining.repository.Providers;
import org.motechproject.whp.mtraining.web.Sessions;
import org.motechproject.whp.mtraining.web.domain.ActivationStatus;
import org.motechproject.whp.mtraining.web.domain.Bookmark;
import org.motechproject.whp.mtraining.web.domain.BookmarkPostRequest;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_BOOKMARK_MODIFIED_DATE;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_CALLER_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_SESSION_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_UNIQUE_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.UNKNOWN_PROVIDER;

@RunWith(MockitoJUnitRunner.class)
public class BookmarkControllerTest {

    private Providers providers;
    private BookmarkController bookmarkController;
    private Sessions sessions;
    private AllBookmarkRequests callLogs;

    @Mock
    private BookmarkService bookmarkService;
    @Mock
    private Courses courses;

    @Before
    public void before() {
        providers = mock(Providers.class);
        sessions = mock(Sessions.class);
        callLogs = mock(AllBookmarkRequests.class);
        bookmarkController = new BookmarkController(providers, sessions, callLogs, bookmarkService, courses);
    }

    @Test
    public void shouldMarkCallerAsUnidentifiedIfCallerIdNotRegistered() {
        long callerId = 76465464L;

        when(providers.getByCallerId(callerId)).thenReturn(null);

        MotechResponse response = bookmarkController.getBookmark(callerId, "uuid", null).getBody();
        assertThat(response.getResponseCode(), is(UNKNOWN_PROVIDER.getCode()));

        verify(providers).getByCallerId(callerId);
        verify(callLogs).add(any(BookmarkRequest.class));
    }

    @Test
    public void shouldMarkCallerAsIdentifiedIfCallerIdRegistered() {
        Long callerId = 76465464L;
        Provider provider = mock(Provider.class);
        String providerRemedyId = "remedyId";
        when(provider.getRemedyId()).thenReturn(providerRemedyId);
        when(providers.getByCallerId(callerId)).thenReturn(provider);
        when(provider.getActivationStatus()).thenReturn(ActivationStatus.ACTIVE_RHP.getStatus());
        ContentIdentifierDto contentId = new ContentIdentifierDto(UUID.randomUUID(), 1);
        BookmarkDto bookmarkDto = new BookmarkDto(callerId.toString(), contentId, contentId, contentId, contentId, ISODateTimeUtil.nowInTimeZoneUTC());
        when(bookmarkService.getBookmark(providerRemedyId)).thenReturn(bookmarkDto);

        MotechResponse response = bookmarkController.getBookmark(callerId, "uuid", null).getBody();

        assertThat(response.getResponseCode(), is(ResponseStatus.OK.getCode()));
        verify(providers).getByCallerId(callerId);
        verify(callLogs).add(any(BookmarkRequest.class));
        verify(bookmarkService).getBookmark(provider.getId().toString());
    }

    @Test
    public void shouldGenerateSessionIdIfNotProvided() {
        long callerId = 76465464L;
        String uniqueId = "uuid";

        when(sessions.create()).thenReturn("7868jhgjg");

        MotechResponse bookmark = bookmarkController.getBookmark(callerId, uniqueId, null).getBody();

        assertThat(StringUtils.isBlank(bookmark.getSessionId()), Is.is(false));
        verify(sessions).create();
        verify(callLogs).add(any(BookmarkRequest.class));
    }

    @Test
    public void shouldMarkErrorIfCallerIdIsMissing() {
        MotechResponse response = bookmarkController.getBookmark(null, "uni", "ssn001").getBody();
        assertThat(response.getResponseCode(), Is.is(MISSING_CALLER_ID.getCode()));
    }

    @Test
    public void shouldMarkErrorIfUniqueIdIsMissing() {
        MotechResponse response = bookmarkController.getBookmark(123l, "", "ssn001").getBody();
        assertThat(response.getResponseCode(), Is.is(MISSING_UNIQUE_ID.getCode()));
    }

    @Test
    public void shouldMarkErrorIfProviderIsNotValid() {
        long callerId = 76465464L;
        Provider provider = new Provider(callerId, null, ActivationStatus.ELIMINATED_RHP);
        when(providers.getByCallerId(callerId)).thenReturn(provider);

        MotechResponse response = bookmarkController.getBookmark(callerId, "uuid", null).getBody();

        assertThat(response.getResponseCode(), is(ResponseStatus.NOT_WORKING_PROVIDER.getCode()));
        verify(providers).getByCallerId(callerId);
        verify(callLogs).add(any(BookmarkRequest.class));
    }

    @Test
    public void shouldPostBookmarkToBookmarkService() {
        Long callerId = 87676598l;
        String uniqueId = "unk001";
        String sessionId = "session001";
        ContentIdentifierDto courseIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto moduleIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 2);
        ContentIdentifierDto chapterIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto messageIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);

        BookmarkPostRequest bookmarkPostRequest = new BookmarkPostRequest(callerId, uniqueId, sessionId, new Bookmark(courseIdentifier, moduleIdentifier, chapterIdentifier, messageIdentifier));

        Provider provider = new Provider(callerId, null, ActivationStatus.ACTIVE_TPC);
        when(providers.getByCallerId(callerId)).thenReturn(provider);

        bookmarkController.postBookmark(bookmarkPostRequest);

        ArgumentCaptor<BookmarkDto> bookmarkDtoArgumentCaptor = ArgumentCaptor.forClass(BookmarkDto.class);
        verify(bookmarkService).update(bookmarkDtoArgumentCaptor.capture());


        BookmarkDto postedBookmark = bookmarkDtoArgumentCaptor.getValue();
        assertThat(postedBookmark.getExternalId(), Is.is(provider.getRemedyId()));
        assertThat(postedBookmark.getCourse(), Is.is(courseIdentifier));
        assertThat(postedBookmark.getModule(), Is.is(moduleIdentifier));
        assertThat(postedBookmark.getChapter(), Is.is(chapterIdentifier));
        assertThat(postedBookmark.getMessage(), Is.is(messageIdentifier));
    }

    @Test
    public void shouldSendErrorResponseWhenCallerIdIsMissing() {
        String uniqueId = "unk001";
        String sessionId = "session001";
        BookmarkPostRequest bookmarkPostRequest = new BookmarkPostRequest(null, uniqueId, sessionId, new Bookmark());

        ResponseEntity<MotechResponse> response = bookmarkController.postBookmark(bookmarkPostRequest);
        assertEquals(MISSING_CALLER_ID.getCode(), response.getBody().getResponseCode());

        verify(bookmarkService, never()).update(any(BookmarkDto.class));

    }

    @Test
    public void shouldSendErrorResponseWhenProviderWithGivenCallerIdNotFound() {
        String uniqueId = "unk001";
        String sessionId = "session001";
        Long callerId = 124456l;
        Bookmark bookmark = new BookmarkBuilder().build();
        BookmarkPostRequest bookmarkPostRequest = new BookmarkPostRequest(callerId, uniqueId, sessionId, bookmark);

        when(providers.getByCallerId(callerId)).thenReturn(null);

        ResponseEntity<MotechResponse> response = bookmarkController.postBookmark(bookmarkPostRequest);
        assertEquals(UNKNOWN_PROVIDER.getCode(), response.getBody().getResponseCode());

        verify(bookmarkService, never()).update(any(BookmarkDto.class));
    }


    @Test
    public void shouldSendErrorResponseWhenUniqueIdIsMissing() {
        BookmarkPostRequest bookmarkPostRequest = new BookmarkPostRequest(3232938l, null, "session01", new Bookmark());

        ResponseEntity<MotechResponse> response = bookmarkController.postBookmark(bookmarkPostRequest);
        assertEquals(MISSING_UNIQUE_ID.getCode(), response.getBody().getResponseCode());

        verify(bookmarkService, never()).update(any(BookmarkDto.class));
    }

    @Test
    public void shouldSendErrorResponseWhenSessionIdIsMissing() {
        BookmarkPostRequest bookmarkPostRequest = new BookmarkPostRequest(3232938l, "unq11", null, new Bookmark());

        ResponseEntity<MotechResponse> response = bookmarkController.postBookmark(bookmarkPostRequest);
        assertEquals(MISSING_SESSION_ID.getCode(), response.getBody().getResponseCode());

        verify(bookmarkService, never()).update(any(BookmarkDto.class));
    }

    @Test
    public void shouldSendErrorResponseWhenBookmarkDateModifiedAbsent() {
        long callerId = 3232938l;
        Provider provider = new Provider(callerId, null, ActivationStatus.ACTIVE_TPC);
        when(providers.getByCallerId(callerId)).thenReturn(provider);


        Bookmark bookmark = new BookmarkBuilder().withDateModified(null).build();
        BookmarkPostRequest bookmarkPostRequest = new BookmarkPostRequest(callerId, "unq11", "s001", bookmark);
        ResponseEntity<MotechResponse> responseEntity = bookmarkController.postBookmark(bookmarkPostRequest);

        assertEquals(INVALID_BOOKMARK_MODIFIED_DATE.getCode(), responseEntity.getBody().getResponseCode());
    }


}
