package org.motechproject.whp.mtraining.web.controller;


import org.motechproject.whp.mtraining.csv.domain.CsvImportError;
import org.motechproject.whp.mtraining.csv.parser.CsvParser;
import org.motechproject.whp.mtraining.csv.request.ProviderCsvRequest;
import org.motechproject.whp.mtraining.csv.response.CsvImportResponse;
import org.motechproject.whp.mtraining.csv.validator.ProviderStructureValidator;
import org.motechproject.whp.mtraining.service.impl.ProviderImportService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.List;

import static java.util.Arrays.asList;
import static org.slf4j.LoggerFactory.getLogger;

@Controller
public class ProviderImportController {
    private Logger LOG = getLogger(ProviderImportController.class);

    private CsvParser csvParser;
    private ProviderStructureValidator providerStructureValidator;
    private ProviderImportService providerImportService;

    @Autowired
    public ProviderImportController(CsvParser csvParser, ProviderStructureValidator providerStructureValidator, ProviderImportService providerImportService) {
        this.csvParser = csvParser;
        this.providerStructureValidator = providerStructureValidator;
        this.providerImportService = providerImportService;
    }

    @RequestMapping(value = "/provider/import", method = RequestMethod.POST)
    @ResponseBody
    public CsvImportResponse importProviderStructure(@RequestParam("multipartFile") CommonsMultipartFile multipartFile) {
        try {
            List<ProviderCsvRequest> providerCsvRequests = csvParser.parse(multipartFile, ProviderCsvRequest.class);
            List<CsvImportError> errors = providerStructureValidator.validate(providerCsvRequests);
            if (!errors.isEmpty()) {
                return CsvImportResponse.failure(errors);
            }
            providerImportService.importProviders(providerCsvRequests);
            return CsvImportResponse.success("Providers have been successfully imported");
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            return CsvImportResponse.failure(asList(new CsvImportError(ex.getMessage())));
        }
    }
}
