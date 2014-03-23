package org.motechproject.whp.mtraining.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.whp.mtraining.csv.parser.CsvParser;
import org.motechproject.whp.mtraining.csv.request.CsvRequest;
import org.motechproject.whp.mtraining.csv.response.CourseImportResponse;
import org.motechproject.whp.mtraining.csv.validator.CourseImportError;
import org.motechproject.whp.mtraining.csv.validator.CourseStructureValidator;
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
    CourseStructureValidator courseStructureValidator;

    @Before
    public void setUp() {
        courseImportController = new CourseImportController(csvParser, courseStructureValidator, courseImportService);
    }

    @Test
    public void shouldReturnErrorWhenCourseStructureIsInvalid() throws Exception {
        CommonsMultipartFile csvFile = mock(CommonsMultipartFile.class);
        CsvRequest courseRequestWithoutParentName = new CsvRequest("nodeName", "nodeType", "status", null, "description", "fileName");
        List<CsvRequest> courseList = asList(courseRequestWithoutParentName);
        List<CourseImportError> errors = asList(new CourseImportError("nodeName", "nodeType", "some message"));
        when(csvParser.parse(csvFile, CsvRequest.class)).thenReturn(courseList);
        when(courseStructureValidator.validate(courseList)).thenReturn(errors);

        CourseImportResponse response = courseImportController.importCourseStructure(csvFile);

        assertTrue(response.isFailure());
        assertEquals(errors, response.getErrors());
        verifyZeroInteractions(courseImportService);
    }

    @Test
    public void shouldReturnErrorResultWhenExceptionThrownParsingCsv() throws Exception {
        String exceptionMessage = "Error parsing CSV";
        when(csvParser.parse(any(MultipartFile.class), any(Class.class))).thenThrow(new IOException(exceptionMessage));
        List<CourseImportError> expectedError = asList(new CourseImportError(exceptionMessage));

        CourseImportResponse response = courseImportController.importCourseStructure(mock(CommonsMultipartFile.class));

        assertTrue(response.isFailure());
        assertEquals(expectedError, response.getErrors());
    }

    @Test
    public void shouldImportCourseStructureIfThereAreNoValidationFailures() throws IOException {
        CommonsMultipartFile csvFile = mock(CommonsMultipartFile.class);
        List<CsvRequest> CsvRequests = asList(new CsvRequest());
        when(csvParser.parse(csvFile, CsvRequest.class)).thenReturn(CsvRequests);
        when(courseStructureValidator.validate(CsvRequests)).thenReturn(Collections.EMPTY_LIST);
        when(courseImportService.importCourse(CsvRequests)).thenReturn(new ContentIdentifierDto(UUID.randomUUID(), 1));

        CourseImportResponse courseImportResponse = courseImportController.importCourseStructure(csvFile);

        verify(courseImportService).importCourse(CsvRequests);
        assertTrue(courseImportResponse.isSuccess());
        assertTrue(courseImportResponse.getErrors().isEmpty());
    }

}
