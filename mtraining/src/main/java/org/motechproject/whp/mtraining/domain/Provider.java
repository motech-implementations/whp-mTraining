package org.motechproject.whp.mtraining.domain;

import org.motechproject.whp.mtraining.web.domain.ActivationStatus;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;
import javax.validation.constraints.NotNull;

import static org.apache.commons.lang.RandomStringUtils.randomAlphanumeric;

@PersistenceCapable(table = "provider", identityType = IdentityType.APPLICATION)
public class Provider {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;

    @Persistent(column = "primary_contact_number", defaultFetchGroup = "true")
    @Unique(name = "primary_contact_number")
    @NotNull
    private Long callerId;

    @Column(name = "district", allowsNull = "false")
    private String district;
    @Column(name = "block", allowsNull = "false")
    private String block;
    @Column(name = "state", allowsNull = "false")
    private String state;

    @Column(name = "activation_status", allowsNull = "false")
    private String activationStatus;

    @Column(name = "remedy_id")
    @Unique(name = "remedy_id")
    @NotNull
    private String remedyId;

    public Provider(String remedyId, Long callerId, ActivationStatus activationStatus, String district, String block, String state) {
        this.callerId = callerId;
        this.district = district;
        this.block = block;
        this.state = state;
        this.remedyId = remedyId;
        this.activationStatus = activationStatus.getStatus();
    }

    public Provider() {

    }

    public Long getCallerId() {
        return callerId;
    }

    public Long getId() {
        return id;
    }

    public String getActivationStatus() {
        return activationStatus;
    }

    public String getRemedyId() {
        return remedyId;
    }

    public String getDistrict() {
        return district;
    }

    public String getBlock() {
        return block;
    }

    public String getState() {
        return state;
    }
}