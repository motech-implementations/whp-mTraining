package org.motechproject.whp.mtraining.csv.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.whp.mtraining.csv.request.CourseStructureCsvRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CourseStructureValidatorTest {
    private List<CourseStructureCsvRequest> courseStructureCsvs;
    private List<CourseImportError> errors;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseStructureValidator courseStructureValidator = new CourseStructureValidator();

    @Before
    public void setUp() throws Exception {
        courseStructureCsvs = new ArrayList<>();
        courseStructureCsvs.add(new CourseStructureCsvRequest("Basic TB Symptoms", "Course", "Active", null, "Message Description", null));
        courseStructureCsvs.add(new CourseStructureCsvRequest("Module TB Symptoms", "Module", "Active", "Basic TB Symptoms", "Message Description", null));
        courseStructureCsvs.add(new CourseStructureCsvRequest("Chapter TB Symptoms", "Chapter", "Active", "Module TB Symptoms", "Message Description", null));
        courseStructureCsvs.add(new CourseStructureCsvRequest("Message TB Symptoms", "Message", "Active", "Chapter TB Symptoms", "Message Description", "FileName"));

        when(courseService.getAllCourses()).thenReturn(Collections.<CourseDto>emptyList());
    }

    @Test
    public void shouldHaveNoErrorForValidStructure() {
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertThat(errors.size(), is(0));

    }

    @Test
    public void shouldHaveErrorWhenNoCourseIsProvided() {
        courseStructureCsvs.remove(0);
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertThat(errors.size(), is(1));
        assertEquals(errors.get(0).getMessage(), "Could not find the course name in the CSV. Please add the course details to CSV and try importing again.");
    }

    @Test
    public void shouldReturnErrorIfCSVHasMoreThanOneCourse() {
        courseStructureCsvs.add(new CourseStructureCsvRequest("Basic Malaria Symptoms", "Course", "Active", null, "Message Description", null));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertThat(errors.size(), is(1));
        assertEquals("There are multiple course nodes in the CSV. Please ensure there is only 1 course node in the CSV and try importing again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorWhenNodeWithSameNameExists() {
        courseStructureCsvs.add(new CourseStructureCsvRequest("Message TB Symptoms", "Message", "Active", "Chapter TB Symptoms", "Message Description", "FileName"));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(2, errors.size());
        assertEquals("There are 2 or more nodes with the same name: Message TB Symptoms. Please ensure the nodes are named differently and try importing again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorWhenNodeDoesNotHaveName() {
        courseStructureCsvs.add(new CourseStructureCsvRequest("", "Message", "Active", "Chapter TB Symptoms", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseStructureCsvRequest(null, "Message", "Active", "Chapter TB Symptoms", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseStructureCsvRequest("   ", "Message", "Active", "Chapter TB Symptoms", "Message Description", "FileName"));

        List<CourseImportError> errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(3, errors.size());
        assertEquals("Name not specified. Please specify the node name and try importing again.", errors.get(0).getMessage());
        assertEquals("Name not specified. Please specify the node name and try importing again.", errors.get(1).getMessage());
        assertEquals("Name not specified. Please specify the node name and try importing again.", errors.get(2).getMessage());
    }

    @Test
    public void shouldReturnErrorWhenParentNodeIsAbsent() {
        courseStructureCsvs.add(new CourseStructureCsvRequest("Message TB Symptoms New", "Message", "Active", null, "Message Description", null));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(1, errors.size());
        assertEquals("All nodes other than course should have a parent node. Please ensure a parent node is specified and try importing again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorWhenParentHasInvalidName() {
        courseStructureCsvs.add(new CourseStructureCsvRequest("New TB Symptoms", "Message", "Active", "Module TB Symptoms Invalid", "Message Description", "FileName"));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(1, errors.size());
        assertEquals("Could not find the parent node specified in the CSV. Please check the parent node name for spelling and try importing again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorWhenParentIsOfInvalidType() {
        courseStructureCsvs.add(new CourseStructureCsvRequest("New TB Symptoms", "Message", "Active", "Basic TB Symptoms", "Message Description", "FileName"));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(1, errors.size());
        assertEquals("The parent node specified is of not of valid type. Please check the parent node name and try importing again.", errors.get(0).getMessage());
    }


    @Test
    public void shouldAddErrorWhenMessageDoesNotHaveFileName() {
        courseStructureCsvs.add(new CourseStructureCsvRequest("Message TB Symptoms Version 2", "Message", "Active", "Chapter TB Symptoms", "Message Description", null));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(1, errors.size());
        assertEquals("A message should have the name of the audio file. Please add the filename to CSV and try importing it again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldAddErrorWhenNonMessageNodeDoesNotHaveChild() throws Exception {
        courseStructureCsvs.add(new CourseStructureCsvRequest("Module TB Symptoms2", "Module", "Active", "Basic TB Symptoms", "Message Description", null));
        courseStructureCsvs.add(new CourseStructureCsvRequest("Chapter TB Symptoms2", "Chapter", "Active", "Module TB Symptoms2", "Message Description", null));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(1, errors.size());
        assertEquals("A chapter should have at least one message under it. Please check if the parent node name is correctly specified for modules in the CSV and try importing it again.", errors.get(0).getMessage());
    }


    @Test
    public void shouldReturnErrorForChildStateIfParentIsInvalid() throws Exception {
        courseStructureCsvs.add(new CourseStructureCsvRequest("Module TB Symptoms2", "Module", "Active", "Basic TB Symptoms1-parent", "Message Description", null));
        courseStructureCsvs.add(new CourseStructureCsvRequest("Chapter TB Symptoms Version 2", "Chapter", "Active", "Module TB Symptoms2", "description Module TB Symptoms2", null));
        courseStructureCsvs.add(new CourseStructureCsvRequest("Message TB Symptoms Version 2", "Message", "Active", "Chapter TB Symptoms Version 2", "Message Description", ""));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(2, errors.size());
        assertEquals("Could not find the parent node specified in the CSV. Please check the parent node name for spelling and try importing again.", errors.get(0).getMessage());
        assertEquals("A message should have the name of the audio file. Please add the filename to CSV and try importing it again.", errors.get(1).getMessage());
    }

    @Test
    public void shouldReturnErrorForNodeHavingInvalidStatus() {
        courseStructureCsvs.add(new CourseStructureCsvRequest("Message TB Symptoms 1", "Message", "status_invalid", "Chapter TB Symptoms", "Message Description", "FileName"));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(1, errors.size());
    }

    @Test
    public void shouldValidateIfCourseNameIsSameAsExistingCourse() {
        when(courseService.getAllCourses()).thenReturn(asList(new CourseDto(true, "Different Course Name", "description", Collections.<ModuleDto>emptyList())));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(1, errors.size());
        String errorMessage = errors.get(0).getMessage();
        assertEquals("Course: Different Course Name already exists in database. You cannot import a new course.", errorMessage);
    }

    @Test
    public void shouldNotValidateCourseNameIfNoCourseExistsAlready() {
        when(courseService.getAllCourses()).thenReturn(Collections.<CourseDto>emptyList());

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertTrue(errors.isEmpty());
    }

    @Test
    public void shouldNotReturnAnyErrorIfCourseNameIsSameAsExistingCourse() {
        when(courseService.getAllCourses()).thenReturn(asList(new CourseDto(true, "Basic TB Symptoms", "description", Collections.<ModuleDto>emptyList())));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertTrue(errors.isEmpty());
    }
}

