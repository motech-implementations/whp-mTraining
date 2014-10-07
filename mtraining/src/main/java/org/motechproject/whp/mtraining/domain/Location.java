package org.motechproject.whp.mtraining.domain;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mtraining.domain.MdsEntity;
import org.motechproject.whp.mtraining.util.CustomDateDeserializer;
import org.motechproject.whp.mtraining.util.CustomDateSerializer;

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
        if (level == null) {
            if ((block == null || block.isEmpty()) && (district == null || district.isEmpty())) {
                this.level = STATE_LEVEL;
            } else if (block == null || block.isEmpty()) {
                this.level = DISTRICT_LEVEL;
            } else {
                this.level = BLOCK_LEVEL;
            }
        }
        return level;
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
