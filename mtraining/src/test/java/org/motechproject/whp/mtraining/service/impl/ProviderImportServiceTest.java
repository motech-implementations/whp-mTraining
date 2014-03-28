package org.motechproject.whp.mtraining.service.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.whp.mtraining.csv.request.ProviderCsvRequest;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.repository.Providers;

import java.util.Arrays;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.whp.mtraining.web.domain.ProviderStatus.WORKING_PROVIDER;


@RunWith(MockitoJUnitRunner.class)
public class ProviderImportServiceTest {

    @Mock
    private Providers providers;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldAddToProvidersWhenImporting() {
        ProviderImportService providerImportService = new ProviderImportService(providers);
        String remediId = "RemediX";
        String primaryContactNumber = "9898989898";
        String providerStatus = WORKING_PROVIDER.getStatus();
        String state = "state";
        String block = "block";
        String district = "district";

        providerImportService.importProviders(Arrays.asList(new ProviderCsvRequest(remediId, primaryContactNumber, providerStatus, state, block, district)));

        ArgumentCaptor<Provider> providerArgumentCaptor = ArgumentCaptor.forClass(Provider.class);
        verify(providers).add(providerArgumentCaptor.capture());
        Provider provider = providerArgumentCaptor.getValue();
        assertEquals(provider.getRemediId(), remediId);
        assertEquals(provider.getCallerId(), Long.valueOf(primaryContactNumber));
        assertEquals(provider.getProviderStatus(), WORKING_PROVIDER.getStatus());

        Location providerLocation = provider.getLocation();

        assertNotNull(providerLocation);
        assertEquals(providerLocation.getState(), state);
        assertEquals(providerLocation.getBlock(), block);
        assertEquals(providerLocation.getDistrict(), district);
    }

    @Test
    public void shouldNotAddProvidersIfTheyAlreadyExist() {
        ProviderImportService providerImportService = new ProviderImportService(providers);
        when(providers.all()).thenReturn(newArrayList(new Provider()));

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Providers already exist in the database");

        providerImportService.importProviders(newArrayList(new ProviderCsvRequest()));
    }
}
