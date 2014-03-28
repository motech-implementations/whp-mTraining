package org.motechproject.whp.mtraining.csv.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.whp.mtraining.csv.domain.CsvImportError;
import org.motechproject.whp.mtraining.csv.request.ProviderCsvRequest;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)

public class ProviderStructureValidatorTest {
    private ProviderStructureValidator providerStructureValidator;

    @Before
    public void setUp() {
        providerStructureValidator = new ProviderStructureValidator();
    }

    @Test
    public void shouldMandateARemediIdInCsvForEachRequest() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("    ", "1234567890", "working provider", "state", "block", "district");

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("Remedi Id is not present for caller: 1234567890", errors.get(0).getMessage());
    }

    @Test
    public void remediIdShouldBeUnique() {
        ProviderCsvRequest providerCsvRequest1 = new ProviderCsvRequest("RemediX", "1234567890", "working provider", "state", "block", "district");
        ProviderCsvRequest providerCsvRequest2 = new ProviderCsvRequest("RemediX", "1234567443", "working provider", "state2", "block3", "district2");

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest1, providerCsvRequest2));

        assertEquals(1, errors.size());
        assertEquals("Remedi Id: RemediX has multiple occurrences. Remedi id should be unique.", errors.get(0).getMessage());
    }

    @Test
    public void shouldCheckIfPrimaryContactNumberExistsInCsvForEachRequest() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemediX", "", "working provider", "state", "block", "district");

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("Primary Contact Number is missing.", errors.get(0).getMessage());
    }

    @Test
    public void shouldCheckIfPrimaryContactNumberIsNumeric() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemediX", "qwertyuiop", "working provider", "state", "block", "district");

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("Primary Contact Number is invalid for Remedi Id: RemediX. It should be a 10 digit phone number.", errors.get(0).getMessage());
    }

    @Test
    public void shouldCheckIfPrimaryContactNumberIsTenDigitsInLength() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemediX", "98989898", "working provider", "state", "block", "district");

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("Primary Contact Number is invalid for Remedi Id: RemediX. It should be a 10 digit phone number.", errors.get(0).getMessage());
    }

    @Test
    public void shouldCheckIfPrimaryContactNumberIsTUnique() {
        ProviderCsvRequest providerCsvRequest1 = new ProviderCsvRequest("RemediX", "1234567890", "working provider", "state", "block", "district");
        ProviderCsvRequest providerCsvRequest2 = new ProviderCsvRequest("RemediY", "1234567890", "working provider", "state", "block", "district");

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest1, providerCsvRequest2));

        assertEquals(1, errors.size());
        assertEquals("Primary Contact Number has multiple occurrences.", errors.get(0).getMessage());
    }

    @Test
    public void shouldVerifyForValidProviderStatus() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemediX", "1234567890", "Random", "state", "block", "district");

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("Provider Status for Remedi Id: RemediX is blank or invalid.", errors.get(0).getMessage());
    }

    @Test
    public void shouldVerifyForEmptyState() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemediX", "1234567890", "working provider", "", "block", "district");

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("State is blank for Remedi Id: RemediX.", errors.get(0).getMessage());
    }

    @Test
    public void shouldVerifyForEmptyDistrict() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemediX", "1234567890", "working provider", "state", "block", "");

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("District is blank for Remedi Id: RemediX.", errors.get(0).getMessage());
    }

    @Test
    public void shouldVerifyForEmptyBlock() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemediX", "1234567890", "working provider", "state", "", "district");

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("Block is blank for Remedi Id: RemediX.", errors.get(0).getMessage());
    }
}
