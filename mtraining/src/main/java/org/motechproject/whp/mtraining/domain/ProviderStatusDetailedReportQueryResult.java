package org.motechproject.whp.mtraining.domain;

public class ProviderStatusDetailedReportQueryResult {

    private Long providerId;

    private String trainingStartDate;

    private String currentStatus;

    private String dateModified;

    private String coursePlanContentId;

    private String moduleContentId;

    private String chapterContentId;

    private String lessonContentId;

    private String quizContentId;

    public ProviderStatusDetailedReportQueryResult() {
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

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getCoursePlanContentId() {
        return coursePlanContentId;
    }

    public void setCoursePlanContentId(String coursePlanContentId) {
        this.coursePlanContentId = coursePlanContentId;
    }

    public String getModuleContentId() {
        return moduleContentId;
    }

    public void setModuleContentId(String moduleContentId) {
        this.moduleContentId = moduleContentId;
    }

    public String getChapterContentId() {
        return chapterContentId;
    }

    public void setChapterContentId(String chapterContentId) {
        this.chapterContentId = chapterContentId;
    }

    public String getLessonContentId() {
        return lessonContentId;
    }

    public void setLessonContentId(String lessonContentId) {
        this.lessonContentId = lessonContentId;
    }

    public String getQuizContentId() {
        return quizContentId;
    }

    public void setQuizContentId(String quizContentId) {
        this.quizContentId = quizContentId;
    }
}
