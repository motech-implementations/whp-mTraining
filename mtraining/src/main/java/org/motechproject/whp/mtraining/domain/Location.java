package org.motechproject.whp.mtraining.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mtraining.domain.MdsEntity;

@Entity
public class Location extends MdsEntity {

    public final static int STATE_LEVEL = 1;
    public final static int DISTRICT_LEVEL = 2;
    public final static int BLOCK_LEVEL = 3;

    @Field
    private String block;

    @Field
    private String district;

    @Field(required=true)
    private String state;

    /**
     * Location level: 1-state, 2-district, 3-block
     */
    @Field
    private Integer level;

    public Location(String block, String district, String state) {
        this.block = block;
        this.district = district;
        this.state = state;
        if (block == null && district == null) {
            this.level = STATE_LEVEL;
        } else if (block == null) {
            this.level = DISTRICT_LEVEL;
        } else {
            this.level = BLOCK_LEVEL;
        }
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

    public int getLevel() {
        return level;
    }
}
