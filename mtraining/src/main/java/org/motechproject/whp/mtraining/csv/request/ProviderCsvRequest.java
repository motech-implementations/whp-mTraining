package org.motechproject.whp.mtraining.csv.request;

public class ProviderCsvRequest {

    private String remedyId;
    private String primaryContactNumber;
    private String activationStatus;
    private String state;
    private String block;
    private String district;

    public ProviderCsvRequest() {
    }

    public ProviderCsvRequest(String remedyId, String primaryContactNumber, String activationStatus, String state, String block, String district) {
        this.remedyId = remedyId;
        this.primaryContactNumber = primaryContactNumber;
        this.activationStatus = activationStatus;
        this.state = state;
        this.block = block;
        this.district = district;
    }

    public String getRemedyId() {
        return remedyId;
    }

    public void setRemedyId(String remedyId) {
        this.remedyId = remedyId;
    }

    public String getPrimaryContactNumber() {
        return primaryContactNumber;
    }

    public void setPrimaryContactNumber(String primaryContactNumber) {
        this.primaryContactNumber = primaryContactNumber;
    }

    public String getActivationStatus() {
        return activationStatus;
    }

    public void setActivationStatus(String activationStatus) {
        this.activationStatus = activationStatus;
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