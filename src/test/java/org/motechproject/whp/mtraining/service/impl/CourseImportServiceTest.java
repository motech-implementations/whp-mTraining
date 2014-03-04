package org.motechproject.whp.mtraining.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.whp.mtraining.validator.CourseStructureValidator;
import org.motechproject.whp.mtraining.web.model.ErrorModel;
import org.motechproject.whp.mtraining.csv.CourseStructureCsvRequest;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CourseImportServiceTest {

    private CourseImportService courseImportService;

    @Mock
    private CourseStructureValidator courseStructureValidator;

    @Before
    public void setUp() {
        courseImportService = new CourseImportService(courseStructureValidator);
    }

    @Test
    public void shouldValidateGivenCSVRequestList() {
        List<ErrorModel> expectedErrors = asList(new ErrorModel("error message"));
        List<CourseStructureCsvRequest> requests = asList(new CourseStructureCsvRequest());
        when(courseStructureValidator.validate(requests)).thenReturn(expectedErrors);

        List<ErrorModel> actualErrors = courseImportService.parse(requests);

        assertEquals(expectedErrors,actualErrors);
    }
}

