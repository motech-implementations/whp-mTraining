package org.motechproject.whp.mtraining.csv.request;

import org.hamcrest.core.Is;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class CourseStructureCsvRequestTest {

    @Test
    public void testThatNodeIsACourse() throws Exception {
        CourseStructureCsvRequest course = new CourseStructureCsvRequest("Basic TB Symptoms", "Course", "Active", null, "Message Description", null);
        assertThat(course.isCourse(), Is.is(true));

        CourseStructureCsvRequest module = new CourseStructureCsvRequest("Basic TB Symptoms", "Module", "Active", null, "Message Description", null);
        assertThat(module.isCourse(), Is.is(false));

        CourseStructureCsvRequest invalidNode = new CourseStructureCsvRequest("Basic TB Symptoms", null, "Active", null, "Message Description", null);
        assertThat(invalidNode.isCourse(), Is.is(false));
    }

    @Test
    public void testThatNodeIsMessage() throws Exception {
        CourseStructureCsvRequest message = new CourseStructureCsvRequest("Basic TB Symptoms", "Message", "Active", "parentNode", "Message Description", null);
        assertThat(message.isMessage(), Is.is(true));

        CourseStructureCsvRequest course = new CourseStructureCsvRequest("Basic TB Symptoms", "Course", "Active", null, "Message Description", null);
        assertThat(course.isMessage(), Is.is(false));

        CourseStructureCsvRequest invalidNode = new CourseStructureCsvRequest("Basic TB Symptoms", null, "Active", null, "Message Description", null);
        assertThat(invalidNode.isMessage(), Is.is(false));

    }

    @Test
    public void shouldIndicateIfMessageHasAFileName() throws Exception {
        CourseStructureCsvRequest messageWithFile = new CourseStructureCsvRequest("Basic TB Symptoms", "Message", "Active", "parentNode", "Message Description", "some_file.wav");
        assertThat(messageWithFile.hasFileName(), Is.is(true));

        CourseStructureCsvRequest messageWithoutFile = new CourseStructureCsvRequest("Basic TB Symptoms", "Message", "Active", "parentNode", "Message Description", null);
        assertThat(messageWithoutFile.hasFileName(), Is.is(false));

    }

    @Test
    public void testThatNodeHasAParentNode() throws Exception {
        CourseStructureCsvRequest course = new CourseStructureCsvRequest("Basic TB Symptoms", "Course", "Active", "parentNode", "Message Description", null);
        assertThat(course.hasParent(), Is.is(true));
    }
}
