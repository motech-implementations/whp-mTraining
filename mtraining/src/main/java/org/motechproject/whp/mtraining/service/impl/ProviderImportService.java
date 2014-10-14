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

    public ProviderImportService(ProviderService providerService, LocationService locationService) {
        this.providerService = providerService;
        this.locationService = locationService;
    }

    public void importProviders(List<ProviderCsvRequest> providerCsvRequests) {
        for (ProviderCsvRequest providerCsvRequest : providerCsvRequests) {
            Provider provider = createProvider(providerCsvRequest);

            if (providerService.getProviderById(provider.getId()) == null) {
                providerService.createProvider(provider);
            } else {
                providerService.updateProvider(provider);
            }
        }
    }

    private Provider createProvider(ProviderCsvRequest providerCsvRequest) {
        Location location = createNewStateLocation(providerCsvRequest.getState());
        if (location == null) {
            location = locationService.getStateByName(providerCsvRequest.getState());
        }
        return new Provider(providerCsvRequest.getRemedi_id(), Long.valueOf(providerCsvRequest.getPrimary_contact()),
                from(providerCsvRequest.getProviderstatus()),
                location);
    }

    private Location createNewStateLocation (String stateName) {
        if (!locationService.doesStateExist(stateName)){
            return locationService.createLocation(new Location(stateName));
        }
        return null;
    }
}


