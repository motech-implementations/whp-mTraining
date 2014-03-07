package org.motechproject.whp.mtraining.domain;

import org.joda.time.DateTime;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.UUID;

@PersistenceCapable(table = "course", identityType = IdentityType.APPLICATION)
public class Course {

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
    private DateTime publisAttemptedOn;


    public Course(UUID courseId, Integer version, boolean publishedToIvr) {
        this.courseId = courseId;
        this.version = version;
        this.publishedToIvr = publishedToIvr;
        this.publisAttemptedOn = DateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Course course = (Course) o;

        if (publishedToIvr != course.publishedToIvr) return false;
        if (courseId != null ? !courseId.equals(course.courseId) : course.courseId != null) return false;
        if (version != null ? !version.equals(course.version) : course.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (courseId != null ? courseId.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (publishedToIvr ? 1 : 0);
        result = 31 * result + (publisAttemptedOn != null ? publisAttemptedOn.hashCode() : 0);
        return result;
    }
}
