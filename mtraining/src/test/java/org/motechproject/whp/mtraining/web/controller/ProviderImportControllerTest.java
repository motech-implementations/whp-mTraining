package org.motechproject.whp.mtraining.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.whp.mtraining.csv.parser.CsvParser;
import org.motechproject.whp.mtraining.csv.request.ProviderCsvRequest;
import org.motechproject.whp.mtraining.csv.response.CsvImportResponse;
import org.motechproject.whp.mtraining.csv.domain.CsvImportError;
import org.motechproject.whp.mtraining.csv.validator.ProviderStructureValidator;
import org.motechproject.whp.mtraining.service.impl.ProviderImportService;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.whp.mtraining.csv.response.CsvImportResponse.FAILURE_RESPONSE_STATUS;
import static org.motechproject.whp.mtraining.csv.response.CsvImportResponse.SUCCESS_RESPONSE_STATUS;

@RunWith(MockitoJUnitRunner.class)
public class ProviderImportControllerTest {

    @Mock
    private CsvParser csvParser;
    @Mock
    private ProviderStructureValidator providerStructureValidator;
    @Mock
    private ProviderImportService providerImportService;

    @Test
    public void shouldImportSuccessfullyWhenTheRequestsAreValid() throws IOException {
        CommonsMultipartFile csvFile = mock(CommonsMultipartFile.class);
        ProviderImportController providerImportController = new ProviderImportController(csvParser, providerStructureValidator, providerImportService);
        ArrayList<ProviderCsvRequest> providerCsvRequests = newArrayList(new ProviderCsvRequest("remedyId", "1234567890", "Active Rhp", "state", "block", "district"));
        when(csvParser.parse(csvFile, ProviderCsvRequest.class)).thenReturn(providerCsvRequests);
        when(providerStructureValidator.validate(providerCsvRequests)).thenReturn(Collections.EMPTY_LIST);

        CsvImportResponse importResponse = providerImportController.importProviderStructure(csvFile);

        verify(providerImportService).importProviders(providerCsvRequests);
        assertEquals(importResponse.getStatus(), SUCCESS_RESPONSE_STATUS);
        assertEquals(importResponse.getMessage(), "Providers have been successfully imported");
    }

    @Test
    public void shouldReturnErrorsWhenProviderListIsInvalid() throws IOException {
        CommonsMultipartFile csvFile = mock(CommonsMultipartFile.class);
        ProviderImportController providerImportController = new ProviderImportController(csvParser, providerStructureValidator, providerImportService);
        ArrayList<ProviderCsvRequest> providerCsvRequests = newArrayList(new ProviderCsvRequest("remedyId", "1234567890", "Active Rhp", "state", "block", "district"));
        when(csvParser.parse(csvFile, ProviderCsvRequest.class)).thenReturn(providerCsvRequests);
        when(providerStructureValidator.validate(providerCsvRequests)).thenReturn(newArrayList(new CsvImportError("Some error")));

        CsvImportResponse importResponse = providerImportController.importProviderStructure(csvFile);

        verify(providerImportService, never()).importProviders(providerCsvRequests);
        assertEquals(importResponse.getStatus(), FAILURE_RESPONSE_STATUS);
        assertEquals(importResponse.getMessage(), "Could not import the CSV due to errors. Please fix the errors and try importing again.");
    }
}
