package org.motechproject.whp.mtraining.web.controller;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
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
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseProgressDto;
import org.motechproject.mtraining.exception.InvalidBookmarkException;
import org.motechproject.mtraining.service.BookmarkService;
import org.motechproject.mtraining.service.CourseProgressService;
import org.motechproject.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.BookmarkBuilder;
import org.motechproject.whp.mtraining.domain.CoursePublicationAttempt;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.reports.domain.BookmarkRequest;
import org.motechproject.whp.mtraining.repository.AllBookmarkRequests;
import org.motechproject.whp.mtraining.repository.AllCoursePublicationAttempts;
import org.motechproject.whp.mtraining.repository.Providers;
import org.motechproject.whp.mtraining.web.Sessions;
import org.motechproject.whp.mtraining.web.domain.Bookmark;
import org.motechproject.whp.mtraining.web.domain.CourseProgress;
import org.motechproject.whp.mtraining.web.domain.CourseProgressGetRequest;
import org.motechproject.whp.mtraining.web.domain.CourseProgressPostRequest;
import org.motechproject.whp.mtraining.web.domain.CourseProgressResponse;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.ProviderStatus;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_BOOKMARK;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_DATE_TIME;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_CALLER_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_SESSION_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_UNIQUE_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.UNKNOWN_PROVIDER;

@RunWith(MockitoJUnitRunner.class)
public class BookmarkControllerTest {

    private Providers providers;
    private BookmarkController bookmarkController;
    private Sessions sessions;
    private AllBookmarkRequests allBookmarkRequests;

    @Mock
    private BookmarkService bookmarkService;

    @Mock
    private CourseProgressService courseProgressService;

    @Mock
    private AllCoursePublicationAttempts allCoursePublicationAttempts;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final Location DEFAULT_PROVIDER_LOCATION = new Location("block", "district", "state");

    @Before
    public void before() {
        providers = mock(Providers.class);
        sessions = mock(Sessions.class);
        allBookmarkRequests = mock(AllBookmarkRequests.class);
        bookmarkController = new BookmarkController(providers, sessions, allBookmarkRequests, courseProgressService, allCoursePublicationAttempts);
    }

    @Test
    public void shouldMarkCallerAsUnidentifiedIfCallerIdNotRegistered() {
        long callerId = 76465464L;
        when(providers.getByCallerId(callerId)).thenReturn(null);

        MotechResponse response = bookmarkController.getBookmark(new CourseProgressGetRequest(callerId, null, "uuid")).getBody();

        assertThat(response.getResponseCode(), is(UNKNOWN_PROVIDER.getCode()));
        verify(providers).getByCallerId(callerId);
        verify(allBookmarkRequests).add(any(BookmarkRequest.class));
    }

    @Test
    public void shouldMarkCallerAsIdentifiedIfCallerIdRegistered() {
        Long callerId = 76465464L;
        Provider provider = mock(Provider.class);
        String providerRemediId = "remediId";
        when(provider.getRemediId()).thenReturn(providerRemediId);
        when(providers.getByCallerId(callerId)).thenReturn(provider);
        when(provider.getProviderStatus()).thenReturn(ProviderStatus.WORKING_PROVIDER.getStatus());
        ContentIdentifierDto contentId = new ContentIdentifierDto(UUID.randomUUID(), 1);
        BookmarkDto bookmarkDto = new BookmarkDto(callerId.toString(), contentId, contentId, contentId, contentId, contentId, ISODateTimeUtil.nowInTimeZoneUTC());
        CourseProgressDto courseProgressDto = new CourseProgressDto(providerRemediId, null, bookmarkDto, CourseStatus.STARTED);
        when(courseProgressService.getCourseProgressForEnrollee(providerRemediId)).thenReturn(courseProgressDto);

        MotechResponse response = bookmarkController.getBookmark(new CourseProgressGetRequest(callerId, null, "uuid")).getBody();

        assertThat(response.getResponseCode(), is(ResponseStatus.OK.getCode()));
        verify(providers).getByCallerId(callerId);
        verify(allBookmarkRequests).add(any(BookmarkRequest.class));
        verify(courseProgressService).getCourseProgressForEnrollee(provider.getId().toString());
    }

    @Test
    public void shouldGenerateSessionIdIfNotProvided() {
        long callerId = 76465464L;
        String uniqueId = "uuid";
        when(sessions.create()).thenReturn("7868jhgjg");

        MotechResponse bookmark = bookmarkController.getBookmark(new CourseProgressGetRequest(callerId, null, uniqueId)).getBody();

        assertThat(StringUtils.isBlank(bookmark.getSessionId()), Is.is(false));
        verify(sessions).create();
        verify(allBookmarkRequests).add(any(BookmarkRequest.class));
    }

    @Test
    public void shouldMarkErrorIfCallerIdIsMissing() {
        MotechResponse response = bookmarkController.getBookmark(new CourseProgressGetRequest(null, "ssn001", "uni")).getBody();
        assertThat(response.getResponseCode(), Is.is(MISSING_CALLER_ID.getCode()));
    }

    @Test
    public void shouldMarkErrorIfUniqueIdIsMissing() {
        MotechResponse response = bookmarkController.getBookmark(new CourseProgressGetRequest(123l, "ssn001", "")).getBody();
        assertThat(response.getResponseCode(), Is.is(MISSING_UNIQUE_ID.getCode()));
    }

    @Test
    public void shouldMarkErrorIfProviderIsNotValid() {
        long callerId = 76465464L;
        Provider provider = new Provider("remediId", callerId, ProviderStatus.NOT_WORKING_PROVIDER, DEFAULT_PROVIDER_LOCATION);
        when(providers.getByCallerId(callerId)).thenReturn(provider);

        MotechResponse response = bookmarkController.getBookmark(new CourseProgressGetRequest(callerId, null, "uuid")).getBody();

        assertThat(response.getResponseCode(), is(ResponseStatus.NOT_WORKING_PROVIDER.getCode()));
        verify(providers).getByCallerId(callerId);
        verify(allBookmarkRequests).add(any(BookmarkRequest.class));
    }


    @Test
    public void shouldReturnBookmarkRetrievedFromBookmarkService() {
        Long callerId = 87676598l;
        String uniqueId = "unk001";
        String sessionId = "session001";
        String remediId = "remediId";
        ContentIdentifierDto course = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto module = new ContentIdentifierDto(UUID.randomUUID(), 2);
        ContentIdentifierDto chapter = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto message = new ContentIdentifierDto(UUID.randomUUID(), 1);
        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();

        BookmarkDto bookmarkDto = new BookmarkDto(remediId, course, module, chapter, message, null, now);
        CourseProgressDto courseProgressDto = new CourseProgressDto(remediId, null, bookmarkDto, CourseStatus.STARTED);
        Provider provider = new Provider(remediId, callerId, ProviderStatus.WORKING_PROVIDER, DEFAULT_PROVIDER_LOCATION);
        when(providers.getByCallerId(callerId)).thenReturn(provider);
        CoursePublicationAttempt lastCourseSuccessfulAttempt = new CoursePublicationAttempt(UUID.randomUUID(), 1, true);
        when(allCoursePublicationAttempts.getLastSuccessfulCoursePublicationAttempt()).thenReturn(lastCourseSuccessfulAttempt);

        when(courseProgressService.getCourseProgressForEnrollee(remediId)).thenReturn(courseProgressDto);

        ResponseEntity<? extends MotechResponse> response = bookmarkController.getBookmark(new CourseProgressGetRequest(callerId, sessionId, uniqueId));

        assertThat(response.getStatusCode(), Is.is(HttpStatus.OK));

        CourseProgressResponse courseProgressResponse = (CourseProgressResponse) response.getBody();

        assertThat(courseProgressResponse.getCallerId(), Is.is(callerId));
        assertThat(courseProgressResponse.getUniqueId(), Is.is(uniqueId));
        assertThat(courseProgressResponse.getSessionId(), Is.is(sessionId));
        Bookmark bookmark = courseProgressResponse.getCourseProgress().getBookmark();


        assertThat(courseProgressResponse.getCourseProgress().getCourseStatus(), Is.is(CourseStatus.STARTED.value()));
        assertThat(bookmark.getCourseIdentifierDto(), Is.is(course));
        assertThat(bookmark.getModuleIdentifierDto(), Is.is(module));
        assertThat(bookmark.getChapterIdentifierDto(), Is.is(chapter));
        assertThat(bookmark.getMessageIdentifierDto(), Is.is(message));
        assertThat(bookmark.getQuizIdentifierDto(), IsNull.nullValue());
        assertThat(bookmark.getDateModified(), Is.is(now.toString()));
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
        ContentIdentifierDto quizIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);

        Bookmark bookmark = new Bookmark(courseIdentifier, moduleIdentifier, chapterIdentifier, messageIdentifier, quizIdentifier, DateTime.now().toString());
        CourseProgress courseProgress = new CourseProgress(DateTime.now().toString(), bookmark, 2, "COMPLETED");
        CourseProgressPostRequest courseProgressPostRequest = new CourseProgressPostRequest(callerId, uniqueId, sessionId, courseProgress);


        Provider provider = new Provider("remediId", callerId, ProviderStatus.WORKING_PROVIDER, DEFAULT_PROVIDER_LOCATION);
        when(providers.getByCallerId(callerId)).thenReturn(provider);

        bookmarkController.postBookmark(courseProgressPostRequest);

        ArgumentCaptor<CourseProgressDto> courseProgressDtoArgumentCaptor = ArgumentCaptor.forClass(CourseProgressDto.class);
        verify(courseProgressService).addOrUpdateCourseProgress(courseProgressDtoArgumentCaptor.capture());

        CourseProgressDto courseProgressDto = courseProgressDtoArgumentCaptor.getValue();
        assertThat(courseProgressDto.getExternalId(), Is.is(provider.getRemediId()));
        assertThat(courseProgressDto.getBookmarkDto().getCourse(), Is.is(courseIdentifier));
        assertThat(courseProgressDto.getBookmarkDto().getModule(), Is.is(moduleIdentifier));
        assertThat(courseProgressDto.getBookmarkDto().getChapter(), Is.is(chapterIdentifier));
        assertThat(courseProgressDto.getBookmarkDto().getMessage(), Is.is(messageIdentifier));
        assertThat(courseProgressDto.getCourseStatus(), Is.is(CourseStatus.COMPLETED));

        ArgumentCaptor<BookmarkRequest> bookmarkRequestArgumentCaptor = ArgumentCaptor.forClass(BookmarkRequest.class);
        verify(allBookmarkRequests, times(1)).add(bookmarkRequestArgumentCaptor.capture());
        BookmarkRequest bookmarkRequest = bookmarkRequestArgumentCaptor.getValue();

        assertThat(bookmarkRequest.getCallerId(), Is.is(callerId));
        assertThat(bookmarkRequest.hasBookmarkFor(courseIdentifier), Is.is(true));
    }

    @Test
    public void shouldThrowErrorOnPostBookmarkForInvalidBookmark() {
        Long callerId = 87676598l;
        String uniqueId = "unk001";
        String sessionId = "session001";
        ContentIdentifierDto courseIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto moduleIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 2);
        ContentIdentifierDto messageIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 2);
        ContentIdentifierDto chapterIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        Bookmark bookmark = new Bookmark(courseIdentifier, moduleIdentifier, chapterIdentifier, messageIdentifier, null, DateTime.now().toString());
        CourseProgress courseProgress = new CourseProgress(DateTime.now().toString(), bookmark, 2, "ONGOING");
        CourseProgressPostRequest courseProgressPostRequest = new CourseProgressPostRequest(callerId, uniqueId, sessionId, courseProgress);
        Provider provider = new Provider("remediId", callerId, ProviderStatus.WORKING_PROVIDER, DEFAULT_PROVIDER_LOCATION);
        when(providers.getByCallerId(callerId)).thenReturn(provider);
        ArgumentCaptor<CourseProgressDto> courseProgressDtoArgumentCaptor = ArgumentCaptor.forClass(CourseProgressDto.class);
        doThrow(new InvalidBookmarkException("")).when(courseProgressService).addOrUpdateCourseProgress(courseProgressDtoArgumentCaptor.capture());
        ResponseEntity<MotechResponse> response = bookmarkController.postBookmark(courseProgressPostRequest);

        assertEquals(INVALID_BOOKMARK.getCode(), response.getBody().getResponseCode());
    }

    @Test
    public void shouldSendErrorResponseWhenCallerIdIsMissing() {
        String uniqueId = "unk001";
        String sessionId = "session001";
        CourseProgress courseProgress = new CourseProgress(DateTime.now().toString(), new Bookmark(), 2, "ONGOING");
        CourseProgressPostRequest courseProgressPostRequest = new CourseProgressPostRequest(null, uniqueId, sessionId, courseProgress);

        ResponseEntity<MotechResponse> response = bookmarkController.postBookmark(courseProgressPostRequest);

        assertEquals(MISSING_CALLER_ID.getCode(), response.getBody().getResponseCode());
        verify(bookmarkService, never()).addOrUpdate(any(BookmarkDto.class));

    }

    @Test
    public void shouldSendErrorResponseWhenProviderWithGivenCallerIdNotFound() {
        String uniqueId = "unk001";
        String sessionId = "session001";
        Long callerId = 124456l;
        Bookmark bookmark = new BookmarkBuilder().build();
        CourseProgress courseProgress = new CourseProgress(DateTime.now().toString(), bookmark, 2, "ONGOING");
        CourseProgressPostRequest courseProgressPostRequest = new CourseProgressPostRequest(callerId, uniqueId, sessionId, courseProgress);
        when(providers.getByCallerId(callerId)).thenReturn(null);

        ResponseEntity<MotechResponse> response = bookmarkController.postBookmark(courseProgressPostRequest);

        assertEquals(UNKNOWN_PROVIDER.getCode(), response.getBody().getResponseCode());
        verify(bookmarkService, never()).addOrUpdate(any(BookmarkDto.class));
    }

    @Test
    public void shouldSendErrorResponseWhenUniqueIdIsMissing() {
        CourseProgress courseProgress = new CourseProgress(DateTime.now().toString(), new Bookmark(), 2, "ONGOING");
        CourseProgressPostRequest courseProgressPostRequest = new CourseProgressPostRequest(3232938l, null, "session01", courseProgress);

        ResponseEntity<MotechResponse> response = bookmarkController.postBookmark(courseProgressPostRequest);

        assertEquals(MISSING_UNIQUE_ID.getCode(), response.getBody().getResponseCode());
        verify(bookmarkService, never()).addOrUpdate(any(BookmarkDto.class));
    }

    @Test
    public void shouldSendErrorResponseWhenSessionIdIsMissing() {
        CourseProgress courseProgress = new CourseProgress(DateTime.now().toString(), new Bookmark(), 2, "ONGOING");
        CourseProgressPostRequest courseProgressPostRequest = new CourseProgressPostRequest(3232938l, "unq11", null, courseProgress);

        ResponseEntity<MotechResponse> response = bookmarkController.postBookmark(courseProgressPostRequest);

        assertEquals(MISSING_SESSION_ID.getCode(), response.getBody().getResponseCode());
        verify(bookmarkService, never()).addOrUpdate(any(BookmarkDto.class));
    }

    @Test
    public void shouldSendErrorResponseWhenBookmarkDateModifiedAbsent() {
        long callerId = 3232938l;
        Provider provider = new Provider(null, callerId, ProviderStatus.WORKING_PROVIDER, DEFAULT_PROVIDER_LOCATION);
        when(providers.getByCallerId(callerId)).thenReturn(provider);
        Bookmark bookmark = new BookmarkBuilder().withDateModified(null).build();
        CourseProgress courseProgress = new CourseProgress(DateTime.now().toString(), bookmark, 2, "ONGOING");
        CourseProgressPostRequest courseProgressPostRequest = new CourseProgressPostRequest(callerId, "unq11", "s001", courseProgress);

        ResponseEntity<MotechResponse> responseEntity = bookmarkController.postBookmark(courseProgressPostRequest);

        assertEquals(INVALID_DATE_TIME.getCode(), responseEntity.getBody().getResponseCode());
    }

    @Test
    public void shouldGetOnlyTheLastSuccessfullyPublishedCourseForGeneratingBookmark() {
        CoursePublicationAttempt lastCourseSuccessfulAttempt = new CoursePublicationAttempt(UUID.randomUUID(), 1, true);
        when(allCoursePublicationAttempts.getLastSuccessfulCoursePublicationAttempt()).thenReturn(lastCourseSuccessfulAttempt);

        when(providers.getByCallerId(1l)).thenReturn(new Provider("r001", 1l, ProviderStatus.WORKING_PROVIDER, DEFAULT_PROVIDER_LOCATION));

        BookmarkDto bookmarkDTO = new BookmarkBuilder().withExternalId("r001").buildDTO();
        CourseProgressDto courseProgressDto = new CourseProgressDto("r001", null, bookmarkDTO, CourseStatus.STARTED);
        when(courseProgressService.getInitialCourseProgressForEnrollee("r001", new ContentIdentifierDto(lastCourseSuccessfulAttempt.getCourseId(), lastCourseSuccessfulAttempt.getVersion()))).thenReturn(courseProgressDto);

        bookmarkController.getBookmark(new CourseProgressGetRequest(1l, "uk", "s001"));

        verify(allCoursePublicationAttempts).getLastSuccessfulCoursePublicationAttempt();
    }
}
