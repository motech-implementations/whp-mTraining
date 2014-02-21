package org.motechproject.whp.mtraining.domain;


import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table = "provider", identityType = IdentityType.APPLICATION)
public class Provider {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;

    @Persistent(column = "primary_contact_number", defaultFetchGroup = "true")
    private Long callerId;

    @Column(name = "location_id")
    private Location location;

    public Provider(Long callerId) {
        this.callerId = callerId;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Long getCallerId() {
        return callerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCallerId(Long callerId) {
        this.callerId = callerId;
    }

    public Location getLocation() {
        return location;
    }
}
