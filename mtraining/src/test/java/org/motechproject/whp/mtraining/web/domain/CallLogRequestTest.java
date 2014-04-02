package org.motechproject.whp.mtraining.web.domain;

import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.mtraining.util.ISODateTimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CallLogRequestTest {

    @Test
    public void shouldValidateCallLog() {
        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        DateTime tenMinutesAfterNow = now.plusMinutes(10);
        CallLogRequest validCallLogRequest = new CallLogRequest(746l, "unk001", "ssn001", Collections.<CallLogRecord>emptyList(), now.toString(), tenMinutesAfterNow.toString());
        List<ValidationError> expectedEmptyErrors = validCallLogRequest.validate();
        assertThat(expectedEmptyErrors.size(), Is.is(0));

        CallLogRequest invalidCallLogRequest = new CallLogRequest(null, null, "", Collections.<CallLogRecord>emptyList(), now.toString(), tenMinutesAfterNow.toString());
        assertThat(invalidCallLogRequest.validate().size(), Is.is(3));
    }

    @Test
    public void shouldReturnOnlyCallLogRequestValidationFailuresWhenCallLogRequestIsInvalid() {
        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        DateTime tenMinutesAfterNow = now.plusMinutes(10);
        CallLogRecord invalidCallLogRecord = new CallLogRecord();
        List<CallLogRecord> callLogRecords = Arrays.asList(invalidCallLogRecord);
        CallLogRequest invalidCallLogRequestWithCallRecords = new CallLogRequest(null, null, "", callLogRecords, now.toString(), tenMinutesAfterNow.toString());
        List<ValidationError> validationErrors = invalidCallLogRequestWithCallRecords.validate();
        assertThat(validationErrors.size(), Is.is(3));
    }

    @Test
    public void shouldValidateCallLogRecords() {
        List<CallLogRecord> callLogRecords = new ArrayList<>();
        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        DateTime tenMinutesAddedToNow = now.plusMinutes(10);
        callLogRecords.add(new CallLogRecord(null, 1, "FOO", now.toString(), tenMinutesAddedToNow.toString()));

        CallLogRequest callLogRequest = new CallLogRequest(777897l, "unk001", "ssn001", callLogRecords, now.toString(), tenMinutesAddedToNow.toString());

        List<ValidationError> validationErrors = callLogRequest.validate();
        assertThat(validationErrors.size(), Is.is(2));
        assertTrue(validationErrors.contains(new ValidationError(ResponseStatus.MISSING_CONTENT_ID.getCode())));
        assertTrue(validationErrors.contains(new ValidationError(ResponseStatus.INVALID_CALL_LOG_TYPE.getCode())));
    }

}
