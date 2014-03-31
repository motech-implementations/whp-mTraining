package org.motechproject.whp.mtraining.service.impl;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.repository.Providers;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.web.domain.ProviderStatus;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.NOT_WORKING_PROVIDER;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.UNKNOWN_PROVIDER;

@RunWith(MockitoJUnitRunner.class)
public class ProviderServiceImplTest {

    @Mock
    private Providers providers;

    private ProviderServiceImpl providerService;

    @Before
    public void setUp() {
        providerService = new ProviderServiceImpl(providers);
    }

    @Test
    public void shouldAddProvider() {
        Provider provider = new Provider("remediId", 654654l, ProviderStatus.WORKING_PROVIDER, new Location("block", "district", "state"));
        Provider persistedProvider = mock(Provider.class);
        when(providers.add(provider)).thenReturn(persistedProvider);
        when(persistedProvider.getId()).thenReturn(100l);

        Long id = providerService.add(provider);

        assertThat(id, Is.is(100l));
        verify(providers).add(provider);
    }

    @Test
    public void shouldDeleteProvider() {
        Providers providers = mock(Providers.class);
        ProviderService providerService = new ProviderServiceImpl(providers);

        providerService.delete(100l);

        verify(providers).delete(100l);
    }

    @Test
    public void shouldMarkCallerAsUnidentifiedIfCallerIdNotRegistered() {
        long callerId = 76465464L;
        when(providers.getByCallerId(callerId)).thenReturn(null);

        ResponseStatus responseStatus = providerService.validateProvider(callerId);

        verify(providers).getByCallerId(callerId);
        assertEquals(responseStatus.getCode(), UNKNOWN_PROVIDER.getCode());
    }

    @Test
    public void shouldMarkErrorIfProviderIsNotValid() {
        long callerId = 76465464L;
        Provider provider = new Provider("remediId", callerId, ProviderStatus.NOT_WORKING_PROVIDER, new Location("block", "district", "state"));
        when(providers.getByCallerId(callerId)).thenReturn(provider);

        ResponseStatus response = providerService.validateProvider(callerId);

        verify(providers).getByCallerId(callerId);
        assertEquals(response, NOT_WORKING_PROVIDER);
    }
}
