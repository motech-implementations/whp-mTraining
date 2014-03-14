package org.motechproject.whp.mtraining.domain;

import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class CourseTest {

    @Test
    public void shouldTestCourseEquality() {
        UUID courseId = UUID.randomUUID();
        Course course = new Course(courseId, 1, true);
        Course sameCourse = new Course(courseId, 1, true);
        Course courseWithDifferentVersion = new Course(courseId, 2, true);
        Course courseWithDifferentPublishedState = new Course(courseId, 2, false);
        Course courseWithDifferentCourseId = new Course(UUID.randomUUID(), 1, true);

        assertTrue(course.equals(sameCourse));
        assertFalse(course.equals(null));
        assertFalse(course.equals(courseWithDifferentCourseId));
        assertFalse(course.equals(courseWithDifferentVersion));
        assertFalse(course.equals(courseWithDifferentPublishedState));
    }

    @Test
    public void shouldTestThatEqualCoursesHaveSameHashCodes() {
        UUID courseId = UUID.randomUUID();
        Course course = new Course(courseId, 1, true);
        Course sameCourse = new Course(courseId, 1, true);
        assertThat(course.hashCode(), Is.is(sameCourse.hashCode()));
    }
}
