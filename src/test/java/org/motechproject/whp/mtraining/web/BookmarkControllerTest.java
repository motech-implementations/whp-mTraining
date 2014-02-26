package org.motechproject.whp.mtraining.web;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.whp.mtraining.domain.CallLog;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.repository.CallLogs;
import org.motechproject.whp.mtraining.repository.Providers;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookmarkControllerTest {


    private Providers providers;
    private BookmarkController bookmarkController;
    private Sessions sessions;
    private CallLogs callLogs;

    @Before
    public void before() {
        providers = mock(Providers.class);
        sessions = mock(Sessions.class);
        callLogs = mock(CallLogs.class);
        bookmarkController = new BookmarkController(providers, sessions, callLogs);
    }

    @Test
    public void shouldMarkCallerAsUnidentifiedIfCallerIdNotRegistered() {
        long callerId = 76465464L;

        when(providers.getByCallerId(callerId)).thenReturn(null);

        MotechResponse response = bookmarkController.getBookmark(callerId, "uuid", null);
        assertThat(response.getResponseStatusCode(), is(ResponseStatus.UNKNOWN_PROVIDER.getCode()));

        verify(providers).getByCallerId(callerId);
        verify(callLogs).record(any(CallLog.class));
    }


    @Test
    public void shouldMarkCallerAsIdentifiedIfCallerIdRegistered() {
        long callerId = 76465464L;

        Provider provider = new Provider(callerId);
        when(providers.getByCallerId(callerId)).thenReturn(provider);

        MotechResponse response = bookmarkController.getBookmark(callerId, "uuid", null);
        assertThat(response.getResponseStatusCode(), is(ResponseStatus.OK.getCode()));

        verify(providers).getByCallerId(callerId);
        verify(callLogs).record(any(CallLog.class));
    }

    @Test
    public void shouldGenerateSessionIdIfNotProvided() {
        long callerId = 76465464L;
        String uniqueId = "uuid";

        when(sessions.create()).thenReturn("7868jhgjg");

        MotechResponse bookmark = bookmarkController.getBookmark(callerId, uniqueId, null);

        assertThat(StringUtils.isBlank(bookmark.getSessionId()), Is.is(false));
        verify(sessions).create();
        verify(callLogs).record(any(CallLog.class));
    }

}
