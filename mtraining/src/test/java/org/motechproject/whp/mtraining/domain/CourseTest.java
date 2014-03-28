package org.motechproject.whp.mtraining.domain;

import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class CourseTest {

    @Test
    public void shouldTestCourseEquality() {
        UUID courseId = UUID.randomUUID();
        CoursePublicationStatus coursePublicationStatus = new CoursePublicationStatus(courseId, 1, true);
        CoursePublicationStatus sameCoursePublicationStatus = new CoursePublicationStatus(courseId, 1, true);
        CoursePublicationStatus coursePublicationStatusWithDifferentVersion = new CoursePublicationStatus(courseId, 2, true);
        CoursePublicationStatus coursePublicationStatusWithDifferentPublishedState = new CoursePublicationStatus(courseId, 2, false);
        CoursePublicationStatus courseWithDifferentCoursePublicationStatusId = new CoursePublicationStatus(UUID.randomUUID(), 1, true);

        assertTrue(coursePublicationStatus.equals(sameCoursePublicationStatus));
        assertFalse(coursePublicationStatus.equals(null));
        assertFalse(coursePublicationStatus.equals(courseWithDifferentCoursePublicationStatusId));
        assertFalse(coursePublicationStatus.equals(coursePublicationStatusWithDifferentVersion));
        assertFalse(coursePublicationStatus.equals(coursePublicationStatusWithDifferentPublishedState));
    }

    @Test
    public void shouldTestThatEqualCoursesHaveSameHashCodes() {
        UUID courseId = UUID.randomUUID();
        CoursePublicationStatus coursePublicationStatus = new CoursePublicationStatus(courseId, 1, true);
        CoursePublicationStatus sameCoursePublicationStatus = new CoursePublicationStatus(courseId, 1, true);
        assertThat(coursePublicationStatus.hashCode(), Is.is(sameCoursePublicationStatus.hashCode()));
    }
}
