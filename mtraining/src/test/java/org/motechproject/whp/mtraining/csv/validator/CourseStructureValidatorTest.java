package org.motechproject.whp.mtraining.csv.validator;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.CourseUnitState;
import org.motechproject.mtraining.service.MTrainingService;
import org.motechproject.whp.mtraining.csv.domain.CsvImportError;
import org.motechproject.whp.mtraining.csv.request.CourseCsvRequest;
import org.motechproject.whp.mtraining.service.CoursePlanService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CourseStructureValidatorTest {

    @Mock
    private MTrainingService mTrainingService;

    @Mock
    private CoursePlanService coursePlanService;

    private CourseCsvStructureValidator courseStructureValidator;
    private List<CourseCsvRequest> courseStructureCsvs;
    private List<CsvImportError> errors;

    @Before
    public void setUp() throws Exception {
        courseStructureValidator = new CourseCsvStructureValidator(mTrainingService, coursePlanService);
        courseStructureCsvs = new ArrayList<>();
        courseStructureCsvs.add(new CourseCsvRequest("Course plan", "Course", CourseUnitState.Active, null, "Message Description", "filename"));
        courseStructureCsvs.add(new CourseCsvRequest("Basic TB Symptoms", "Module", CourseUnitState.Active, "Course plan", "Message Description", "filename"));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms", "Chapter", CourseUnitState.Active, "Basic TB Symptoms", "Message Description", "fileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Lesson TB Symptoms", "Lesson", CourseUnitState.Active, "Chapter TB Symptoms", "Message Description", "FileName"));

        when(mTrainingService.getCourseByName(anyString())).thenReturn(Collections.<Course>emptyList());
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
    public void shouldReturnErrorWhenNodeWithSameNameExists() {
        courseStructureCsvs.add(new CourseCsvRequest("Lesson TB Symptoms", "Lesson", CourseUnitState.Active, "Chapter TB Symptoms", "Message Description", "FileName"));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(2, errors.size());
        assertEquals("There are 2 or more nodes with the same name: Lesson TB Symptoms. Please ensure the nodes are named differently and try importing again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorWhenNodeDoesNotHaveName() {
        courseStructureCsvs.add(new CourseCsvRequest("", "Lesson", CourseUnitState.Active, "Chapter TB Symptoms", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest(null, "Lesson", CourseUnitState.Active, "Chapter TB Symptoms", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("   ", "Lesson", CourseUnitState.Active, "Chapter TB Symptoms", "Message Description", "FileName"));

        List<CsvImportError> errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(3, errors.size());
        assertEquals("Name not specified. Please specify the node name and try importing again.", errors.get(0).getMessage());
        assertEquals("Name not specified. Please specify the node name and try importing again.", errors.get(1).getMessage());
        assertEquals("Name not specified. Please specify the node name and try importing again.", errors.get(2).getMessage());
    }

    @Test
    public void shouldReturnErrorWhenParentNodeIsAbsent() {
        courseStructureCsvs.add(new CourseCsvRequest("Lesson TB Symptoms New", "Lesson", CourseUnitState.Active, null, "Message Description", "filename"));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(1, errors.size());
        assertEquals("All nodes other than course should have a parent node. Please ensure a parent node is specified and try importing again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorWhenParentHasInvalidName() {
        courseStructureCsvs.add(new CourseCsvRequest("New TB Symptoms", "Lesson", CourseUnitState.Active, "Module TB Symptoms Invalid", "Message Description", "FileName"));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(1, errors.size());
        assertEquals("Could not find the parent node specified in the CSV. Please check the parent node name for spelling and try importing again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorWhenParentIsOfInvalidType() {
        courseStructureCsvs.add(new CourseCsvRequest("New TB Symptoms", "Lesson", CourseUnitState.Active, "Basic TB Symptoms", "Message Description", "FileName"));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(1, errors.size());
        assertEquals("The parent node specified is of not of valid type. Please check the parent node name and try importing again.", errors.get(0).getMessage());
    }


    @Test
    public void shouldAddErrorWhenLessonDoesNotHaveFileName() {
        courseStructureCsvs.add(new CourseCsvRequest("Message TB Symptoms Version 2", "Lesson", CourseUnitState.Active, "Chapter TB Symptoms", "Message Description", null));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(1, errors.size());
        assertEquals("Missing audio filename. Please add the filename to CSV and try importing it again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldAddErrorWhenNonLessonNodeDoesNotHaveChild() throws Exception {
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms2", "Chapter", CourseUnitState.Active, "Basic TB Symptoms", "Message Description", "filename"));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(1, errors.size());
        assertEquals("A chapter should have at least one lesson under it. Please check if the parent node name is correctly specified for modules in the CSV and try importing it again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorForChildStateIfParentIsInvalid() throws Exception {
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms Version 2", "Chapter", CourseUnitState.Active, "Basic TB Symptoms1-parent", "description Module TB Symptoms2", "Filename"));
        courseStructureCsvs.add(new CourseCsvRequest("Lesson TB Symptoms Version 2", "Lesson", CourseUnitState.Active, "Chapter TB Symptoms Version 2", "Message Description", ""));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(2, errors.size());
        assertEquals("Could not find the parent node specified in the CSV. Please check the parent node name for spelling and try importing again.", errors.get(0).getMessage());
        assertEquals("Missing audio filename. Please add the filename to CSV and try importing it again.", errors.get(1).getMessage());
    }

    @Test
    public void shouldReturnErrorForNodeHavingInvalidStatus() {
        courseStructureCsvs.add(new CourseCsvRequest("Lesson TB Symptoms 1", "Lesson", null, "Chapter TB Symptoms", "Message Description", "FileName"));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        assertEquals(1, errors.size());
    }

    @Test
    public void shouldValidateIfCourseNameIsSameAsExistingCourse() {
        when(mTrainingService.getCourseByName(anyString())).thenReturn(asList(new Course("Different Module Name", CourseUnitState.Active, "description", Collections.<Chapter>emptyList())));
        errors = courseStructureValidator.validate(courseStructureCsvs);
        
        assertEquals(1, errors.size());
        String errorMessage = errors.get(0).getMessage();
        assertEquals("Module: Different Module Name already exists in database.", errorMessage);
    }

    @Test
    public void shouldNotValidateCourseNameIfNoCourseExistsAlready() {
        when(mTrainingService.getCourseByName(anyString())).thenReturn(Collections.<Course>emptyList());

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertTrue(errors.isEmpty());
    }

    @Ignore
    @Test
    public void shouldNotReturnAnyErrorIfCourseNameIsSameAsExistingCourse() {
        when(mTrainingService.getCourseByName(anyString())).thenReturn(asList(new Course("Basic TB Symptoms", CourseUnitState.Active, "description", Collections.<Chapter>emptyList())));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertTrue(errors.isEmpty());
    }

    @Test
    public void shouldReturnErrorIfTheCorrectAnswerFileIsNotAvailable() throws Exception {
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms1", "Chapter", CourseUnitState.Active, "Basic TB Symptoms", "Message Description", "fileName", null, null, null, "1", "50"));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms", "Question", CourseUnitState.Active, "Chapter TB Symptoms1", "Message Description", "FileName", "1;2", "1", "", null, null));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(1, errors.size());
        assertEquals("A Question should have the name of the correct answer audio file. Please add the filename to CSV and try importing it again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorIfOptionsAreNotAvailable() throws Exception {
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms1", "Chapter", CourseUnitState.Active, "Basic TB Symptoms", "Message Description", "fileName", null, null, null, "1", "50"));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms", "Question", CourseUnitState.Active, "Chapter TB Symptoms1", "Message Description", "FileName", "", "1", "CorrectAnswer", "", ""));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(1, errors.size());
        assertEquals("A Question should contain options and correct answer should be one among the options. Please verify and try importing it again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorIfTheCorrectAnswerIsNotOneAmongTheOptions() throws Exception {
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms1", "Chapter", CourseUnitState.Active, "Basic TB Symptoms", "Message Description", "fileName", null, null, null, "1", "50"));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms", "Question", CourseUnitState.Active, "Chapter TB Symptoms1", "Message Description", "FileName", "1;2", "3", "CorrectAnswer", "", ""));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(1, errors.size());
        assertEquals("A Question should contain options and correct answer should be one among the options. Please verify and try importing it again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorIfNoOfQuestionsIsLessThanTheRequiredNoOfQuestionsToBeAnswered() {
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms1", "Chapter", CourseUnitState.Inactive, "Basic TB Symptoms", "Message Description", "Filename", null, null, null, "2", "60"));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms1", "Question", CourseUnitState.Active, "Chapter TB Symptoms1", "Message Description", "FileName", "1;2", "1", "CorrectAnswer", null, null));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(1, errors.size());
        assertEquals("Number of questions available in the CSV for this chapter is less than number of quiz questions specified for the chapter. Please add more questions for the chapter and try importing again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorIfNoOfQuestionsIsGreaterThanZeroAndPassPercentageIsNotValid() {
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms1", "Chapter", CourseUnitState.Active, "Basic TB Symptoms", "Message Description", "Filename", null, null, null, "2", "200"));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms1", "Question", CourseUnitState.Active, "Chapter TB Symptoms1", "Message Description", "FileName", "1;2", "1", "CorrectAnswer", null, null));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(1, errors.size());
        assertEquals("Specify the pass percentage between 0 and 100 for the chapter's quiz and try importing again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldThrowNumberFormatExceptionWhenNoOfQuestionsIsNonNumeric() {
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms1", "Chapter", CourseUnitState.Active, "Basic TB Symptoms", "Message Description", "Filename", null, null, null, "*", ""));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms1", "Question", CourseUnitState.Active, "Chapter TB Symptoms1", "Message Description", "FileName", "1;2", "1", "CorrectAnswer", null, null));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(1, errors.size());
        assertEquals("A Chapter should have valid no of questions and pass percentage between 0 and 100. Please try importing it again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldThrowErrorWhenChapterDoesNotSpecifyQuestionsButQuestionsForTheChapterAreAvailable() {
        List<CourseCsvRequest> courseStructureCsvs = new ArrayList<>();
        courseStructureCsvs.add(new CourseCsvRequest("CoursePlan", "Course", CourseUnitState.Active, null, "Message Description", "Filename"));
        courseStructureCsvs.add(new CourseCsvRequest("Basic TB Symptoms", "Module", CourseUnitState.Active, "CoursePlan", "Message Description", "Filename"));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms", "Chapter", CourseUnitState.Active, "Basic TB Symptoms", "Message Description", "fileName", null, null, null, "", "50"));
        courseStructureCsvs.add(new CourseCsvRequest("Lesson TB Symptoms", "Lesson", CourseUnitState.Active, "Chapter TB Symptoms", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms", "Question", CourseUnitState.Active, "Chapter TB Symptoms", "Message Description", "FileName", "1;2", "1", "CorrectAnswer", "", ""));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(1, errors.size());
        assertEquals("The chapter has questions in the CSV but number of questions to be played in the quiz is not specified. Please specify the number of questions for the chapter and try again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorIfActiveCourseHasNoActiveChild() {
        List<CourseCsvRequest> courseStructureCsvs = new ArrayList<>();
        courseStructureCsvs.add(new CourseCsvRequest("CoursePlan", "Course", CourseUnitState.Active, null, "Message Description", "Filename"));
        courseStructureCsvs.add(new CourseCsvRequest("Basic TB Symptoms", "Module", CourseUnitState.Active, "CoursePlan", "Message Description", "Filename"));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms", "Chapter", CourseUnitState.Inactive, "Basic TB Symptoms", "Message Description", "fileName", null, null, null, "1", "1"));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms2", "Chapter", CourseUnitState.Inactive, "Basic TB Symptoms", "Message Description", "fileName", null, null, null, "0", "0"));
        courseStructureCsvs.add(new CourseCsvRequest("Lesson TB Symptoms", "Lesson", CourseUnitState.Active, "Chapter TB Symptoms", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Lesson TB Symptoms2", "Lesson", CourseUnitState.Active, "Chapter TB Symptoms2", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms", "Question", CourseUnitState.Active, "Chapter TB Symptoms", "Message Description", "FileName", "1;2", "1", "CorrectAnswer", "", ""));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(1, errors.size());
        assertEquals("A module should have at least one active chapter under it. Please add active chapters under the module or mark the module as inactive and try importing it again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldNotReturnErrorIfInActiveCourseHasHasNoActiveChild() {
        List<CourseCsvRequest> courseStructureCsvs = new ArrayList<>();
        courseStructureCsvs.add(new CourseCsvRequest("CoursePlan", "Course", CourseUnitState.Inactive, null, "Message Description", "Filename"));
        courseStructureCsvs.add(new CourseCsvRequest("Basic TB Symptoms", "Module", CourseUnitState.Inactive, "CoursePlan", "Message Description", "Filename"));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms", "Chapter", CourseUnitState.Inactive, "Basic TB Symptoms", "Message Description", "fileName", null, null, null, "1", "1"));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms2", "Chapter", CourseUnitState.Inactive, "Basic TB Symptoms", "Message Description", "fileName", null, null, null, "0", "0"));
        courseStructureCsvs.add(new CourseCsvRequest("Lesson TB Symptoms", "Lesson", CourseUnitState.Active, "Chapter TB Symptoms", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Lesson TB Symptoms2", "Lesson", CourseUnitState.Active, "Chapter TB Symptoms2", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms", "Question", CourseUnitState.Active, "Chapter TB Symptoms", "Message Description", "FileName", "1;2", "1", "CorrectAnswer", "", ""));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(0, errors.size());
    }

    @Test
    public void shouldReturnErrorIfActiveChapterHasHasNoActiveChild() {
        List<CourseCsvRequest> courseStructureCsvs = new ArrayList<>();
        courseStructureCsvs.add(new CourseCsvRequest("CoursePlan", "Course", CourseUnitState.Active, null, "Message Description", "Filename"));
        courseStructureCsvs.add(new CourseCsvRequest("Basic TB Symptoms", "Module", CourseUnitState.Active, "CoursePlan", "Message Description", "Filename"));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms", "Chapter", CourseUnitState.Active, "Basic TB Symptoms", "Message Description", "fileName", null, null, null, "0", "1"));
        courseStructureCsvs.add(new CourseCsvRequest("Lesson TB Symptoms", "Lesson", CourseUnitState.Inactive, "Chapter TB Symptoms", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Lesson TB Symptoms2", "Lesson", CourseUnitState.Inactive, "Chapter TB Symptoms", "Message Description", "FileName"));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(1, errors.size());
        assertEquals("A chapter should have at least one active lesson under it. Please add active lessons under the chapter or mark the chapter as inactive and try importing it again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldReturnErrorIfNoOfActiveQuestionsIsLessThanTheRequiredNoOfQuestionsToBeAnsweredForActiveChapter() {
        List<CourseCsvRequest> courseStructureCsvs = new ArrayList<>();
        courseStructureCsvs.add(new CourseCsvRequest("CoursePlan", "Course", CourseUnitState.Active, null, "Message Description", "Filename"));
        courseStructureCsvs.add(new CourseCsvRequest("Basic TB Symptoms", "Module", CourseUnitState.Active, "CoursePlan", "Message Description", "Filename"));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms", "Chapter", CourseUnitState.Active, "Basic TB Symptoms", "Message Description", "fileName", null, null, null, "2", "1"));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms2", "Chapter", CourseUnitState.Inactive, "Basic TB Symptoms", "Message Description", "fileName", null, null, null, "0", "0"));
        courseStructureCsvs.add(new CourseCsvRequest("Lesson TB Symptoms", "Lesson", CourseUnitState.Active, "Chapter TB Symptoms", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Lesson TB Symptoms2", "Lesson", CourseUnitState.Active, "Chapter TB Symptoms2", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms", "Question", CourseUnitState.Active, "Chapter TB Symptoms", "Message Description", "FileName", "1", "1", "CorrectAnswer", "", ""));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms2", "Question", CourseUnitState.Inactive, "Chapter TB Symptoms", "Message Description", "FileName", "1", "1", "CorrectAnswer", "", ""));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(1, errors.size());
        assertEquals("Number of active questions available in the CSV for this active chapter is less than number of quiz questions specified for the chapter. Please add more active questions for the chapter or  and try importing again.", errors.get(0).getMessage());
    }

    @Test
    public void shouldNotReturnErrorIfNoOfActiveQuestionsIsLessThanTheRequiredNoOfQuestionsToBeAnsweredForInActiveChapter() {
        List<CourseCsvRequest> courseStructureCsvs = new ArrayList<>();
        courseStructureCsvs.add(new CourseCsvRequest("CoursePlan", "Course", CourseUnitState.Active, null, "Message Description", "Filename"));
        courseStructureCsvs.add(new CourseCsvRequest("Basic TB Symptoms", "Module", CourseUnitState.Active, "CoursePlan", "Message Description", "Filename"));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms", "Chapter", CourseUnitState.Inactive, "Basic TB Symptoms", "Message Description", "fileName", null, null, null, "2", "1"));
        courseStructureCsvs.add(new CourseCsvRequest("Chapter TB Symptoms2", "Chapter", CourseUnitState.Active, "Basic TB Symptoms", "Message Description", "fileName", null, null, null, "0", "0"));
        courseStructureCsvs.add(new CourseCsvRequest("Lesson TB Symptoms", "Lesson", CourseUnitState.Active, "Chapter TB Symptoms", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Lesson TB Symptoms2", "Lesson", CourseUnitState.Active, "Chapter TB Symptoms2", "Message Description", "FileName"));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms", "Question", CourseUnitState.Active, "Chapter TB Symptoms", "Message Description", "FileName", "1", "1", "CorrectAnswer", "", ""));
        courseStructureCsvs.add(new CourseCsvRequest("Question TB Symptoms2", "Question", CourseUnitState.Inactive, "Chapter TB Symptoms", "Message Description", "FileName", "1", "1", "CorrectAnswer", "", ""));

        errors = courseStructureValidator.validate(courseStructureCsvs);

        assertEquals(0, errors.size());
    }
}
