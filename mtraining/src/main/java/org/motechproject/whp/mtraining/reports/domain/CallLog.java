package org.motechproject.whp.mtraining.reports.domain;

import org.joda.time.DateTime;
import org.motechproject.whp.mtraining.csv.domain.NodeType;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.UUID;

@PersistenceCapable(table = "call_log", identityType = IdentityType.APPLICATION)
public class CallLog {

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
    @Persistent(column = "node_id")
    private UUID nodeId;
    @Persistent(column = "node_version")
    private Integer nodeVersion;
    @Persistent(column = "node_type")
    private NodeType nodeType;
    @Persistent(column = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private DateTime startTime;
    @Persistent(column = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private DateTime endTime;
    @Persistent(column = "status")
    private String status;

    public CallLog(String remedyId, Long callerId, String uniqueId, String sessionId,
                   UUID nodeId, Integer nodeVersion, NodeType nodeType, DateTime startTime, DateTime endTime, String status) {
        this.remedyId = remedyId;
        this.callerId = callerId;
        this.uniqueId = uniqueId;
        this.sessionId = sessionId;
        this.nodeId = nodeId;
        this.nodeVersion = nodeVersion;
        this.nodeType = nodeType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
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

    public UUID getNodeId() {
        return nodeId;
    }

    public Integer getNodeVersion() {
        return nodeVersion;
    }

    public NodeType getNodeType() {
        return nodeType;
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
}