package org.motechproject.whp.mtraining.csv.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.whp.mtraining.csv.domain.CsvImportError;
import org.motechproject.whp.mtraining.csv.request.ProviderCsvRequest;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.repository.Providers;
import org.motechproject.whp.mtraining.web.domain.ProviderStatus;

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)

public class ProviderStructureValidatorTest {
    private ProviderStructureValidator providerStructureValidator;
    @Mock
    private Providers providers;

    @Before
    public void setUp()
    {
        providerStructureValidator = new ProviderStructureValidator(providers);
    }

    @Test
    public void shouldMandateARemediIdInCsvForEachRequest() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("    ", "1234567890", "working provider", "state", "block", "district");
        when(providers.all()).thenReturn(Collections.<Provider>emptyList());

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("Remedi Id is not present for caller: 1234567890", errors.get(0).getMessage());
    }

    @Test
    public void remediIdShouldBeUnique() {
        ProviderCsvRequest providerCsvRequest1 = new ProviderCsvRequest("RemediX", "1234567890", "working provider", "state", "block", "district");
        ProviderCsvRequest providerCsvRequest2 = new ProviderCsvRequest("RemediX", "1234567443", "working provider", "state2", "block3", "district2");
        when(providers.all()).thenReturn(Collections.<Provider>emptyList());

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest1, providerCsvRequest2));

        assertEquals(1, errors.size());
        assertEquals("Remedi Id: RemediX has multiple occurrences. Remedi id should be unique.", errors.get(0).getMessage());
    }

    @Test
    public void shouldCheckIfPrimaryContactNumberExistsInCsvForEachRequest() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemediX", "", "working provider", "state", "block", "district");
        when(providers.all()).thenReturn(Collections.<Provider>emptyList());

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("Primary Contact Number is missing.", errors.get(0).getMessage());
    }

    @Test
    public void shouldCheckIfPrimaryContactNumberIsNumeric() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemediX", "qwertyuiop", "working provider", "state", "block", "district");
        when(providers.all()).thenReturn(Collections.<Provider>emptyList());

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("Primary Contact Number is invalid for Remedi Id: RemediX. It should be a 10 digit phone number.", errors.get(0).getMessage());
    }

    @Test
    public void shouldCheckIfPrimaryContactNumberIsTenDigitsInLength() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemediX", "98989898", "working provider", "state", "block", "district");
        when(providers.all()).thenReturn(Collections.<Provider>emptyList());

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("Primary Contact Number is invalid for Remedi Id: RemediX. It should be a 10 digit phone number.", errors.get(0).getMessage());
    }

    @Test
    public void shouldCheckIfPrimaryContactNumberIsTUnique() {
        ProviderCsvRequest providerCsvRequest1 = new ProviderCsvRequest("RemediX", "1234567890", "working provider", "state", "block", "district");
        ProviderCsvRequest providerCsvRequest2 = new ProviderCsvRequest("RemediY", "1234567890", "working provider", "state", "block", "district");
        when(providers.all()).thenReturn(Collections.<Provider>emptyList());

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest1, providerCsvRequest2));

        assertEquals(1, errors.size());
        assertEquals("Primary Contact Number has multiple occurrences.", errors.get(0).getMessage());
    }

    @Test
    public void shouldVerifyForValidProviderStatus() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemediX", "1234567890", "Random", "state", "block", "district");
        when(providers.all()).thenReturn(Collections.<Provider>emptyList());

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("Provider Status for Remedi Id: RemediX is blank or invalid.", errors.get(0).getMessage());
    }

    @Test
    public void shouldVerifyForEmptyState() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemediX", "1234567890", "working provider", "", "block", "district");
        when(providers.all()).thenReturn(Collections.<Provider>emptyList());

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("State is blank for Remedi Id: RemediX.", errors.get(0).getMessage());
    }

    @Test
    public void shouldVerifyForEmptyDistrict() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemediX", "1234567890", "working provider", "state", "block", "");
        when(providers.all()).thenReturn(Collections.<Provider>emptyList());

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("District is blank for Remedi Id: RemediX.", errors.get(0).getMessage());
    }

    @Test
    public void shouldVerifyForEmptyBlock() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemediX", "1234567890", "working provider", "state", "", "district");
        when(providers.all()).thenReturn(Collections.<Provider>emptyList());

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("Block is blank for Remedi Id: RemediX.", errors.get(0).getMessage());
    }

    @Test
    public void shouldVerifyForDifferentContactNumberForSameRemediId() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemediX", "1234567890", "working provider", "state", "block", "district");
        when(providers.all()).thenReturn(asList(new Provider("RemediX", 1234567891L, ProviderStatus.NOT_WORKING_PROVIDER,new Location("state","block","district"))));

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(0, errors.size());

    }

    @Test
    public void shouldVerifyForSameContactNumberForDifferentRemediId() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemediX", "1234567890", "working provider", "state", "block", "district");
        when(providers.all()).thenReturn(asList(new Provider("RemediY", 1234567890L, ProviderStatus.NOT_WORKING_PROVIDER,new Location("state2","block2","district2"))));

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(1, errors.size());
        assertEquals("Database contains the given contact number associated with Remedi Id: RemediY.", errors.get(0).getMessage());
    }

    @Test
    public void shouldVerifyForSameContactNumberForSameRemediId() {
        ProviderCsvRequest providerCsvRequest = new ProviderCsvRequest("RemediX", "1234567890", "working provider", "state", "block", "district");
        when(providers.all()).thenReturn(asList(new Provider("RemediX", 1234567890L, ProviderStatus.NOT_WORKING_PROVIDER,new Location("state","block","district"))));

        List<CsvImportError> errors = providerStructureValidator.validate(newArrayList(providerCsvRequest));

        assertEquals(0, errors.size());
    }

}
