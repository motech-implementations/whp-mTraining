package org.motechproject.whp.mtraining.dto;

public class TrainingStatusReportDto {

    private String district;

    private Long providerRegistered;

    private Long providerCompletedCourse;

    private Long providerInCourse;

    public TrainingStatusReportDto() {
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Long getProviderRegistered() {
        return providerRegistered;
    }

    public void setProviderRegistered(Long providerRegistered) {
        this.providerRegistered = providerRegistered;
    }

    public Long getProviderCompletedCourse() {
        return providerCompletedCourse;
    }

    public void setProviderCompletedCourse(Long providerCompletedCourse) {
        this.providerCompletedCourse = providerCompletedCourse;
    }

    public Long getProviderInCourse() {
        return providerInCourse;
    }

    public void setProviderInCourse(Long providerInCourse) {
        this.providerInCourse = providerInCourse;
    }
}
