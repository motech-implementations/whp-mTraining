package org.motechproject.whp.mtraining.reports.domain;

import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity
public class CallDuration {

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
    private DateTime callStartTime;

    @Field
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
