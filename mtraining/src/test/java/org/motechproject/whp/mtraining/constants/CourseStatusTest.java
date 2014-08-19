package org.motechproject.whp.mtraining.constants;

import org.hamcrest.core.Is;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class CourseStatusTest {

    @Test
    public void shouldReturnTheCorrectEnumValue() {
        assertThat(CourseStatus.enumFor("completed"), Is.is(CourseStatus.COMPLETED));
        assertThat(CourseStatus.enumFor("started"), Is.is(CourseStatus.STARTED));
        assertThat(CourseStatus.enumFor("closed"), Is.is(CourseStatus.CLOSED));
        assertThat(CourseStatus.enumFor("ongoing"), Is.is(CourseStatus.ONGOING));
        assertThat(CourseStatus.enumFor("COMPLETED"), Is.is(CourseStatus.COMPLETED));
        assertThat(CourseStatus.enumFor("some unknown value"), Is.is(CourseStatus.UNKNOWN));
        assertThat(CourseStatus.enumFor(null), Is.is(CourseStatus.UNKNOWN));
    }

}
