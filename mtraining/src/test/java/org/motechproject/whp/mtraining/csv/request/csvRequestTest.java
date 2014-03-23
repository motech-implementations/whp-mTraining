package org.motechproject.whp.mtraining.csv.request;

import org.hamcrest.core.Is;
import org.junit.Test;

import static org.junit.Assert.*;

public class csvRequestTest {

    @Test
    public void testThatNodeIsACourse() throws Exception {
        CsvRequest course = new CsvRequest("Basic TB Symptoms", "Course", "Active", null, "Message Description", null);
        assertThat(course.isCourse(), Is.is(true));

        CsvRequest module = new CsvRequest("Basic TB Symptoms", "Module", "Active", null, "Message Description", null);
        assertThat(module.isCourse(), Is.is(false));

        CsvRequest invalidNode = new CsvRequest("Basic TB Symptoms", null, "Active", null, "Message Description", null);
        assertThat(invalidNode.isCourse(), Is.is(false));
    }

    @Test
    public void testThatNodeIsMessage() throws Exception {
        CsvRequest message = new CsvRequest("Basic TB Symptoms", "Message", "Active", "parentNode", "Message Description", null);
        assertThat(message.isMessage(), Is.is(true));

        CsvRequest course = new CsvRequest("Basic TB Symptoms", "Course", "Active", null, "Message Description", null);
        assertThat(course.isMessage(), Is.is(false));

        CsvRequest invalidNode = new CsvRequest("Basic TB Symptoms", null, "Active", null, "Message Description", null);
        assertThat(invalidNode.isMessage(), Is.is(false));

    }

    @Test
    public void shouldIndicateIfMessageHasAFileName() throws Exception {
        CsvRequest messageWithFile = new CsvRequest("Basic TB Symptoms", "Message", "Active", "parentNode", "Message Description", "some_file.wav");
        assertThat(messageWithFile.hasFileName(), Is.is(true));

        CsvRequest messageWithoutFile = new CsvRequest("Basic TB Symptoms", "Message", "Active", "parentNode", "Message Description", null);
        assertThat(messageWithoutFile.hasFileName(), Is.is(false));

    }

    @Test
    public void testThatNodeHasAParentNode() throws Exception {
        CsvRequest course = new CsvRequest("Basic TB Symptoms", "Course", "Active", "parentNode", "Message Description", null);
        assertThat(course.hasParent(), Is.is(true));
    }

    @Test
    public void testThatNodeHasValidStatus() {
        CsvRequest courseWithActiveStatus = new CsvRequest("Basic TB Symptoms", "Course", "Active", "parentNode", "Message Description", null);
        assertTrue(courseWithActiveStatus.isValidStatus());

        CsvRequest courseWithInActiveStatus = new CsvRequest("Basic TB Symptoms", "Course", "inActive", "parentNode", "Message Description", null);
        assertTrue(courseWithInActiveStatus.isValidStatus());

        CsvRequest courseWithNullStatus = new CsvRequest("Basic TB Symptoms", "Course", null, "parentNode", "Message Description", null);
        assertTrue(courseWithNullStatus.isValidStatus());

        CsvRequest courseWithBlankStatus = new CsvRequest("Basic TB Symptoms", "Course", "  ", "parentNode", "Message Description", null);
        assertTrue(courseWithBlankStatus.isValidStatus());

        CsvRequest courseWithWrongStatus = new CsvRequest("Basic TB Symptoms", "Course", "status", "parentNode", "Message Description", null);
        assertFalse(courseWithWrongStatus.isValidStatus());
    }
}
