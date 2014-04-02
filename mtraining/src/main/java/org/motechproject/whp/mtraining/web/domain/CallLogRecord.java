package org.motechproject.whp.mtraining.web.domain;

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
            validationErrors.add(new ValidationError(MISSING_CONTENT_ID.getCode(), errorMessage()));
        }
        if (version == null) {
            validationErrors.add(new ValidationError(MISSING_VERSION.getCode(), errorMessage()));
        }
        if (CallLogRecordType.from(type) == null) {
            validationErrors.add(new ValidationError(INVALID_CALL_LOG_TYPE.getCode(), errorMessage()));
        }
        if (isBlank(startTime) && isBlank(endTime)) {
            validationErrors.add(new ValidationError(MISSING_TIME.getCode(), errorMessage()));
        }
        if (!isBlank(startTime) && !ISODateTimeUtil.validate(startTime)) {
            validationErrors.add(new ValidationError(INVALID_DATE_TIME.getCode(), errorMessage()));
        }
        if (!isBlank(endTime) && !ISODateTimeUtil.validate(endTime)) {
            validationErrors.add(new ValidationError(INVALID_DATE_TIME.getCode(), errorMessage()));
        }
        return validationErrors;
    }

    private String errorMessage() {
        return String.format("Invalid %s Call Log", this.type);
    }
}