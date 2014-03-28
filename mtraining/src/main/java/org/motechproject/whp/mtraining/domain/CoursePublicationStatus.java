package org.motechproject.whp.mtraining.domain;

import org.joda.time.DateTime;
import org.motechproject.mtraining.util.ISODateTimeUtil;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.UUID;

@PersistenceCapable(table = "course_publication_status", identityType = IdentityType.APPLICATION)
public class CoursePublicationStatus {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;

    @Persistent(column = "course_id")
    private UUID courseId;
    @Persistent
    private Integer version;
    @Persistent(column = "published_to_ivr")
    private boolean publishedToIvr;

    @Persistent(column = "publish_attempted_on")
    @Temporal(TemporalType.TIMESTAMP)
    private DateTime publishAttemptedOn;


    public CoursePublicationStatus(UUID courseId, Integer version, boolean publishedToIvr) {
        this.courseId = courseId;
        this.version = version;
        this.publishedToIvr = publishedToIvr;
        this.publishAttemptedOn = ISODateTimeUtil.nowInTimeZoneUTC();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CoursePublicationStatus coursePublicationStatus = (CoursePublicationStatus) o;

        if (publishedToIvr != coursePublicationStatus.publishedToIvr) return false;
        if (courseId != null ? !courseId.equals(coursePublicationStatus.courseId) : coursePublicationStatus.courseId != null) return false;
        if (version != null ? !version.equals(coursePublicationStatus.version) : coursePublicationStatus.version != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (courseId != null ? courseId.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (publishedToIvr ? 1 : 0);
        return result;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public Integer getVersion() {
        return version;
    }
}
