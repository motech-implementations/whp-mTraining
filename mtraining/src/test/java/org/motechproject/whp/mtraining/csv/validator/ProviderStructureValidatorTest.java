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
    public void shouldMandateARemedyIdInCsvForEachRequest() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("    ", "1234567890", "Active rhp", "state", "block", "district");

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("Remedy Id is not present for caller : 1234567890", errors.get(0).getMessage());
    }

    @Test
    public void remedyIdShouldBeUnique() {
        ProviderCsvRequest providerCsvRequest1 = new ProviderCsvRequest("RemedyX", "1234567890", "Active rhp", "state", "block", "district");
        ProviderCsvRequest providerCsvRequest2 = new ProviderCsvRequest("RemedyX", "1234567443", "Active rhp", "state2", "block3", "district2");

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest1, providerCsvRequest2));

        assertEquals(1, errors.size());
        assertEquals("Remedy Id : RemedyX has multiple occurrences. Remedy id should be unique.", errors.get(0).getMessage());
    }

    @Test
    public void shouldMandateAPrimaryContactNumberInCsvForEachRequest() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemedyX", "", "Active rhp", "state", "block", "district");

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("Primary Contact Number is invalid for Remedy Id: RemedyX. It should be a 10 digit phone number.", errors.get(0).getMessage());
    }

    @Test
    public void shouldCheckIfPrimaryContactNumberIsNumeric() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemedyX", "qwertyuiop", "Active rhp", "state", "block", "district");

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("Primary Contact Number is invalid for Remedy Id: RemedyX. It should be a 10 digit phone number.", errors.get(0).getMessage());
    }

    @Test
    public void shouldCheckIfPrimaryContactNumberIsTenDigitsInLength() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemedyX", "98989898", "Active rhp", "state", "block", "district");

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("Primary Contact Number is invalid for Remedy Id: RemedyX. It should be a 10 digit phone number.", errors.get(0).getMessage());
    }

    @Test
    public void shouldCheckIfPrimaryContactNumberIsTUnique() {
        ProviderCsvRequest providerCsvRequest1 = new ProviderCsvRequest("RemedyX", "1234567890", "Active rhp", "state", "block", "district");
        ProviderCsvRequest providerCsvRequest2 = new ProviderCsvRequest("RemedyY", "1234567890", "Active rhp", "state", "block", "district");

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest1, providerCsvRequest2));

        assertEquals(1, errors.size());
        assertEquals("Primary Contact has multiple occurrences.", errors.get(0).getMessage());
    }

    @Test
    public void shouldVerifyForValidActivationStatus() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemedyX", "1234567890", "Random", "state", "block", "district");

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("Activation Status for Remedy Id: RemedyX is blank or invalid.", errors.get(0).getMessage());
    }

    @Test
    public void shouldVerifyForEmptyState() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemedyX", "1234567890", "Active rhp", "", "block", "district");

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("State is blank for Remedy Id: RemedyX.", errors.get(0).getMessage());
    }

    @Test
    public void shouldVerifyForEmptyDistrict() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemedyX", "1234567890", "Active rhp", "state", "block", "");

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("District is blank for Remedy Id: RemedyX.", errors.get(0).getMessage());
    }

    @Test
    public void shouldVerifyForEmptyBlock() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemedyX", "1234567890", "Active rhp", "state", "", "district");

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("Block is blank for Remedy Id: RemedyX.", errors.get(0).getMessage());
    }
}
