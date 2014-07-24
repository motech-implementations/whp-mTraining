package org.motechproject.whp.mtraining.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mtraining.domain.MdsEntity;

@Entity
public class Location extends MdsEntity {

    private String block;

    private String district;

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
