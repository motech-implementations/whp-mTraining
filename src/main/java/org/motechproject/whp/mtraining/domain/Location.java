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
    private String village;

    @Persistent
    private String post;

    @Persistent
    private String block;

    @Persistent
    private String district;

    @Persistent
    private String state;

    @Persistent
    private Integer pincode;

    public Location(String village, String post, String block, String district, String state, Integer pincode) {
        this.village = village;
        this.post = post;
        this.block = block;
        this.district = district;
        this.state = state;
        this.pincode = pincode;
    }

    public Location(String state, Integer pincode) {
        this.state = state;
        this.pincode = pincode;
    }

    public String getVillage() {
        return village;
    }

    public String getPost() {
        return post;
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

    public Integer getPincode() {
        return pincode;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPincode(Integer pincode) {
        this.pincode = pincode;
    }
}
