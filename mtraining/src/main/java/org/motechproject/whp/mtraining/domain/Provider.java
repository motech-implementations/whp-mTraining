package org.motechproject.whp.mtraining.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mtraining.domain.MdsEntity;
import org.motechproject.whp.mtraining.web.domain.ProviderStatus;

@Entity
public class Provider extends MdsEntity {

    // primary contact number
    @Field
    private Long callerId;

    @Field
    private Location location;

    @Field
    private ProviderStatus providerStatus;

    @Field
    private String remediId;

    public Provider(String remediId, Long callerId, ProviderStatus providerStatus, Location location) {
        this.remediId = remediId;
        this.callerId = callerId;
        this.providerStatus = providerStatus;
        this.location = location;
    }

    public Provider() {
    }

    public Long getCallerId() {
        return callerId;
    }

    public ProviderStatus getProviderStatus() {
        return providerStatus;
    }

    public String getRemediId() {
        return remediId;
    }

    public Location getLocation() {
        return location;
    }

    public void setCallerId(Long callerId) {
        this.callerId = callerId;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setProviderStatus(ProviderStatus providerStatus) {
        this.providerStatus = providerStatus;
    }

    public void setRemediId(String remediId) {
        this.remediId = remediId;
    }
}