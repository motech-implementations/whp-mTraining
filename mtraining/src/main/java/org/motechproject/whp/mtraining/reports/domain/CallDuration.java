package org.motechproject.whp.mtraining.reports.domain;

import org.joda.time.DateTime;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@PersistenceCapable(table = "call_duration", identityType = IdentityType.APPLICATION)
public class CallDuration {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;
    @Persistent(column = "provider_remedy_id")
    private String remedyId;
    @Persistent(column = "caller_id")
    private Long callerId;
    @Persistent(column = "unique_id")
    private String uniqueId;
    @Persistent(column = "session_id")
    private String sessionId;
    @Persistent(column = "call_start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private DateTime callStartTime;
    @Persistent(column = "call_end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private DateTime callEndTime;

    public CallDuration(String remedyId, Long callerId, String uniqueId, String sessionId, DateTime callStartTime, DateTime callEndTime) {
        this.remedyId = remedyId;
        this.callerId = callerId;
        this.uniqueId = uniqueId;
        this.sessionId = sessionId;
        this.callStartTime = callStartTime;
        this.callEndTime = callEndTime;
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

    public DateTime getCallStartTime() {
        return callStartTime;
    }

    public DateTime getCallEndTime() {
        return callEndTime;
    }
}
