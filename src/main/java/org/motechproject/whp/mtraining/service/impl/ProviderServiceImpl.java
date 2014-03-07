package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.repository.Providers;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("providerService")
public class ProviderServiceImpl implements ProviderService {

    private Providers providers;

    @Autowired
    public ProviderServiceImpl(Providers providers) {
        this.providers = providers;
    }

    @Override
    public Long add(Provider provider) {
        Provider savedProvider = providers.add(provider);
        return savedProvider.getId();
    }

    @Override
    public boolean delete(Long providerId) {
        return providers.delete(providerId);
    }
}
