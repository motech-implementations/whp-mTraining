package org.motechproject.whp.mtraining.domain;

import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.whp.mtraining.util.ISODateTimeUtil;

@Entity
public class CoursePublicationAttempt {

    @Field
    private Long id;

    @Field
    private Long courseId;

    @Field
    private boolean publishedToIvr;

    @Field
    private DateTime publishAttemptedOn;


    public CoursePublicationAttempt(Long courseId,boolean publishedToIvr) {
        this.courseId = courseId;
        this.publishedToIvr = publishedToIvr;
        this.publishAttemptedOn = ISODateTimeUtil.nowInTimeZoneUTC();
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

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (courseId != null ? courseId.hashCode() : 0);
        result = 31 * result + (publishedToIvr ? 1 : 0);
        return result;
    }

    public Long getCourseId() {
        return courseId;
    }

    public boolean isPublishedToIvr() {
        return publishedToIvr;
    }
}
