package org.motechproject.whp.mtraining.web.controller;


import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.util.ExceptionUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.jdo.JDODataStoreException;
import java.util.List;

/**
 * Web API for Provider
 */
@Controller
public class ProviderController {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(ProviderController.class);

    @Autowired
    ProviderService providerService;
    
    @RequestMapping("/providers")
    @ResponseBody
    public List<Provider> getAllProviders() {
        return providerService.getAllProviders();
    }

    @RequestMapping(value = "/provider/{providerId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Provider getProvider(@PathVariable long providerId) {
        return providerService.getProviderById(providerId);
    }

    @RequestMapping(value = "/provider", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> createProvider(@RequestBody Provider provider) {
        try {
            providerService.createProvider(provider);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (JDODataStoreException e) {
            String message = ExceptionUtil.getConstraintViolationMessage(e);
            LOG.info(message);
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/provider/{providerId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> updateProvider(@RequestBody Provider provider) {
        try {
            providerService.updateProvider(provider);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (JDODataStoreException e) {
            String message = ExceptionUtil.getConstraintViolationMessage(e);
            LOG.info(message);
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/provider/remediid/{remediId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> updateProviderMappedByRemediId(@RequestBody Provider provider) {
        try {
            providerService.updateProviderbyRemediId(provider);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (JDODataStoreException e) {
            String message = ExceptionUtil.getConstraintViolationMessage(e);
            LOG.info(message);
            return new ResponseEntity<>(message, HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/provider/{providerId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeProvider(@PathVariable long providerId) {
        Provider provider = providerService.getProviderById(providerId);
        providerService.deleteProvider(provider);
    }

}
