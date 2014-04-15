package org.motechproject.whp.mtraining.csv.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.whp.mtraining.csv.domain.CsvImportError;
import org.motechproject.whp.mtraining.csv.request.CourseCsvRequest;

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
    @Mock
    private CourseService courseService;

    private CourseStructureValidator courseStructureValidator;
    private List<CourseCsvRequest> courseStructureCsvs;
    private List<CsvImportError> errors;

    @Before
    public void setUp() throws Exception {
        courseStructureValidator = new CourseStructureValidator(courseService);
        courseStructureCsvs = new ArrayList<>();
        courseStructureCsvs.add(new CourseCsvRequest("Basic TB Symptoms", "Course", "Active", null, "Message Description", null));
        courseStructureCsvs.add(new CourseCsvRequest("Module TB Symptoms", "Module", "Active", "Basic TB Symptoms", "Message Description", null));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms", "Chapter", "Active", "Module TB Symptoms", "Message Description", "fileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Message TB Symptoms", "Message", "Active", "Chapter TB Symptoms", "Message Description", "FileName"));

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
        courseStructureCsvs.add(new CourseCsvRequest("Basic Malaria Symptoms", "Course", "Active", null, "Message Description", null));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertThat(errors.size(), is(1));
        assertEquals("There are multiple course nodes in the CSV. Please ensure there is only 1 course node in the CSV and try importing again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorWhenNodeWithSameNameExists() {
        courseStructureCsvs.add(new CourseCsvRequest("Message TB Symptoms", "Message", "Active", "Chapter TB Symptoms", "Message Description", "FileName"));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(2, errors.size());
        assertEquals("There are 2 or more nodes with the same name: Message TB Symptoms. Please ensure the nodes are named differently and try importing again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorWhenNodeDoesNotHaveName() {
        courseStructureCsvs.add(new CourseCsvRequest("", "Message", "Active", "Chapter TB Symptoms", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest(null, "Message", "Active", "Chapter TB Symptoms", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("   ", "Message", "Active", "Chapter TB Symptoms", "Message Description", "FileName"));

        List<CsvImportError> errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(3, errors.size());
        assertEquals("Name not specified. Please specify the node name and try importing again.", errors.get(0).getMessage());
        assertEquals("Name not specified. Please specify the node name and try importing again.", errors.get(1).getMessage());
        assertEquals("Name not specified. Please specify the node name and try importing again.", errors.get(2).getMessage());
    }

    @Test
    public void shouldReturnErrorWhenParentNodeIsAbsent() {
        courseStructureCsvs.add(new CourseCsvRequest("Message TB Symptoms New", "Message", "Active", null, "Message Description", null));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(1, errors.size());
        assertEquals("All nodes other than course should have a parent node. Please ensure a parent node is specified and try importing again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorWhenParentHasInvalidName() {
        courseStructureCsvs.add(new CourseCsvRequest("New TB Symptoms", "Message", "Active", "Module TB Symptoms Invalid", "Message Description", "FileName"));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(1, errors.size());
        assertEquals("Could not find the parent node specified in the CSV. Please check the parent node name for spelling and try importing again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorWhenParentIsOfInvalidType() {
        courseStructureCsvs.add(new CourseCsvRequest("New TB Symptoms", "Message", "Active", "Basic TB Symptoms", "Message Description", "FileName"));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(1, errors.size());
        assertEquals("The parent node specified is of not of valid type. Please check the parent node name and try importing again.", errors.get(0).getMessage());
    }


    @Test
    public void shouldAddErrorWhenMessageDoesNotHaveFileName() {
        courseStructureCsvs.add(new CourseCsvRequest("Message TB Symptoms Version 2", "Message", "Active", "Chapter TB Symptoms", "Message Description", null));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(1, errors.size());
        assertEquals("A message and question should have the name of the audio file. Please add the filename to CSV and try importing it again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldAddErrorWhenNonMessageNodeDoesNotHaveChild() throws Exception {
        courseStructureCsvs.add(new CourseCsvRequest("Module TB Symptoms2", "Module", "Active", "Basic TB Symptoms", "Message Description", null));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms2", "Chapter", "Active", "Module TB Symptoms2", "Message Description", null));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(1, errors.size());
        assertEquals("A chapter should have at least one message under it. Please check if the parent node name is correctly specified for modules in the CSV and try importing it again.", errors.get(0).getMessage());
    }


    @Test
    public void shouldReturnErrorForChildStateIfParentIsInvalid() throws Exception {
        courseStructureCsvs.add(new CourseCsvRequest("Module TB Symptoms2", "Module", "Active", "Basic TB Symptoms1-parent", "Message Description", null));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms Version 2", "Chapter", "Active", "Module TB Symptoms2", "description Module TB Symptoms2", null));
        courseStructureCsvs.add(new CourseCsvRequest("Message TB Symptoms Version 2", "Message", "Active", "Chapter TB Symptoms Version 2", "Message Description", ""));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(2, errors.size());
        assertEquals("Could not find the parent node specified in the CSV. Please check the parent node name for spelling and try importing again.", errors.get(0).getMessage());
        assertEquals("A message and question should have the name of the audio file. Please add the filename to CSV and try importing it again.", errors.get(1).getMessage());
    }

    @Test
    public void shouldReturnErrorForNodeHavingInvalidStatus() {
        courseStructureCsvs.add(new CourseCsvRequest("Message TB Symptoms 1", "Message", "status_invalid", "Chapter TB Symptoms", "Message Description", "FileName"));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(1, errors.size());
    }

    @Test
    public void shouldValidateIfCourseNameIsSameAsExistingCourse() {
        when(courseService.getAllCourses()).thenReturn(asList(new CourseDto(true, "Different Course Name", "description", "filename", "Author", Collections.<ModuleDto>emptyList())));

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
        when(courseService.getAllCourses()).thenReturn(asList(new CourseDto(true, "Basic TB Symptoms", "description", "filename", "Author", Collections.<ModuleDto>emptyList())));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertTrue(errors.isEmpty());
    }

    @Test
    public void shouldReturnErrorIfTheCorrectAnswerFileIsNotAvailable() throws Exception {
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms1", "Chapter", "Active", "Module TB Symptoms", "Message Description", "fileName", null, null, null, "1", "50"));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms", "Question", "Active", "Chapter TB Symptoms1", "Message Description", "FileName", "1;2", "1", "", null, null));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(1, errors.size());
        assertEquals("A Question should have the name of the correct answer audio file. Please add the filename to CSV and try importing it again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorIfOptionsAreNotAvailable() throws Exception {
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms1", "Chapter", "Active", "Module TB Symptoms", "Message Description", "fileName", null, null, null, "1", "50"));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms", "Question", "Active", "Chapter TB Symptoms1", "Message Description", "FileName", "", "1", "CorrectAnswer", "", ""));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(1, errors.size());
        assertEquals("A Question should contain options and correct answer should be one among the options. Please verify and try importing it again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorIfTheCorrectAnswerIsNotOneAmongTheOptions() throws Exception {
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms1", "Chapter", "Active", "Module TB Symptoms", "Message Description", "fileName", null, null, null, "1", "50"));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms", "Question", "Active", "Chapter TB Symptoms1", "Message Description", "FileName", "1;2", "3", "CorrectAnswer", "", ""));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(1, errors.size());
        assertEquals("A Question should contain options and correct answer should be one among the options. Please verify and try importing it again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorIfNoOfQuestionsIsLessThanTheRequiredNoOfQuestionsToBeAnswered() {
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms1", "Chapter", "inactive", "Module TB Symptoms", "Message Description", null, null, null, null, "2", "60"));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms1", "Question", "Active", "Chapter TB Symptoms1", "Message Description", "FileName", "1;2", "1", "CorrectAnswer", null, null));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(1, errors.size());
        assertEquals("Number of questions available in the CSV for this chapter is less than number of quiz questions specified for the chapter. Please add more questions for the chapter and try importing again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorIfNoOfQuestionsIsGreaterThanZeroAndPassPercentageIsNotValid() {
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms1", "Chapter", "Active", "Module TB Symptoms", "Message Description", null, null, null, null, "2", "200"));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms1", "Question", "Active", "Chapter TB Symptoms1", "Message Description", "FileName", "1;2", "1", "CorrectAnswer", null, null));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(1, errors.size());
        assertEquals("Specify the pass percentage between 0 and 100 for the chapter's quiz and try importing again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldThrowNumberFormatExceptionWhenNoOfQuestionsIsNonNumeric() {
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms1", "Chapter", "Active", "Module TB Symptoms", "Message Description", null, null, null, null, "*", ""));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms1", "Question", "Active", "Chapter TB Symptoms1", "Message Description", "FileName", "1;2", "1", "CorrectAnswer", null, null));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(1, errors.size());
        assertEquals("A Chapter should have valid no of questions and pass percentage between 0 and 100. Please try importing it again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldThrowErrorWhenChapterDoesNotSpecifyQuestionsButQuestionsForTheChapterAreAvailable() {
        List<CourseCsvRequest> courseStructureCsvs = new ArrayList<>();
        courseStructureCsvs.add(new CourseCsvRequest("Basic TB Symptoms", "Course", "Active", null, "Message Description", null));
        courseStructureCsvs.add(new CourseCsvRequest("Module TB Symptoms", "Module", "Active", "Basic TB Symptoms", "Message Description", null));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms", "Chapter", "Active", "Module TB Symptoms", "Message Description", "fileName", null, null, null, "", "50"));
        courseStructureCsvs.add(new CourseCsvRequest("Message TB Symptoms", "Message", "Active", "Chapter TB Symptoms", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms", "Question", "Active", "Chapter TB Symptoms", "Message Description", "FileName", "1;2", "1", "CorrectAnswer", "", ""));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(1, errors.size());
        assertEquals("The chapter has questions in the CSV but number of questions to be played in the quiz is not specified. Please specify the number of questions for the chapter and try again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorIfActiveCourseHasNoActiveChild() {
        List<CourseCsvRequest> courseStructureCsvs = new ArrayList<>();
        courseStructureCsvs.add(new CourseCsvRequest("Basic TB Symptoms", "Course", "Active", null, "Message Description", null));
        courseStructureCsvs.add(new CourseCsvRequest("Module TB Symptoms", "Module", "inactive", "Basic TB Symptoms", "Message Description", null));
        courseStructureCsvs.add(new CourseCsvRequest("Module TB Symptoms2", "Module", "inactive", "Basic TB Symptoms", "Message Description", null));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms", "Chapter", "Active", "Module TB Symptoms", "Message Description", "fileName", null, null, null, "1", "1"));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms2", "Chapter", "Active", "Module TB Symptoms2", "Message Description", "fileName", null, null, null, "0", "0"));
        courseStructureCsvs.add(new CourseCsvRequest("Message TB Symptoms", "Message", "Active", "Chapter TB Symptoms", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Message TB Symptoms2", "Message", "Active", "Chapter TB Symptoms2", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms", "Question", "Active", "Chapter TB Symptoms", "Message Description", "FileName", "1;2", "1", "CorrectAnswer", "", ""));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(1, errors.size());
        assertEquals("A course should have at least one active module under it. Please add active modules under the course or mark the course as inactive and try importing it again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldNotReturnErrorIfInActiveCourseHasHasNoActiveChild() {
        List<CourseCsvRequest> courseStructureCsvs = new ArrayList<>();
        courseStructureCsvs.add(new CourseCsvRequest("Basic TB Symptoms", "Course", "inActive", null, "Message Description", null));
        courseStructureCsvs.add(new CourseCsvRequest("Module TB Symptoms", "Module", "inactive", "Basic TB Symptoms", "Message Description", null));
        courseStructureCsvs.add(new CourseCsvRequest("Module TB Symptoms2", "Module", "inactive", "Basic TB Symptoms", "Message Description", null));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms", "Chapter", "Active", "Module TB Symptoms", "Message Description", "fileName", null, null, null, "1", "1"));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms2", "Chapter", "Active", "Module TB Symptoms2", "Message Description", "fileName", null, null, null, "0", "0"));
        courseStructureCsvs.add(new CourseCsvRequest("Message TB Symptoms", "Message", "Active", "Chapter TB Symptoms", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Message TB Symptoms2", "Message", "Active", "Chapter TB Symptoms2", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms", "Question", "Active", "Chapter TB Symptoms", "Message Description", "FileName", "1;2", "1", "CorrectAnswer", "", ""));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(0, errors.size());
    }


    @Test
    public void shouldReturnErrorIfActiveModuleHasHasNoActiveChild() {
        List<CourseCsvRequest> courseStructureCsvs = new ArrayList<>();
        courseStructureCsvs.add(new CourseCsvRequest("Basic TB Symptoms", "Course", "Active", null, "Message Description", null));
        courseStructureCsvs.add(new CourseCsvRequest("Module TB Symptoms", "Module", "active", "Basic TB Symptoms", "Message Description", null));
        courseStructureCsvs.add(new CourseCsvRequest("Module TB Symptoms2", "Module", "inactive", "Basic TB Symptoms", "Message Description", null));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms", "Chapter", "inactive", "Module TB Symptoms", "Message Description", "fileName", null, null, null, "1", "1"));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms2", "Chapter", "inactive", "Module TB Symptoms2", "Message Description", "fileName", null, null, null, "0", "0"));
        courseStructureCsvs.add(new CourseCsvRequest("Message TB Symptoms", "Message", "Active", "Chapter TB Symptoms", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Message TB Symptoms2", "Message", "Active", "Chapter TB Symptoms2", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms", "Question", "Active", "Chapter TB Symptoms", "Message Description", "FileName", "1;2", "1", "CorrectAnswer", "", ""));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(1, errors.size());
        assertEquals("A module should have at least one active chapter under it. Please add active chapters under the module or mark the module as inactive and try importing it again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorIfActiveChapterHasHasNoActiveChild() {
        List<CourseCsvRequest> courseStructureCsvs = new ArrayList<>();
        courseStructureCsvs.add(new CourseCsvRequest("Basic TB Symptoms", "Course", "Active", null, "Message Description", null));
        courseStructureCsvs.add(new CourseCsvRequest("Module TB Symptoms", "Module", "active", "Basic TB Symptoms", "Message Description", null));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms", "Chapter", "Active", "Module TB Symptoms", "Message Description", "fileName", null, null, null, "0", "1"));
        courseStructureCsvs.add(new CourseCsvRequest("Message TB Symptoms", "Message", "inactive", "Chapter TB Symptoms", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Message TB Symptoms2", "Message", "inactive", "Chapter TB Symptoms", "Message Description", "FileName"));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(1, errors.size());
        assertEquals("A chapter should have at least one active message under it. Please add active messages under the chapter or mark the chapter as inactive and try importing it again.", errors.get(0).getMessage());
    }


    @Test
    public void shouldReturnErrorIfNoOfActiveQuestionsIsLessThanTheRequiredNoOfQuestionsToBeAnsweredForActiveChapter() {
        List<CourseCsvRequest> courseStructureCsvs = new ArrayList<>();
        courseStructureCsvs.add(new CourseCsvRequest("Basic TB Symptoms", "Course", "Active", null, "Message Description", null));
        courseStructureCsvs.add(new CourseCsvRequest("Module TB Symptoms", "Module", "active", "Basic TB Symptoms", "Message Description", null));
        courseStructureCsvs.add(new CourseCsvRequest("Module TB Symptoms2", "Module", "inactive", "Basic TB Symptoms", "Message Description", null));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms", "Chapter", "active", "Module TB Symptoms", "Message Description", "fileName", null, null, null, "2", "1"));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms2", "Chapter", "inactive", "Module TB Symptoms2", "Message Description", "fileName", null, null, null, "0", "0"));
        courseStructureCsvs.add(new CourseCsvRequest("Message TB Symptoms", "Message", "Active", "Chapter TB Symptoms", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Message TB Symptoms2", "Message", "Active", "Chapter TB Symptoms2", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms", "Question", "active", "Chapter TB Symptoms", "Message Description", "FileName", "1", "1", "CorrectAnswer", "", ""));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms2", "Question", "inactive", "Chapter TB Symptoms", "Message Description", "FileName", "1", "1", "CorrectAnswer", "", ""));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(1, errors.size());
        assertEquals("Number of active questions available in the CSV for this active chapter is less than number of quiz questions specified for the chapter. Please add more active questions for the chapter or  and try importing again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldNotReturnErrorIfNoOfActiveQuestionsIsLessThanTheRequiredNoOfQuestionsToBeAnsweredForInActiveChapter() {
        List<CourseCsvRequest> courseStructureCsvs = new ArrayList<>();
        courseStructureCsvs.add(new CourseCsvRequest("Basic TB Symptoms", "Course", "Active", null, "Message Description", null));
        courseStructureCsvs.add(new CourseCsvRequest("Module TB Symptoms", "Module", "active", "Basic TB Symptoms", "Message Description", null));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms", "Chapter", "inactive", "Module TB Symptoms", "Message Description", "fileName", null, null, null, "2", "1"));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms2", "Chapter", "active", "Module TB Symptoms", "Message Description", "fileName", null, null, null, "0", "0"));
        courseStructureCsvs.add(new CourseCsvRequest("Message TB Symptoms", "Message", "Active", "Chapter TB Symptoms", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Message TB Symptoms2", "Message", "Active", "Chapter TB Symptoms2", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms", "Question", "active", "Chapter TB Symptoms", "Message Description", "FileName", "1", "1", "CorrectAnswer", "", ""));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms2", "Question", "inactive", "Chapter TB Symptoms", "Message Description", "FileName", "1", "1", "CorrectAnswer", "", ""));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(0, errors.size());
    }
}
