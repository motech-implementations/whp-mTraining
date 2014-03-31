package org.motechproject.whp.mtraining.csv.request;

import org.hamcrest.core.Is;
import org.junit.Test;

import static org.junit.Assert.*;

public class CourseCsvRequestTest {

    @Test
    public void testThatNodeIsACourse() throws Exception {
        CourseCsvRequest course = new CourseCsvRequest("Basic TB Symptoms", "Course", "Active", null, "Message Description", null);
        assertThat(course.isCourse(), Is.is(true));

        CourseCsvRequest module = new CourseCsvRequest("Basic TB Symptoms", "Module", "Active", null, "Message Description", null);
        assertThat(module.isCourse(), Is.is(false));

        CourseCsvRequest invalidNode = new CourseCsvRequest("Basic TB Symptoms", null, "Active", null, "Message Description", null);
        assertThat(invalidNode.isCourse(), Is.is(false));
    }

    @Test
    public void testThatNodeIsMessage() throws Exception {
        CourseCsvRequest message = new CourseCsvRequest("Basic TB Symptoms", "Message", "Active", "parentNode", "Message Description", null);
        assertThat(message.isMessage(), Is.is(true));

        CourseCsvRequest course = new CourseCsvRequest("Basic TB Symptoms", "Course", "Active", null, "Message Description", null);
        assertThat(course.isMessage(), Is.is(false));

        CourseCsvRequest invalidNode = new CourseCsvRequest("Basic TB Symptoms", null, "Active", null, "Message Description", null);
        assertThat(invalidNode.isMessage(), Is.is(false));

    }

    @Test
    public void shouldIndicateIfMessageHasAFileName() throws Exception {
        CourseCsvRequest messageWithFile = new CourseCsvRequest("Basic TB Symptoms", "Message", "Active", "parentNode", "Message Description", "some_file.wav");
        assertThat(messageWithFile.hasFileName(), Is.is(true));

        CourseCsvRequest messageWithoutFile = new CourseCsvRequest("Basic TB Symptoms", "Message", "Active", "parentNode", "Message Description", null);
        assertThat(messageWithoutFile.hasFileName(), Is.is(false));

    }

    @Test
    public void testThatNodeHasAParentNode() throws Exception {
        CourseCsvRequest course = new CourseCsvRequest("Basic TB Symptoms", "Course", "Active", "parentNode", "Message Description", null);
        assertThat(course.hasParent(), Is.is(true));
    }

    @Test
    public void testThatNodeHasValidStatus() {
        CourseCsvRequest courseWithActiveStatus = new CourseCsvRequest("Basic TB Symptoms", "Course", "Active", "parentNode", "Message Description", null);
        assertTrue(courseWithActiveStatus.isValidStatus());

        CourseCsvRequest courseWithInActiveStatus = new CourseCsvRequest("Basic TB Symptoms", "Course", "inActive", "parentNode", "Message Description", null);
        assertTrue(courseWithInActiveStatus.isValidStatus());

        CourseCsvRequest courseWithNullStatus = new CourseCsvRequest("Basic TB Symptoms", "Course", null, "parentNode", "Message Description", null);
        assertTrue(courseWithNullStatus.isValidStatus());

        CourseCsvRequest courseWithBlankStatus = new CourseCsvRequest("Basic TB Symptoms", "Course", "  ", "parentNode", "Message Description", null);
        assertTrue(courseWithBlankStatus.isValidStatus());

        CourseCsvRequest courseWithWrongStatus = new CourseCsvRequest("Basic TB Symptoms", "Course", "status", "parentNode", "Message Description", null);
        assertFalse(courseWithWrongStatus.isValidStatus());
    }

    @Test
    public void testThatNodeHasInactiveStatus(){
        CourseCsvRequest courseWithActiveStatus = new CourseCsvRequest("Basic TB Symptoms", "Course", "Active", "parentNode", "Message Description", null);
        assertFalse(courseWithActiveStatus.isInActive());

        CourseCsvRequest courseWithInActiveStatus = new CourseCsvRequest("Basic TB Symptoms", "Course", "inActive", "parentNode", "Message Description", null);
        assertTrue(courseWithInActiveStatus.isInActive());
    }

}
