package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.reports.CallLogReporter;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.web.domain.BasicResponse;
import org.motechproject.whp.mtraining.web.domain.CallLogRequest;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.motechproject.whp.mtraining.web.domain.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.UNKNOWN_PROVIDER;
import static org.springframework.http.HttpStatus.OK;

@Controller
public class CallLogController {

    private Logger LOGGER = LoggerFactory.getLogger(CallLogController.class);
    private CallLogReporter callLogReporter;

    private ProviderService providerService;

    @Autowired
    public CallLogController(CallLogReporter callLogReporter, ProviderService providerService) {
        this.callLogReporter = callLogReporter;
        this.providerService = providerService;
    }

    @RequestMapping(value = "/callLog", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public ResponseEntity<? extends MotechResponse> postCallLog(@RequestBody CallLogRequest callLogRequest) {
        Long callerId = callLogRequest.getCallerId();
        String sessionId = callLogRequest.getSessionId();
        String uniqueId = callLogRequest.getUniqueId();
        LOGGER.debug(String.format("Received bookmarkDto request for caller %s with session %s and uniqueId %s", callerId, sessionId, uniqueId));
        BasicResponse basicResponse = new BasicResponse(callerId, uniqueId, sessionId);
        List<ValidationError> validationErrors = callLogRequest.validate();

        if (!validationErrors.isEmpty()) {
            ValidationError firstValidationError = validationErrors.get(0);
            return new ResponseEntity<>(basicResponse.withResponse(firstValidationError.getErrorCode(), firstValidationError.getMessage()), OK);
        }

        Provider provider = providerService.byCallerId(callLogRequest.getCallerId());
        if (provider == null) {
            return new ResponseEntity<>(basicResponse.withResponse(UNKNOWN_PROVIDER), OK);
        }
        callLogReporter.report(callLogRequest, provider);
        return new ResponseEntity<>(basicResponse.withResponse(ResponseStatus.OK), OK);
    }
}
