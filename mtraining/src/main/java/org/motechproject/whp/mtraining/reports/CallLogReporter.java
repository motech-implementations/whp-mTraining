package org.motechproject.whp.mtraining.reports;

import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.reports.domain.CallDuration;
import org.motechproject.whp.mtraining.reports.domain.CallLog;
import org.motechproject.whp.mtraining.repository.AllCallDurations;
import org.motechproject.whp.mtraining.repository.AllCallLogs;
import org.motechproject.whp.mtraining.service.ProviderService;
import org.motechproject.whp.mtraining.web.domain.BasicResponse;
import org.motechproject.whp.mtraining.web.domain.CallLogRequest;
import org.motechproject.whp.mtraining.web.domain.Node;
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
    private AllCallDurations allCallDurations;

    @Autowired
    public CallLogReporter(ProviderService courseService, AllCallLogs allCallLogs, AllCallDurations allCallDurations) {
        this.providerService = courseService;
        this.allCallLogs = allCallLogs;
        this.allCallDurations = allCallDurations;
    }

    public ResponseEntity<BasicResponse> validateAndAddLog(CallLogRequest callLogRequest, BasicResponse basicResponse) {
        Long callerId = callLogRequest.getCallerId();
        Provider provider = providerService.byCallerId(callerId);
        if (provider == null)
            return new ResponseEntity<>(basicResponse.withResponse(UNKNOWN_PROVIDER), OK);
        String remediId = provider.getRemediId();
        String uniqueId = callLogRequest.getUniqueId();
        String sessionId = callLogRequest.getSessionId();
        allCallDurations.add(new CallDuration(remediId, callerId, uniqueId, sessionId, parse(callLogRequest.getCallStartTime()),
                parse(callLogRequest.getCallEndTime())));
        for (Node node : callLogRequest.getNodes()) {
            String status = node.getEndTime() != null ? "Completed" : "Started";
            CallLog callLog = new CallLog(remediId, callerId, uniqueId,
                    sessionId, node.getContentId(), node.getVersion(),
                    from(node.getType()), parse(node.getStartTime()), parse(node.getEndTime()), status,
                    node.getRestarted());
            allCallLogs.add(callLog);
        }
        return new ResponseEntity<>(basicResponse.withResponse(ResponseStatus.OK), OK);
    }
}
