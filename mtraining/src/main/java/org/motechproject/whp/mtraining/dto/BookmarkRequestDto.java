package org.motechproject.whp.mtraining.dto;

import org.joda.time.DateTime;
import org.motechproject.whp.mtraining.reports.domain.BookmarkRequestType;

public class BookmarkRequestDto extends MdsEntityDto {

    private Long callerId;

    private String uniqueId;

    private String sessionId;

    private int responseCode;

    private String responseMessage;

    private String remediId;

    private BookmarkRequestType requestType;

    private DateTime courseStartTime;

    private Integer timeLeftToCompleteCourseInHrs;

    private String courseStatus;

    private BookmarkReportDto BookmarkReportDto;

    public BookmarkRequestDto(long id, DateTime creationDate, DateTime modificationDate, Long callerId, String uniqueId, String sessionId, int responseCode,
                              String responseMessage, String remediId, BookmarkRequestType requestType, DateTime courseStartTime,
                              Integer timeLeftToCompleteCourseInHrs, String courseStatus, BookmarkReportDto BookmarkReportDto) {
        super(id, creationDate, modificationDate);
        this.callerId = callerId;
        this.uniqueId = uniqueId;
        this.sessionId = sessionId;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.remediId = remediId;
        this.requestType = requestType;
        this.courseStartTime = courseStartTime;
        this.timeLeftToCompleteCourseInHrs = timeLeftToCompleteCourseInHrs;
        this.courseStatus = courseStatus;
        this.BookmarkReportDto = BookmarkReportDto;
    }

    public Long getCallerId() {
        return callerId;
    }

    public void setCallerId(Long callerId) {
        this.callerId = callerId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getRemediId() {
        return remediId;
    }

    public void setRemediId(String remediId) {
        this.remediId = remediId;
    }

    public BookmarkRequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(BookmarkRequestType requestType) {
        this.requestType = requestType;
    }

    public DateTime getCourseStartTime() {
        return courseStartTime;
    }

    public void setCourseStartTime(DateTime courseStartTime) {
        this.courseStartTime = courseStartTime;
    }

    public Integer getTimeLeftToCompleteCourseInHrs() {
        return timeLeftToCompleteCourseInHrs;
    }

    public void setTimeLeftToCompleteCourseInHrs(Integer timeLeftToCompleteCourseInHrs) {
        this.timeLeftToCompleteCourseInHrs = timeLeftToCompleteCourseInHrs;
    }

    public String getCourseStatus() {
        return courseStatus;
    }

    public void setCourseStatus(String courseStatus) {
        this.courseStatus = courseStatus;
    }

    public BookmarkReportDto getBookmarkReportDto() {
        return BookmarkReportDto;
    }

    public void setBookmarkReportDto(BookmarkReportDto BookmarkReportDto) {
        this.BookmarkReportDto = BookmarkReportDto;
    }
}
