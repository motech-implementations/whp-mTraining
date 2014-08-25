package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.domain.Location;

import java.util.List;

/**
 * Service for basic Location related methods
 */
public interface LocationService {

    Location createLocation(Location location);

    Location updateLocation(Location location);

    void deleteLocation(Location location);

    List<Location> getAllLocations();

    Location getLocationById(long id);

    List<Location> getLocationByName(String locationName);
}
