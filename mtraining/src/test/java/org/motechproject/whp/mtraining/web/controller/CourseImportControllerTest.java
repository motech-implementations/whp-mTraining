package org.motechproject.whp.mtraining.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.whp.mtraining.dto.ContentIdentifierDto;
import org.motechproject.whp.mtraining.csv.parser.CsvParser;
import org.motechproject.whp.mtraining.csv.request.CourseCsvRequest;
import org.motechproject.whp.mtraining.csv.response.CsvImportResponse;
import org.motechproject.whp.mtraining.csv.domain.CsvImportError;
import org.motechproject.whp.mtraining.csv.validator.CourseCsvStructureValidator;
import org.motechproject.whp.mtraining.csv.web.controller.CourseImportController;
import org.motechproject.whp.mtraining.service.impl.CourseImportService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class CourseImportControllerTest {
    private CourseImportController courseImportController;

    @Mock
    private CsvParser csvParser;
    @Mock
    private CourseImportService courseImportService;
    @Mock
    CourseCsvStructureValidator courseStructureValidator;

    @Before
    public void setUp() {
        courseImportController = new CourseImportController(csvParser, courseStructureValidator, courseImportService);
    }

    @Test
    public void shouldReturnErrorWhenCourseStructureIsInvalid() throws Exception {
        CommonsMultipartFile csvFile = mock(CommonsMultipartFile.class);
        CourseCsvRequest courseRequestWithoutParentName = new CourseCsvRequest("nodeName", "nodeType", "status", null, "description", "fileName");
        List<CourseCsvRequest> courseList = asList(courseRequestWithoutParentName);
        List<CsvImportError> errors = asList(new CsvImportError("nodeName", "nodeType", "some message"));
        when(csvParser.parse(csvFile, CourseCsvRequest.class)).thenReturn(courseList);
        when(courseStructureValidator.validate(courseList)).thenReturn(errors);

        CsvImportResponse response = courseImportController.importCourseStructure(csvFile);

        assertTrue(response.isFailure());
        assertEquals(errors, response.getErrors());
        verifyZeroInteractions(courseImportService);
    }

    @Test
    public void shouldReturnErrorResultWhenExceptionThrownParsingCsv() throws Exception {
        String exceptionMessage = "Error parsing CSV";
        when(csvParser.parse(any(MultipartFile.class), any(Class.class))).thenThrow(new IOException(exceptionMessage));
        List<CsvImportError> expectedError = asList(new CsvImportError(exceptionMessage));

        CsvImportResponse response = courseImportController.importCourseStructure(mock(CommonsMultipartFile.class));

        assertTrue(response.isFailure());
        assertEquals(expectedError, response.getErrors());
    }

    @Test
    public void shouldImportCourseStructureIfThereAreNoValidationFailures() throws IOException {
        CommonsMultipartFile csvFile = mock(CommonsMultipartFile.class);
        List<CourseCsvRequest> courseCsvRequests = asList(new CourseCsvRequest());
        when(csvParser.parse(csvFile, CourseCsvRequest.class)).thenReturn(courseCsvRequests);
        when(courseStructureValidator.validate(courseCsvRequests)).thenReturn(Collections.EMPTY_LIST);
        when(courseImportService.importCourse(courseCsvRequests)).thenReturn(new ContentIdentifierDto(UUID.randomUUID(), 1));

        CsvImportResponse csvImportResponse = courseImportController.importCourseStructure(csvFile);

        verify(courseImportService).importCourse(courseCsvRequests);
        assertTrue(csvImportResponse.isSuccess());
        assertTrue(csvImportResponse.getErrors().isEmpty());
    }

}
