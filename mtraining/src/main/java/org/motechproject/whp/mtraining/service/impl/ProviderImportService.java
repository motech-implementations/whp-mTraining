package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.whp.mtraining.csv.request.ProviderCsvRequest;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.repository.Providers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.motechproject.whp.mtraining.web.domain.ProviderStatus.from;

@Service
public class ProviderImportService {

    private Providers providers;

    @Autowired
    public ProviderImportService(Providers providers) {
        this.providers = providers;
    }

    public void importProviders(List<ProviderCsvRequest> providerCsvRequests) {
        for (ProviderCsvRequest providerCsvRequest : providerCsvRequests) {
            providers.addOrUpdate(createProvider(providerCsvRequest));
        }
    }

    private Provider createProvider(ProviderCsvRequest providerCsvRequest) {
        Location location = new Location(providerCsvRequest.getBlock(), providerCsvRequest.getDistrict(), providerCsvRequest.getState());
        return new Provider(providerCsvRequest.getRemedi_id(), Long.valueOf(providerCsvRequest.getPrimary_contact()),
                from(providerCsvRequest.getProviderstatus()),
                location);
    }
}


