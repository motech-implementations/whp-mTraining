package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.whp.mtraining.csv.request.ProviderCsvRequest;
import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.service.LocationService;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.motechproject.whp.mtraining.web.domain.ProviderStatus.from;

@Service
public class ProviderImportService {

    @Autowired
    private ProviderService providerService;

    @Autowired
    private LocationService locationService;

    public ProviderImportService() {}

    public ProviderImportService(ProviderService providerService) {
        this.providerService = providerService;
    }

    public void importProviders(List<ProviderCsvRequest> providerCsvRequests) {
        for (ProviderCsvRequest providerCsvRequest : providerCsvRequests) {
            Provider provider = createProvider(providerCsvRequest);
            createNewStateLocation(providerCsvRequest.getState());

            if (providerService.getProviderById(provider.getId()) == null) {
                providerService.createProvider(provider);
            } else {
                providerService.updateProvider(provider);
            }
        }
    }

    private Provider createProvider(ProviderCsvRequest providerCsvRequest) {
        Location location = locationService.getBlockByName(providerCsvRequest.getBlock());
        if (location == null) {
            location = new Location(providerCsvRequest.getBlock(), providerCsvRequest.getDistrict(), providerCsvRequest.getState());
        }

        return new Provider(providerCsvRequest.getRemedi_id(), Long.valueOf(providerCsvRequest.getPrimary_contact()),
                from(providerCsvRequest.getProviderstatus()),
                location);
    }

    private void createNewStateLocation (String stateName) {
        if (!locationService.doesStateExist(stateName)){
            locationService.createLocation(new Location(null, null, stateName));
        }
    }
}


