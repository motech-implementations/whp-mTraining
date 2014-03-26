package org.motechproject.whp.mtraining.csv.request;

public class ProviderCsvRequest {

    private String remedi_id;
    private String primary_contact;
    private String providerstatus;
    private String state;
    private String block;
    private String district;

    public ProviderCsvRequest() {
    }

    public ProviderCsvRequest(String remedi_id, String primary_contact, String providerstatus, String state, String block, String district) {
        this.remedi_id = remedi_id;
        this.primary_contact = primary_contact;
        this.providerstatus = providerstatus;
        this.state = state;
        this.block = block;
        this.district = district;
    }

    public String getRemedi_id() {
        return remedi_id;
    }

    public void setRemedi_id(String remedi_id) {
        this.remedi_id = remedi_id;
    }

    public void setPrimary_contact(String primary_contact) {
        this.primary_contact = primary_contact;
    }

    public String getPrimary_contact_number() {
        return primary_contact;
    }

    public String getProviderstatus() {
        return providerstatus;
    }

    public String getPrimary_contact() {
        return primary_contact;
    }

    public void setProviderstatus(String providerstatus) {
        this.providerstatus = providerstatus;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}