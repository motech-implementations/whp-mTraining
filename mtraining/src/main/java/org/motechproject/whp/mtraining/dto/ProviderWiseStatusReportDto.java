package org.motechproject.whp.mtraining.dto;

import org.joda.time.DateTime;

public class ProviderWiseStatusReportDto {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private Long providerId;

    private String trainingStartDate;

    private String trainingEndDate;

    public ProviderWiseStatusReportDto() {
    }

    public ProviderWiseStatusReportDto(Long providerId, DateTime trainingStartDate, DateTime trainingEndDate) {
        this.providerId = providerId;
        if (trainingStartDate != null) {
            this.trainingStartDate = trainingStartDate.toString(DATE_FORMAT);
        }
        if (trainingEndDate != null) {
            this.trainingEndDate = trainingEndDate.toString(DATE_FORMAT);
        }
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public String getTrainingStartDate() {
        return trainingStartDate;
    }

    public void setTrainingStartDate(String trainingStartDate) {
        this.trainingStartDate = trainingStartDate;
    }

    public String getTrainingEndDate() {
        return trainingEndDate;
    }

    public void setTrainingEndDate(String trainingEndDate) {
        this.trainingEndDate = trainingEndDate;
    }
}
