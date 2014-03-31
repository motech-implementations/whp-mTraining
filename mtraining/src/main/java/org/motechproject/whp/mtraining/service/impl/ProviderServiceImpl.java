package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.repository.Providers;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.motechproject.whp.mtraining.web.domain.ProviderStatus.isInvalid;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.NOT_WORKING_PROVIDER;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.OK;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.UNKNOWN_PROVIDER;

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

    public Provider byCallerId(Long callerId) {
        return providers.getByCallerId(callerId);
    }

    public ResponseStatus validateProvider(Long callerId) {
        Provider provider = byCallerId(callerId);
        if (provider == null)
            return UNKNOWN_PROVIDER;
        if (isInvalid(provider.getProviderStatus()))
            return NOT_WORKING_PROVIDER;
        return OK;
    }
}
