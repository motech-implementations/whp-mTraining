package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.whp.mtraining.domain.Location;
import org.motechproject.whp.mtraining.service.LocationService;
import org.motechproject.whp.mtraining.service.impl.ContentOperationServiceImpl;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jdo.JDOException;
import java.util.List;

/**
 * Web API for Locations
 */
@Controller
public class LocationController {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ContentOperationServiceImpl.class);

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

    @RequestMapping("/unusedLocationsByCourse")
    @ResponseBody
    public List<Location> getUnusedLocationsByCourse() {
        return locationService.getUnusedLocationsByCourse();
    }

    @RequestMapping("/locations")
    @ResponseBody
    public List<Location> getLocations() {
        return locationService.getAllLocations();
    }

    @RequestMapping(value = "/location/{locationId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Location getLocation(@PathVariable long locationId) {
        return locationService.getLocationById(locationId);
    }

    @RequestMapping(value = "/location", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> createLocation(@RequestBody Location location) {
        try {
            locationService.createLocation(location);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (JDOException e) {
            LOG.warn(e.getNestedExceptions()[0].getMessage());
            return new ResponseEntity<>(e.getNestedExceptions()[0].getMessage(), HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/location/{locationId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> updateLocation(@RequestBody Location location) {
        try {
            locationService.updateLocation(location);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (JDOException e) {
            LOG.warn(e.getNestedExceptions()[0].getMessage());
            return new ResponseEntity<>(e.getNestedExceptions()[0].getMessage(), HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/location/{locationId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeLocation(@PathVariable long locationId) {
        locationService.deleteLocation(getLocation(locationId));
    }
}
