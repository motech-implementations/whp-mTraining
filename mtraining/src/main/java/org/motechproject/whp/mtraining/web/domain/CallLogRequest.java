package org.motechproject.whp.mtraining.web.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.csv.domain.NodeType;

import java.util.UUID;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_DATE_TIME;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_NODE_TYPE;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_TIME;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.OK;

public class CallLogRequest extends CallDetailsRequest {

    @JsonProperty
    private UUID nodeId;
    @JsonProperty
    private Integer nodeVersion;
    @JsonProperty
    private String nodeType;
    @JsonProperty
    private String startTime;
    @JsonProperty
    private String endTime;

    public CallLogRequest() {
    }

    public CallLogRequest(Long callerId, String uniqueId, String sessionId, UUID nodeId, Integer nodeVersion, String nodeType, String startTime, String endTime) {
        super(callerId, uniqueId, sessionId);
        this.nodeId = nodeId;
        this.nodeVersion = nodeVersion;
        this.nodeType = nodeType;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public UUID getNodeId() {
        return nodeId;
    }

    public Integer getNodeVersion() {
        return nodeVersion;
    }

    public String getNodeType() {
        return nodeType;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public ResponseStatus validate() {
        ResponseStatus validationStatus = super.validate();
        if (!validationStatus.isValid()) return validationStatus;
        if (nodeId == null || nodeVersion == null)
            return ResponseStatus.MISSING_NODE;
        if (NodeType.from(nodeType) == null)
            return INVALID_NODE_TYPE;
        if (isBlank(startTime) && isBlank(endTime))
            return MISSING_TIME;
        if (!isBlank(startTime) && !ISODateTimeUtil.validate(startTime))
            return INVALID_DATE_TIME;
        if (!isBlank(endTime) && !ISODateTimeUtil.validate(endTime))
            return INVALID_DATE_TIME;
        return OK;
    }
}