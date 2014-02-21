package org.motechproject.whp.mtraining.web.controller;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.whp.mtraining.web.parser.CsvParser;
import org.motechproject.whp.mtraining.web.request.CourseStructureCsvRequest;
import org.motechproject.whp.mtraining.web.request.CsvImportRequest;

import java.io.StringReader;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CourseImportControllerTest {

    private CourseImportController courseImportController;

    @Mock
    private CsvParser csvParser;

    @Before
    public void setUp() {
        courseImportController = new CourseImportController(csvParser);
    }

    @Test
    public void shouldParseAGivenCsvFile() throws Exception {
        String csvContent = "csvContent";
        CsvImportRequest request = mock(CsvImportRequest.class);
        when(request.getStringContent()).thenReturn(csvContent);

        courseImportController.importCourseStructure(request);

        ArgumentCaptor<StringReader> captor = ArgumentCaptor.forClass(StringReader.class);
        verify(csvParser).parse(captor.capture(), eq(CourseStructureCsvRequest.class));
        StringReader actualReader = captor.getValue();
        assertEquals(csvContent, IOUtils.toString(actualReader));
    }
}
