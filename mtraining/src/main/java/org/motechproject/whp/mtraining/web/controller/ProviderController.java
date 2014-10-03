package org.motechproject.whp.mtraining.web.controller;


import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Web API for Provider
 */
@Controller
public class ProviderController {

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
    public void createProvider(@RequestBody Provider provider) {
        providerService.createProvider(provider);
    }

    @RequestMapping(value = "/provider/{providerId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public void updateProvider(@RequestBody Provider provider) {
        providerService.updateProvider(provider);
    }

    @RequestMapping(value = "/provider/remediid/{remediId}", method = RequestMethod.PUT, consumes = "application/json")
    @ResponseBody
    public void updateProviderMappedbyRemediId(@RequestBody Provider provider) {
        providerService.updateProviderbyRemediId(provider);
    }

    @RequestMapping(value = "/provider/{providerId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void removeProvider(@PathVariable long providerId) {
        Provider provider = providerService.getProviderById(providerId);
        providerService.deleteProvider(provider);
    }
}
