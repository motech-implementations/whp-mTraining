package org.motechproject.whp.mtraining.dto;

/**
 * Contract object representing a Location.
 * + block : the name of the block for a given geographical location
 * + district : the name of the district for a given geographical location
 * + state : the name of the state for a given geographical location
 */

public class LocationDto {
    private String block;
    private String district;
    private String state;

    public LocationDto() {
    }

    public LocationDto(String block, String district, String state) {
        this.block = block;
        this.district = district;
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
