package org.motechproject.whp.mtraining.service.impl;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.repository.Providers;
import org.motechproject.whp.mtraining.service.ProviderService;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProviderServiceImplTest {

    @Test
    public void shouldAddProvider() {
        Providers providers = mock(Providers.class);
        ProviderService providerService = new ProviderServiceImpl(providers);
        Provider provider = new Provider(654654l);

        Provider persistedProvider = new Provider(654654l);
        persistedProvider.setId(100l);
        when(providers.save(provider)).thenReturn(persistedProvider);

        Long id = providerService.add(provider);

        assertThat(id, Is.is(100l));

        verify(providers).save(provider);
    }


    @Test
    public void shouldDeleteProvider() {
        Providers providers = mock(Providers.class);
        ProviderService providerService = new ProviderServiceImpl(providers);

        providerService.delete(100l);

        verify(providers).delete(100l);
    }

}
