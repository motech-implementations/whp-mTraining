package org.motechproject.whp.mtraining.web.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.mtraining.util.ISODateTimeUtil;

import java.util.UUID;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_CALL_STATUS;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_DATE_TIME;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_NODE;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_TIME;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.OK;

public class Node {

    @JsonProperty()
    private UUID contentId;
    @JsonProperty
    private Integer version;
    @JsonProperty
    private String type;
    @JsonProperty
    private String startTime;
    @JsonProperty
    private String endTime;
    @JsonProperty
    private Boolean restarted;

    public Node() {
    }

    public Node(UUID contentId, Integer version, String nodeType, String startTime, String endTime, Boolean restarted) {
        this.contentId = contentId;
        this.version = version;
        this.type = nodeType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.restarted = restarted;
    }

    public UUID getContentId() {
        return contentId;
    }

    public Integer getVersion() {
        return version;
    }

    public String getType() {
        return type;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public Boolean getRestarted() {
        return restarted;
    }

    public ResponseStatus validate() {
        String node = " for content: " + type;
        if (contentId == null || version == null) {
            return MISSING_NODE.appendMessage(node);
        }
        if (isBlank(startTime) && isBlank(endTime))
            return MISSING_TIME.appendMessage(node);
        if (!isBlank(startTime) && !ISODateTimeUtil.validate(startTime))
            return INVALID_DATE_TIME.appendMessage(node);
        if (!isBlank(endTime) && !ISODateTimeUtil.validate(endTime))
            return INVALID_DATE_TIME.appendMessage(node);
        if (restarted == null)
            return INVALID_CALL_STATUS;
        return OK;
    }
}