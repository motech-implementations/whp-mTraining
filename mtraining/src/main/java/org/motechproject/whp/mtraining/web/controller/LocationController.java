package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Web API for Locations
 */
@Controller
public class LocationController {

    @Autowired
    LocationService locationService;

    @RequestMapping("/blockLocations")
    @ResponseBody
    public List<Location> getBlockLocations() {
        return locationService.getBlockLocations();
    }

    @RequestMapping("/stateLocations")
    @ResponseBody
    public List<Location> getStateLocations() {
        return locationService.getStateLocations();
    }

    @RequestMapping(value = "/location/{locationId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Location getLocation(@PathVariable long locationId) {
        return locationService.getLocationById(locationId);
    }

    @RequestMapping(value = "/location", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public void createLocation(@RequestBody Location location) {
        locationService.createLocation(location);
    }

    @RequestMapping(value = "/location/{locationId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public void updateLocation(@RequestBody Location location) {
        locationService.updateLocation(location);
    }

    @RequestMapping(value = "/location/{locationId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeLocation(@RequestBody Location location) {
        locationService.deleteLocation(location);
    }
}
