package org.motechproject.whp.mtraining.web.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.csv.domain.CallLogRecordType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_CALL_LOG_TYPE;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.INVALID_DATE_TIME;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_CONTENT_ID;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_TIME;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.MISSING_VERSION;

public class CallLogRecord {

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
    private boolean restarted;

    public CallLogRecord() {
    }

    public CallLogRecord(UUID contentId, Integer version, String recordType, String startTime, String endTime) {
        this.contentId = contentId;
        this.version = version;
        this.type = recordType;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public List<ValidationError> validate() {
        List<ValidationError> validationErrors = new ArrayList<>();
        if (contentId == null) {
            validationErrors.add(errorMessage(MISSING_CONTENT_ID));
        }
        if (version == null) {
            validationErrors.add(errorMessage(MISSING_VERSION));
        }
        if (CallLogRecordType.from(type) == null) {
            validationErrors.add(errorMessage(INVALID_CALL_LOG_TYPE));
        }
        if (isBlank(startTime) && isBlank(endTime)) {
            validationErrors.add(errorMessage(MISSING_TIME));
        }
        if (!isBlank(startTime) && !ISODateTimeUtil.validate(startTime)) {
            validationErrors.add(errorMessage(INVALID_DATE_TIME));
        }
        if (!isBlank(endTime) && !ISODateTimeUtil.validate(endTime)) {
            validationErrors.add(errorMessage(INVALID_DATE_TIME));
        }
        return validationErrors;
    }

    @JsonIgnore
    public boolean isRestarted() {
        return restarted;
    }

    private ValidationError errorMessage(ResponseStatus status) {
        String content = contentId != null ? contentId.toString() : type;
        String message = status.getMessage().concat(" for " + content);
        return new ValidationError(status.getCode(), message);
    }
}