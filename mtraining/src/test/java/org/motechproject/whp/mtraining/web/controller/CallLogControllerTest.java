package org.motechproject.whp.mtraining.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.whp.mtraining.csv.domain.NodeType;
import org.motechproject.whp.mtraining.reports.CallLogReporter;
import org.motechproject.whp.mtraining.web.domain.BasicResponse;
import org.motechproject.whp.mtraining.web.domain.CallLogRequest;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.Node;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

@RunWith(MockitoJUnitRunner.class)
public class CallLogControllerTest {

    @Mock
    private CallLogReporter callLogReporter;

    private CallLogController callLogController;

    @Before
    public void setUp() {
        callLogController = new CallLogController(callLogReporter);
    }

    @Test
    public void shouldReturnWithErrorsIfValidationFails() {
        long callerId = 1234567890L;
        String uniqueId = "uniqueId";
        String sessionId = null;
        Node course = new Node(UUID.randomUUID(), 1, NodeType.COURSE.name(), null, "2014-03-30T17:52:25.976Z", true);
        CallLogRequest callLogRequest = new CallLogRequest(callerId, uniqueId, sessionId, newArrayList(course), null, null);

        ResponseEntity<? extends MotechResponse> response = callLogController.postCallLog(callLogRequest);

        verify(callLogReporter, never()).validateAndAddLog(callLogRequest, new BasicResponse(callerId, uniqueId, sessionId));
        assertFalse(ResponseStatus.OK.getCode().equals(response.getBody().getResponseCode()));
    }

    @Test
    public void shouldReturnSuccessWhenCallLogIsAdded() {
        long callerId = 1234567890L;
        String uniqueId = "uniqueId";
        String sessionId = "sessionId";
        Node course = new Node(UUID.randomUUID(), 1, NodeType.COURSE.name(), null, "2014-03-30T17:52:25.976Z", true);
        CallLogRequest callLogRequest = new CallLogRequest(callerId, uniqueId, sessionId, newArrayList(course), null, null);
        BasicResponse basicResponse = new BasicResponse(callerId, uniqueId, sessionId);
        when(callLogReporter.validateAndAddLog(eq(callLogRequest), any(BasicResponse.class))).thenReturn(new ResponseEntity<>(basicResponse.withResponse(ResponseStatus.OK), OK));

        ResponseEntity<? extends MotechResponse> response = callLogController.postCallLog(callLogRequest);

        assertTrue(ResponseStatus.OK.getCode().equals(response.getBody().getResponseCode()));
    }
}
