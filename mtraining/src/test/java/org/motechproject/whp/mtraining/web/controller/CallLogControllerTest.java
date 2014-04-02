package org.motechproject.whp.mtraining.web.controller;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.whp.mtraining.csv.domain.CallLogRecordType;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.reports.CallLogReporter;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.web.domain.CallLogRecord;
import org.motechproject.whp.mtraining.web.domain.CallLogRequest;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CallLogControllerTest {

    @Mock
    private CallLogReporter callLogReporter;

    @Mock
    private ProviderService providerService;

    private CallLogController callLogController;

    @Before
    public void setUp() {
        callLogController = new CallLogController(callLogReporter, providerService);
    }

    @Test
    public void shouldReturnWithErrorsIfCallLogRequestFailsValidation() {
        long callerId = 1234567890L;
        String uniqueId = "uniqueId";
        String nullSessionId = null;

        CallLogRecord course = new CallLogRecord(UUID.randomUUID(), 1, CallLogRecordType.COURSE.name(), "2014-03-30T17:42:25.976Z", "2014-03-30T17:52:25.976Z");

        CallLogRequest callLogRequest = new CallLogRequest(callerId, uniqueId, nullSessionId, newArrayList(course), "2014-03-30T17:52:25.976Z", "2014-03-30T17:55:25.976Z");

        ResponseEntity<? extends MotechResponse> response = callLogController.postCallLog(callLogRequest);

        verify(callLogReporter, never()).report(any(CallLogRequest.class), any(Provider.class));
        assertFalse(ResponseStatus.OK.getCode().equals(response.getBody().getResponseCode()));
    }

    @Test
    public void shouldReturnWithErrorsIfCallLogRecordFailsValidation() {
        long callerId = 1234567890L;
        String uniqueId = "uniqueId";
        String sessionId = "ssn001";
        String invalidStartTime = "2014-33-30T17:52:25.976Z";

        CallLogRecord course = new CallLogRecord(UUID.randomUUID(), 1, CallLogRecordType.MESSAGE.name(), invalidStartTime, "2014-03-30T17:52:25.976Z");
        CallLogRequest callLogRequest = new CallLogRequest(callerId, uniqueId, sessionId, newArrayList(course), "2014-03-30T17:52:25.976Z", "2014-03-30T17:55:25.976Z");

        when(providerService.byCallerId(callerId)).thenReturn(new Provider());

        ResponseEntity<? extends MotechResponse> response = callLogController.postCallLog(callLogRequest);

        MotechResponse motechResponse = response.getBody();
        assertThat(motechResponse.getResponseCode(), Is.is(ResponseStatus.INVALID_DATE_TIME.getCode()));
        assertThat(motechResponse.getResponseMessage(), Is.is("Invalid MESSAGE Call Log"));

        verify(callLogReporter, never()).report(any(CallLogRequest.class), any(Provider.class));
    }

    @Test
    public void shouldReturnSuccessWhenCallLogIsAdded() {
        long callerId = 1234567890L;
        String uniqueId = "uniqueId";
        String sessionId = "sessionId";
        CallLogRecord course = new CallLogRecord(UUID.randomUUID(), 1, CallLogRecordType.COURSE.name(), null, "2014-03-30T17:52:25.976Z");
        CallLogRequest callLogRequest = new CallLogRequest(callerId, uniqueId, sessionId, newArrayList(course), "2014-03-30T17:52:25.976Z", "2014-03-30T17:55:25.976Z");

        when(providerService.byCallerId(callerId)).thenReturn(new Provider());

        ResponseEntity<? extends MotechResponse> response = callLogController.postCallLog(callLogRequest);

        assertTrue(ResponseStatus.OK.getCode().equals(response.getBody().getResponseCode()));
    }

}
