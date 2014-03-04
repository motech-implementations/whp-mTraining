package org.motechproject.whp.mtraining.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.whp.mtraining.web.model.ErrorModel;
import org.motechproject.whp.mtraining.csv.parser.CsvParser;
import org.motechproject.whp.mtraining.csv.CourseStructureCsvRequest;
import org.motechproject.whp.mtraining.service.impl.CourseImportService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class CourseImportControllerTest {

    @Mock
    private CourseImportController courseImportController;

    @Mock
    private CsvParser csvParser;

    @Mock
    private CourseImportService courseService;

    @Before
    public void setUp() {
        courseImportController = new CourseImportController(csvParser, courseService);
    }

    @Test
    public void shouldReturnErrorWhenCourseStructureIsInvalid() throws Exception {

        List<CourseStructureCsvRequest> courseList = new ArrayList<>();

        CourseStructureCsvRequest courseRequestWithoutParentName = new CourseStructureCsvRequest("nodeName", "nodeType", "status", null, "description", "fileName");
        courseList.add(courseRequestWithoutParentName);

        when(csvParser.parse(any(MultipartFile.class), any(Class.class))).thenReturn(courseList);

        ArrayList<ErrorModel> errorModels = new ArrayList<>();
        errorModels.add(new ErrorModel("nodeName", "nodeType", "some message"));

        when(courseService.parse(courseList)).thenReturn(errorModels);

        List<ErrorModel> courseErrors = courseImportController.importCourseStructure(mock(CommonsMultipartFile.class));

        assertThat(courseErrors.size(),is(1));

        verify(csvParser).parse(any(MultipartFile.class), any(Class.class));
        verify(courseService).parse(courseList);
    }

    @Test
    public void shouldReturnErrorResultWhenExceptionThrownParsingCsv() throws Exception {

        when(csvParser.parse(any(MultipartFile.class), any(Class.class))).thenThrow(new RuntimeException("All the headers are not present"));
        List<ErrorModel> errorModels = courseImportController.importCourseStructure(mock(CommonsMultipartFile.class));
        assertThat(errorModels.size(),is(1));
    }

}
