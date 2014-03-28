package org.motechproject.whp.mtraining.domain;

import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
@EmbeddedOnly
public class Location {

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
