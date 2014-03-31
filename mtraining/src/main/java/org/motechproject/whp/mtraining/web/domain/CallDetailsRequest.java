package org.motechproject.whp.mtraining.web.domain;

import org.codehaus.jackson.annotate.JsonProperty;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_CALLER_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_SESSION_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_UNIQUE_ID;

public class CallDetailsRequest {

    @JsonProperty
    private Long callerId;
    @JsonProperty
    private String uniqueId;
    @JsonProperty
    private String sessionId;

    public CallDetailsRequest() {
    }

    public CallDetailsRequest(Long callerId, String uniqueId, String sessionId) {
        this.callerId = callerId;
        this.uniqueId = uniqueId;
        this.sessionId = sessionId;
    }

    public Long getCallerId() {
        return callerId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public ResponseStatus validate() {
        if (callerId == null)
            return MISSING_CALLER_ID;
        if (isBlank(uniqueId))
            return MISSING_UNIQUE_ID;
        if (isBlank(sessionId))
            return MISSING_SESSION_ID;
        return ResponseStatus.OK;
    }
}