package org.motechproject.whp.mtraining.web.domain;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class CallLogRequest extends IVRRequest {

    @JsonProperty
    private String callStartTime;
    @JsonProperty
    private String callEndTime;

    @JsonProperty("content")
    private List<CallLogRecord> callLogRecords;

    public CallLogRequest() {
    }

    public CallLogRequest(Long callerId, String uniqueId, String sessionId, List<CallLogRecord> callLogRecords, String callStartTime, String callEndTime) {
        super(callerId, uniqueId, sessionId);
        this.callLogRecords = callLogRecords;
        this.callStartTime = callStartTime;
        this.callEndTime = callEndTime;
    }

    public List<CallLogRecord> getCallLogRecords() {
        return callLogRecords;
    }

    public List<ValidationError> validate() {
        List<ValidationError> validationErrors = super.validate();

        // when callerId or sessionId or uniqueId is missing then no need to validate records,return error straight away
        if (!validationErrors.isEmpty()) {
            return validationErrors;
        }

        for (CallLogRecord callLogRecord : callLogRecords) {
            List<ValidationError> errors = callLogRecord.validate();
            if (!errors.isEmpty()) {
                validationErrors.addAll(errors);
            }
        }
        return validationErrors;
    }

    public String getCallStartTime() {
        return callStartTime;
    }

    public void setCallStartTime(String callStartTime) {
        this.callStartTime = callStartTime;
    }

    public String getCallEndTime() {
        return callEndTime;
    }

    public void setCallEndTime(String callEndTime) {
        this.callEndTime = callEndTime;
    }
}