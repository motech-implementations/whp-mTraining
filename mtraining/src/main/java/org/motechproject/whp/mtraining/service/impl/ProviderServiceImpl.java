package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.repository.ProviderDataService;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.web.domain.ProviderStatus;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.motechproject.whp.mtraining.web.domain.ProviderStatus.isInvalid;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.NOT_WORKING_PROVIDER;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.OK;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.UNKNOWN_PROVIDER;

@Service("providerService")
public class ProviderServiceImpl implements ProviderService {

    @Autowired
    private ProviderDataService providerDataService;

    @Override
    public Provider createProvider(Provider provider) {
        return providerDataService.create(provider);
    }

    @Override
    public Provider updateProvider(Provider provider) {
        Provider providerToUpdate = getProviderById(provider.getId());
        providerToUpdate.setRemediId(provider.getRemediId());
        providerToUpdate.setCallerId(provider.getCallerId());
        providerToUpdate.setProviderStatus(provider.getProviderStatus());
        providerToUpdate.setLocation(provider.getLocation());
        return providerDataService.update(providerToUpdate);
    }

    @Override
    public void deleteProvider(Provider provider) {
        providerDataService.delete(provider);
    }

    @Override
    public Provider getProviderById(long id) {
        return providerDataService.findProviderById(id);
    }

    @Override
    public Provider getProviderByCallerId(Long callerId) {
        List<Provider> providers = providerDataService.findProviderByCallerId(callerId);
        return (providers.size() > 0) ? providers.get(0) : null;
    }

    @Override
    public Provider getProviderByRemediId(String remediId) {
        List<Provider> providers = providerDataService.findProviderByRemediId(remediId);
        return (providers.size() > 0) ? providers.get(0) : null;
    }

    @Override
    public List<Provider> getAllProviders() {
        return providerDataService.retrieveAll();
    }

    public ResponseStatus validateProvider(Long callerId) {
        Provider provider = getProviderByCallerId(callerId);
        if (provider == null)
            return UNKNOWN_PROVIDER;
        if (provider.getProviderStatus() == ProviderStatus.NOT_WORKING_PROVIDER)
            return NOT_WORKING_PROVIDER;
        return OK;
    }
}
