package org.motechproject.whp.mtraining.domain;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table = "location", identityType = IdentityType.APPLICATION)
public class Location {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;

    @Persistent
    private String block;

    @Persistent
    private String district;

    @Persistent
    private String state;


    //For tests
    public Location() {
    }

    public Location(String block, String district, String state) {
        this.block = block;
        this.district = district;
        this.state = state;
    }

    public Location(String state) {
        this.state = state;
    }


    public String getBlock() {
        return block;
    }

    public String getDistrict() {
        return district;
    }

    public String getState() {
        return state;
    }


}
