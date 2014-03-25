package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.whp.mtraining.csv.request.ProviderCsvRequest;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.repository.Providers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.motechproject.whp.mtraining.web.domain.ActivationStatus.from;

@Service
public class ProviderImportService {

    private Providers providers;

    @Autowired
    public ProviderImportService(Providers providers) {
        this.providers = providers;
    }

    public void importProviders(List<ProviderCsvRequest> providerCsvRequests) {
        if (isNotEmpty(providers.all()))
            throw new RuntimeException("Providers already exist in the database");
        for (ProviderCsvRequest providerCsvRequest : providerCsvRequests) {
            providers.add(createProvider(providerCsvRequest));
        }
    }

    private Provider createProvider(ProviderCsvRequest providerCsvRequest) {
        return new Provider(providerCsvRequest.getRemedyId(), Long.valueOf(providerCsvRequest.getPrimaryContactNumber()), from(providerCsvRequest.getActivationStatus()),
                providerCsvRequest.getDistrict(), providerCsvRequest.getBlock(), providerCsvRequest.getState());
    }
}


