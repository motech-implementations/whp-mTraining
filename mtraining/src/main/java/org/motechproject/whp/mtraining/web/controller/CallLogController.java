package org.motechproject.whp.mtraining.web.controller;

import org.motechproject.whp.mtraining.reports.CallLogReporter;
import org.motechproject.whp.mtraining.web.domain.BasicResponse;
import org.motechproject.whp.mtraining.web.domain.CallLogRequest;
import org.motechproject.whp.mtraining.web.domain.MotechResponse;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.HttpStatus.OK;

@Controller
public class CallLogController {

    private Logger LOGGER = LoggerFactory.getLogger(CallLogController.class);
    private CallLogReporter callLogReporter;

    @Autowired
    public CallLogController(CallLogReporter callLogReporter) {
        this.callLogReporter = callLogReporter;
    }

    @RequestMapping(value = "/callLog", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<? extends MotechResponse> postCallLog(@RequestBody CallLogRequest callLogRequest) {
        Long callerId = callLogRequest.getCallerId();
        String sessionId = callLogRequest.getSessionId();
        String uniqueId = callLogRequest.getUniqueId();
        LOGGER.debug(String.format("Received bookmarkDto request for caller %s with session %s and uniqueId %s", callerId, sessionId, uniqueId));
        BasicResponse basicResponse = new BasicResponse(callerId, uniqueId, sessionId);
        ResponseStatus validationStatus = callLogRequest.validate();
        return validationStatus.isValid() ?
                callLogReporter.validateAndAddLog(callLogRequest, basicResponse) :
                new ResponseEntity<>(basicResponse.withResponse(validationStatus), OK);
    }
}
