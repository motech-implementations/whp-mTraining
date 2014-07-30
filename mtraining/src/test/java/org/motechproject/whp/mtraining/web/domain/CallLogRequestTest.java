package org.motechproject.whp.mtraining.web.domain;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CallLogRequestTest {

    @Test
    public void shouldValidateCallLog() {
        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        DateTime tenMinutesAfterNow = now.plusMinutes(10);
        CallLogRequest validCallLogRequest = new CallLogRequest(746l, "unk001", "ssn001", 123L, newArrayList(new CallLogRecord(123L, 1, "COURSE", now.toString(), now.toString())),
                now.toString(), tenMinutesAfterNow.toString());
        List<ValidationError> expectedEmptyErrors = validCallLogRequest.validate();
        assertThat(expectedEmptyErrors.size(), Is.is(0));
    }

    @Test
    public void shouldReturnErrorsForMissingIdsOnly() {
        Long nullCallerId = null;
        String blankSessionId = null;
        String blankUniqueId = StringUtils.EMPTY;
        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        String nullCallCompletionDate = null;

        CallLogRequest callLogRequest = new CallLogRequest(nullCallerId, blankUniqueId, blankSessionId,
                123L, Collections.<CallLogRecord>emptyList(), now.toString(), nullCallCompletionDate);
        List<ValidationError> validationErrors = callLogRequest.validate();
        assertThat(validationErrors.size(), Is.is(3));
        assertTrue(validationErrors.contains(new ValidationError(ResponseStatus.MISSING_CALLER_ID)));
        assertTrue(validationErrors.contains(new ValidationError(ResponseStatus.MISSING_UNIQUE_ID)));
        assertTrue(validationErrors.contains(new ValidationError(ResponseStatus.MISSING_SESSION_ID)));
    }

    @Test
    public void shouldReturnErrorsForInvalidDate() {
        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();

        CallLogRequest callLogRequest = new CallLogRequest(12234l, "UniqueId", "sessionId",
                123L, Collections.<CallLogRecord>emptyList(), "2001-32-12T00:00:00.000Z", now.toString());
        List<ValidationError> validationErrors = callLogRequest.validate();
        assertThat(validationErrors.size(), Is.is(1));
        assertTrue(validationErrors.contains(new ValidationError(ResponseStatus.INVALID_DATE_TIME)));
    }

    @Test
    public void shouldReturnErrorsIfBothCallStartTimeAndCallEndTimeIsNull() {
        CallLogRequest callLogRequest = new CallLogRequest(12234l, "UniqueId", "sessionId",
                123L, Collections.<CallLogRecord>emptyList(), null, null);

        List<ValidationError> validationErrors = callLogRequest.validate();

        assertThat(validationErrors.size(), Is.is(1));
        assertTrue(validationErrors.contains(new ValidationError(ResponseStatus.MISSING_TIME)));
    }


    @Test
    public void shouldReturnOnlyCallLogRequestValidationFailuresWhenCallLogRequestIsInvalid() {
        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        DateTime tenMinutesAfterNow = now.plusMinutes(10);
        CallLogRecord invalidCallLogRecord = new CallLogRecord();
        List<CallLogRecord> callLogRecords = Arrays.asList(invalidCallLogRecord);
        CallLogRequest invalidCallLogRequestWithCallRecords = new CallLogRequest(null, null, "", 123L, callLogRecords, now.toString(), tenMinutesAfterNow.toString());
        List<ValidationError> validationErrors = invalidCallLogRequestWithCallRecords.validate();
        assertThat(validationErrors.size(), Is.is(3));
    }

    @Test
    public void shouldValidateCallLogRecords() {
        List<CallLogRecord> callLogRecords = new ArrayList<>();
        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        DateTime tenMinutesAddedToNow = now.plusMinutes(10);
        callLogRecords.add(new CallLogRecord(null, 1, "FOO", now.toString(), tenMinutesAddedToNow.toString()));

        CallLogRequest callLogRequest = new CallLogRequest(777897l, "unk001", "ssn001", 123L, callLogRecords, now.toString(), tenMinutesAddedToNow.toString());

        List<ValidationError> validationErrors = callLogRequest.validate();
        assertThat(validationErrors.size(), Is.is(2));
        assertTrue(validationErrors.contains(new ValidationError(ResponseStatus.MISSING_CONTENT_ID)));
        assertTrue(validationErrors.contains(new ValidationError(ResponseStatus.INVALID_CALL_LOG_TYPE)));
    }

}
