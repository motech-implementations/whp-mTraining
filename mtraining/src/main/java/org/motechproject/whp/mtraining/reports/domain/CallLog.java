package org.motechproject.whp.mtraining.reports.domain;

import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.whp.mtraining.csv.domain.CallLogRecordType;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.UUID;

@Entity
public class CallLog {

    @Field
    private Long id;

    @Field
    private String remedyId;

    @Field
    private Long callerId;

    @Field
    private String uniqueId;

    @Field
    private String sessionId;

    @Field
    private Long courseId;

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
                   Long courseId, CallLogRecordType callLogRecordType, DateTime startTime, DateTime endTime, String status, boolean restarted) {
        this.remedyId = remedyId;
        this.callerId = callerId;
        this.uniqueId = uniqueId;
        this.sessionId = sessionId;
        this.courseId = courseId;
        this.callLogRecordType = callLogRecordType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.restarted = restarted;
    }

    public Long getId() {
        return id;
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

    public DateTime getStartTime() {
        return startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public Boolean getRestarted() {
        return restarted;
    }
}