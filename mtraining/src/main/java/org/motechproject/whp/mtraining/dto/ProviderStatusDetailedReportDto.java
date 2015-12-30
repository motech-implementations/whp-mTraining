package org.motechproject.whp.mtraining.dto;

import org.joda.time.DateTime;

public class ProviderStatusDetailedReportDto {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private Long providerId;

    private String trainingStartDate;

    private String currentStatus;

    private String courseLocation;

    private String timeSinceInCurrentLocation;

    public ProviderStatusDetailedReportDto() {
    }

    public ProviderStatusDetailedReportDto(long providerId, DateTime trainingStartDate, String currentStatus, String courseLocation, String timeSinceInCurrentLocation) {
        this.providerId = providerId;
        if (trainingStartDate != null) {
            this.trainingStartDate = trainingStartDate.toString(DATE_FORMAT);
        }
        this.currentStatus = currentStatus;
        this.courseLocation = courseLocation;
        this.timeSinceInCurrentLocation = timeSinceInCurrentLocation;
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

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getCourseLocation() {
        return courseLocation;
    }

    public void setCourseLocation(String courseLocation) {
        this.courseLocation = courseLocation;
    }

    public String getTimeSinceInCurrentLocation() {
        return timeSinceInCurrentLocation;
    }

    public void setTimeSinceInCurrentLocation(String timeSinceInCurrentLocation) {
        this.timeSinceInCurrentLocation = timeSinceInCurrentLocation;
    }
}
