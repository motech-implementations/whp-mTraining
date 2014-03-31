package org.motechproject.whp.mtraining.reports;

import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.reports.domain.CallLog;
import org.motechproject.whp.mtraining.repository.AllCallLogs;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.web.domain.BasicResponse;
import org.motechproject.whp.mtraining.web.domain.CallLogRequest;
import org.motechproject.whp.mtraining.web.domain.ResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static org.motechproject.mtraining.util.ISODateTimeUtil.parse;
import static org.motechproject.whp.mtraining.csv.domain.NodeType.from;
import static org.motechproject.whp.mtraining.web.domain.ResponseStatus.UNKNOWN_PROVIDER;
import static org.springframework.http.HttpStatus.OK;

@Component
public class CallLogReporter {

    private ProviderService providerService;
    private AllCallLogs allCallLogs;

    @Autowired
    public CallLogReporter(ProviderService courseService, AllCallLogs allCallLogs) {
        this.providerService = courseService;
        this.allCallLogs = allCallLogs;
    }

    public ResponseEntity<BasicResponse> validateAndAddLog(CallLogRequest callLogRequest, BasicResponse basicResponse) {
        Provider provider = providerService.byCallerId(callLogRequest.getCallerId());
        if (provider == null)
            return new ResponseEntity<>(basicResponse.withResponse(UNKNOWN_PROVIDER), OK);
        String status = callLogRequest.getEndTime() != null ? "Completed" : "Started";
        CallLog callLog = new CallLog(provider.getRemediId(), callLogRequest.getCallerId(), callLogRequest.getUniqueId(), callLogRequest.getSessionId(),
                callLogRequest.getNodeId(), callLogRequest.getNodeVersion(),
                from(callLogRequest.getNodeType()), parse(callLogRequest.getStartTime()), parse(callLogRequest.getEndTime()), status);
        allCallLogs.add(callLog);
        return new ResponseEntity<>(basicResponse.withResponse(ResponseStatus.OK), OK);
    }
}
