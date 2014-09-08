package org.motechproject.whp.mtraining.domain;

import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mtraining.domain.MdsEntity;
import org.motechproject.whp.mtraining.reports.domain.BookmarkRequestType;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;


@Entity
public class BookmarkRequest extends MdsEntity {

    @Field
    private Long callerId;

    @Field
    private String uniqueId;

    @Field
    private String sessionId;

    @Field
    private int responseCode;

    @Field
    private String responseMessage;

    @Field
    private String remediId;

    @Field
    private BookmarkRequestType requestType;

    @Field
    private DateTime courseStartTime;

    @Field
    private Integer timeLeftToCompleteCourseInHrs;

    @Field
    private String courseStatus;

    @Field
    private BookmarkReport bookmarkReport;


    public BookmarkRequest(Long callerId, String uniqueId, String sessionId, ResponseStatus responseStatus, BookmarkRequestType requestType) {
        this.callerId = callerId;
        this.uniqueId = uniqueId;
        this.sessionId = sessionId;
        this.responseCode = responseStatus.getCode();
        this.responseMessage = responseStatus.getMessage();
        this.requestType = requestType;
    }
    public BookmarkRequest(String remediId, Long callerId, String uniqueId, String sessionId, ResponseStatus responseStatus, BookmarkRequestType requestType, String courseStartTime, Integer timeLeftToCompleteCourseInHrs, String courseStatus, BookmarkReport bookmarkReport) {
        this(callerId, uniqueId, sessionId, responseStatus, requestType);
        this.remediId = remediId;
        this.bookmarkReport = bookmarkReport;
        this.courseStartTime = ISODateTimeUtil.parseWithTimeZoneUTC(courseStartTime);
        this.timeLeftToCompleteCourseInHrs = timeLeftToCompleteCourseInHrs;
        this.courseStatus = courseStatus;
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

    public BookmarkReport getBookmarkReport() {
        return bookmarkReport;
    }

    public void setBookmarkReport(BookmarkReport bookmarkReport) {
        this.bookmarkReport = bookmarkReport;
    }
}
