package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.repository.LocationDataService;
import org.motechproject.whp.mtraining.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("locationService")
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationDataService locationDataService;

    @Override
    public Location createLocation(Location location) {
        return locationDataService.create(location);
    }

    @Override
    public Location updateLocation(Location location) {
        return locationDataService.update(location);
    }

    @Override
    public void deleteLocation(Location location) {
        locationDataService.delete(location);
    }

    @Override
    public List<Location> getAllLocations() {
        return locationDataService.retrieveAll();
    }

    @Override
    public Location getLocationById(long id) {
        return locationDataService.findLocationById(id);
    }

    @Override
    public List<Location> getLocationByName(String locationName) {
        return locationDataService.findLocationByName(locationName);
    }
}
