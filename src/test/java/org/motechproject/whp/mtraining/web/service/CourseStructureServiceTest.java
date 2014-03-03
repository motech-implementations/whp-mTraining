package org.motechproject.whp.mtraining.web.service;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.whp.mtraining.web.model.ErrorModel;
import org.motechproject.whp.mtraining.web.request.CourseStructureCsvRequest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class CourseStructureServiceTest {
    CourseStructureService courseStructureService;
    List<CourseStructureCsvRequest> courseStructureCsvs;
    List<ErrorModel> errors;

    @Before
    public void setUp() throws Exception {
        courseStructureService = new CourseStructureService();
        courseStructureCsvs = new ArrayList<>();
        courseStructureCsvs.add(new CourseStructureCsvRequest("Basic TB Symptoms", "Course", "Active", null, "Message Description", null));
        courseStructureCsvs.add(new CourseStructureCsvRequest("Module TB Symptoms", "Module", "Active", "Basic TB Symptoms", "Message Description", null));
        courseStructureCsvs.add(new CourseStructureCsvRequest("Chapter TB Symptoms", "Chapter", "Active", "Module TB Symptoms", "Message Description", null));
        courseStructureCsvs.add(new CourseStructureCsvRequest("Message TB Symptoms", "Message", "Active", "Chapter TB Symptoms", "Message Description", "FileName"));
    }

    @Test
    public void shouldHaveNoErrorForValidStructure() {
        errors = courseStructureService.parseToCourseStructure(courseStructureCsvs);
        assertThat(errors.size(), is(0));

    }
    @Test
    public void shouldHaveErrorWhenNoCourseIsProvided() {
        courseStructureCsvs.remove(0);
        errors = courseStructureService.parseToCourseStructure(courseStructureCsvs);
        assertThat(errors.size(), is(1));
        assertEquals(errors.get(0).getMessage(),"Could not find the course name in the CSV. Please add the course details to CSV and try importing again.");
    }

    @Test
    public void shouldReturnErrorIfCSVHasMoreThanOneCourse() {
        courseStructureCsvs.add(new CourseStructureCsvRequest("Basic Malaria Symptoms", "Course", "Active", null, "Message Description", null));
        errors = courseStructureService.parseToCourseStructure(courseStructureCsvs);
        assertThat(errors.size(), is(1));
        assertEquals("There are multiple course nodes in the CSV. Please ensure there is only course node in the CSV and try importing again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorWhenNodeWithSameNameExists() {
        courseStructureCsvs.add(new CourseStructureCsvRequest("Message TB Symptoms", "Message", "Active", "Chapter TB Symptoms", "Message Description", "FileName"));
        errors = courseStructureService.parseToCourseStructure(courseStructureCsvs);
        assertEquals(1, errors.size());
        assertEquals("There are 2 or more nodes with the same name: Message TB Symptoms. Please ensure the nodes are named differently and try importing again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorWhenNodeDoesNotHaveName() {
        courseStructureCsvs.add(new CourseStructureCsvRequest("", "Message", "Active", "Chapter TB Symptoms", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseStructureCsvRequest(null, "Message", "Active", "Chapter TB Symptoms", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseStructureCsvRequest("   ", "Message", "Active", "Chapter TB Symptoms", "Message Description", "FileName"));

        List<ErrorModel> errors = courseStructureService.parseToCourseStructure(courseStructureCsvs);

        assertEquals(3, errors.size());
        assertEquals("Name not specified.", errors.get(0).getMessage());
        assertEquals("Name not specified.", errors.get(1).getMessage());
        assertEquals("Name not specified.", errors.get(2).getMessage());
    }

    @Test
    public void shouldReturnErrorWhenParentNodeIsAbsent() {
        courseStructureCsvs.add(new CourseStructureCsvRequest("Message TB Symptoms New", "Message", "Active", null, "Message Description", null));
        errors = courseStructureService.parseToCourseStructure(courseStructureCsvs);
        assertEquals(1, errors.size());
        assertEquals("All nodes other than course should have a parent node. Please ensure a parent node is specified and try importing again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorWhenParentHasInvalidName() {
        courseStructureCsvs.add(new CourseStructureCsvRequest("New TB Symptoms", "Message", "Active", "Module TB Symptoms Invalid", "Message Description", "FileName"));
        errors = courseStructureService.parseToCourseStructure(courseStructureCsvs);
        assertEquals(1, errors.size());
        assertEquals("Could not find the parent node specified in the CSV. Please check the parent node name for spelling and try importing again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorWhenParentIsOfInvalidType() {
        courseStructureCsvs.add(new CourseStructureCsvRequest("New TB Symptoms", "Message", "Active", "Basic TB Symptoms", "Message Description", "FileName"));
        errors = courseStructureService.parseToCourseStructure(courseStructureCsvs);
        assertEquals(1, errors.size());
        assertEquals("The parent node specified is of not of valid type. Please check the parent node name and try importing again.", errors.get(0).getMessage());
    }


    @Test
    public void shouldAddErrorWhenMessageDoesNotHaveFileName() {
        courseStructureCsvs.add(new CourseStructureCsvRequest("Message TB Symptoms Version 2", "Message", "Active", "Chapter TB Symptoms", "Message Description", null));
        errors = courseStructureService.parseToCourseStructure(courseStructureCsvs);
        assertEquals(1, errors.size());
        assertEquals("A message should have the name of the audio file. Please add the filename to CSV and try importing it again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldAddErrorWhenNonMessageNodeDoesNotHaveChild() throws Exception {
        courseStructureCsvs.add(new CourseStructureCsvRequest("Module TB Symptoms2", "Module", "Active", "Basic TB Symptoms", "Message Description", null));
        courseStructureCsvs.add(new CourseStructureCsvRequest("Chapter TB Symptoms2", "Chapter", "Active", "Module TB Symptoms2", "Message Description", null));
        errors = courseStructureService.parseToCourseStructure(courseStructureCsvs);
        assertEquals(1, errors.size());
        assertEquals("A chapter should have at least one message under it. Please check if the parent node name is correctly specified for modules in the CSV and try importing it again.", errors.get(0).getMessage());
    }


    @Test
    public void shouldReturnErrorForChildStateIfParentIsInvalid() throws Exception {
        courseStructureCsvs.add(new CourseStructureCsvRequest("Module TB Symptoms2", "Module", "Active", "Basic TB Symptoms1-parent", "Message Description", null));
        courseStructureCsvs.add(new CourseStructureCsvRequest("Chapter TB Symptoms Version 2", "Chapter", "Active", "Module TB Symptoms2", "description Module TB Symptoms2", null));
        courseStructureCsvs.add(new CourseStructureCsvRequest("Message TB Symptoms Version 2", "Message", "Active", "Chapter TB Symptoms Version 2", "Message Description", ""));
        errors = courseStructureService.parseToCourseStructure(courseStructureCsvs);
        assertEquals(2, errors.size());
        assertEquals("Could not find the parent node specified in the CSV. Please check the parent node name for spelling and try importing again.", errors.get(0).getMessage());
        assertEquals("A message should have the name of the audio file. Please add the filename to CSV and try importing it again.", errors.get(1).getMessage());
    }
}

