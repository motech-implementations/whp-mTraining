package org.motechproject.whp.mtraining.domain;

import org.motechproject.whp.mtraining.web.domain.ProviderStatus;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;
import javax.validation.constraints.NotNull;

@PersistenceCapable(table = "provider", identityType = IdentityType.APPLICATION)
public class Provider {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;

    @Persistent(column = "primary_contact_number", defaultFetchGroup = "true")
    @Unique(name = "primary_contact_number")
    @NotNull
    private Long callerId;

    @Embedded(members = {
            @Persistent(name = "district", columns = @Column(name = "district", allowsNull = "false")),
            @Persistent(name = "block", columns = @Column(name = "block", allowsNull = "false")),
            @Persistent(name = "state", columns = @Column(name = "state", allowsNull = "false")),
    })
    private Location location;

    @Column(name = "provider_status", allowsNull = "false")
    private String providerStatus;

    @Column(name = "remedi_id")
    @Unique(name = "remedi_id")
    @NotNull
    private String remediId;

    public Provider(String remediId, Long callerId, ProviderStatus providerStatus, Location location) {
        this.remediId = remediId;
        this.callerId = callerId;
        this.providerStatus = providerStatus.getStatus();
        this.location = location;
    }

    public Provider() {
    }

    public Long getCallerId() {
        return callerId;
    }

    public Long getId() {
        return id;
    }

    public String getProviderStatus() {
        return providerStatus;
    }

    public String getRemediId() {
        return remediId;
    }

    public Location getLocation() {
        return location;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCallerId(Long callerId) {
        this.callerId = callerId;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setProviderStatus(String providerStatus) {
        this.providerStatus = providerStatus;
    }

    public void setRemediId(String remediId) {
        this.remediId = remediId;
    }
}