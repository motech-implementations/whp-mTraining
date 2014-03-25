package org.motechproject.whp.mtraining.service.impl;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.repository.Providers;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.web.domain.ActivationStatus;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProviderServiceImplTest {

    @Test
    public void shouldAddProvider() {
        Providers providers = mock(Providers.class);
        ProviderService providerService = new ProviderServiceImpl(providers);
        Provider provider = new Provider("remedyId", 654654l, ActivationStatus.ACTIVE_RHP, "district", "block", "state");
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
}
