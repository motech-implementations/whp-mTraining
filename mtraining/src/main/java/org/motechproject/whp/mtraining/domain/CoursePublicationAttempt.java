package org.motechproject.whp.mtraining.domain;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mtraining.domain.MdsEntity;
import org.motechproject.whp.mtraining.util.CustomDateDeserializer;
import org.motechproject.whp.mtraining.util.CustomDateSerializer;
import org.motechproject.whp.mtraining.util.PropertyUtil;

@Entity
public class CoursePublicationAttempt extends MdsEntity {

    @Field
    private Long courseId;

    @Field
    private String courseName;

    @Field
    private boolean publishedToIvr;

    @Field
    private int responseCode;

    @Field
    private String responseMessage;

    @Field
    private long version;


    public CoursePublicationAttempt(Long courseId, boolean publishedToIvr) {
        this.courseId = courseId;
        this.publishedToIvr = publishedToIvr;
    }

    public CoursePublicationAttempt(Long courseId, String courseName, boolean publishedToIvr, int responseCode, String responseMessage) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.publishedToIvr = publishedToIvr;
        this.responseCode = responseCode;
        this.responseMessage = PropertyUtil.truncate(responseMessage, 255);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CoursePublicationAttempt coursePublicationAttempt = (CoursePublicationAttempt) o;

        if (publishedToIvr != coursePublicationAttempt.publishedToIvr) return false;
        if (courseId != null ? !courseId.equals(coursePublicationAttempt.courseId) : coursePublicationAttempt.courseId != null) return false;
        return true;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public boolean isPublishedToIvr() {
        return publishedToIvr;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
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
        this.responseMessage = PropertyUtil.truncate(responseMessage, 255);
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
