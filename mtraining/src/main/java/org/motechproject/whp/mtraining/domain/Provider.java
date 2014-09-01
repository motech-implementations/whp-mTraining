package org.motechproject.whp.mtraining.domain;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mtraining.domain.MdsEntity;
import org.motechproject.whp.mtraining.util.CustomDateDeserializer;
import org.motechproject.whp.mtraining.util.CustomDateSerializer;
import org.motechproject.whp.mtraining.web.domain.ProviderStatus;

import javax.jdo.annotations.Unique;

@Entity
public class Provider extends MdsEntity {

    // primary contact number
    @Field
    @Unique
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

    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    public DateTime getCreationDate() {
        return super.getCreationDate();
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    public DateTime getModificationDate() {
        return super.getModificationDate();
    }
}
