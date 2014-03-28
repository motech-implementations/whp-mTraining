package org.motechproject.whp.mtraining.domain;

import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class CourseTest {

    @Test
    public void shouldTestCourseEquality() {
        UUID courseId = UUID.randomUUID();
        CoursePublicationAttempt coursePublicationAttempt = new CoursePublicationAttempt(courseId, 1, true);
        CoursePublicationAttempt sameCoursePublicationAttempt = new CoursePublicationAttempt(courseId, 1, true);
        CoursePublicationAttempt coursePublicationAttemptWithDifferentVersion = new CoursePublicationAttempt(courseId, 2, true);
        CoursePublicationAttempt publishedCourseWithDifferentStatePublicationAttempt = new CoursePublicationAttempt(courseId, 2, false);
        CoursePublicationAttempt courseWithDifferentCoursePublicationAttemptId = new CoursePublicationAttempt(UUID.randomUUID(), 1, true);

        assertTrue(coursePublicationAttempt.equals(sameCoursePublicationAttempt));
        assertFalse(coursePublicationAttempt.equals(null));
        assertFalse(coursePublicationAttempt.equals(courseWithDifferentCoursePublicationAttemptId));
        assertFalse(coursePublicationAttempt.equals(coursePublicationAttemptWithDifferentVersion));
        assertFalse(coursePublicationAttempt.equals(publishedCourseWithDifferentStatePublicationAttempt));
    }

    @Test
    public void shouldTestThatEqualCoursesHaveSameHashCodes() {
        UUID courseId = UUID.randomUUID();
        CoursePublicationAttempt coursePublicationAttempt = new CoursePublicationAttempt(courseId, 1, true);
        CoursePublicationAttempt sameCoursePublicationAttempt = new CoursePublicationAttempt(courseId, 1, true);
        assertThat(coursePublicationAttempt.hashCode(), Is.is(sameCoursePublicationAttempt.hashCode()));
    }
}
