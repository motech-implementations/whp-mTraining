package org.motechproject.whp.mtraining.reports.domain;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mtraining.domain.MdsEntity;
import org.motechproject.whp.mtraining.csv.domain.CallLogRecordType;
import org.motechproject.whp.mtraining.util.CustomDateDeserializer;
import org.motechproject.whp.mtraining.util.CustomDateSerializer;

import java.util.UUID;

@Entity
public class CallLog extends MdsEntity {

    @Field
    private String remedyId;

    @Field
    private Long callerId;

    @Field
    private String uniqueId;

    @Field
    private String sessionId;

    @Field
    private String courseId;

    @Field
    private CallLogRecordType callLogRecordType;

    @Field
    private DateTime startTime;

    @Field
    private DateTime endTime;

    @Field
    private String status;

    @Field
    private Boolean restarted;

    public CallLog(String remedyId, Long callerId, String uniqueId, String sessionId,
                   UUID courseId, CallLogRecordType callLogRecordType, DateTime startTime, DateTime endTime, String status, boolean restarted) {
        this.remedyId = remedyId;
        this.callerId = callerId;
        this.uniqueId = uniqueId;
        this.sessionId = sessionId;
        this.courseId = courseId.toString();
        this.callLogRecordType = callLogRecordType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.restarted = restarted;
    }

    public String getRemedyId() {
        return remedyId;
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

    public CallLogRecordType getCallLogRecordType() {
        return callLogRecordType;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    public DateTime getStartTime() {
        return startTime;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    public DateTime getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public Boolean getRestarted() {
        return restarted;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    public DateTime getCreationDate() {
        return super.getCreationDate();
    }
}