package org.motechproject.whp.mtraining.web.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.mtraining.util.ISODateTimeUtil;

import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_DATE_TIME;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.OK;

public class CallLogRequest extends CallDetailsRequest {

    @JsonProperty("content")
    private List<Node> nodes;
    @JsonProperty
    private String callStartTime;
    @JsonProperty
    private String callEndTime;

    public CallLogRequest() {
    }

    public CallLogRequest(Long callerId, String uniqueId, String sessionId, List<Node> nodes, String callStartTime, String callEndTime) {
        super(callerId, uniqueId, sessionId);
        this.nodes = nodes;
        this.callStartTime = callStartTime;
        this.callEndTime = callEndTime;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public String getCallStartTime() {
        return callStartTime;
    }

    public String getCallEndTime() {
        return callEndTime;
    }

    public ResponseStatus validate() {
        ResponseStatus validationStatus = super.validate();
        if (!validationStatus.isValid()) return validationStatus;
        for (Node node : nodes) {
            ResponseStatus validationResponse = node.validate();
            if (!validationResponse.isValid()) return validationResponse;
        }
        if (!isBlank(callStartTime) && !ISODateTimeUtil.validate(callStartTime))
            return INVALID_DATE_TIME.appendMessage(" for call Start Time");
        if (!isBlank(callEndTime) && !ISODateTimeUtil.validate(callEndTime))
            return INVALID_DATE_TIME.appendMessage(" for call End Time");
        return OK;
    }
}