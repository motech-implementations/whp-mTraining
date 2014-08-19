package org.motechproject.whp.mtraining.reports;

import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.reports.domain.CallDuration;
import org.motechproject.whp.mtraining.reports.domain.CallLog;
import org.motechproject.whp.mtraining.service.CallDurationService;
import org.motechproject.whp.mtraining.service.CallLogService;
import org.motechproject.whp.mtraining.web.domain.CallLogRecord;
import org.motechproject.whp.mtraining.web.domain.CallLogRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.motechproject.whp.mtraining.util.ISODateTimeUtil.parse;
import static org.motechproject.whp.mtraining.csv.domain.CallLogRecordType.from;

@Component
public class CallLogReporter {

    private CallLogService callLogService;
    private CallDurationService callDurationService;

    @Autowired
    public CallLogReporter(CallLogService callLogService, CallDurationService callDurationService) {
        this.callLogService = callLogService;
        this.callDurationService = callDurationService;
    }

    public void report(CallLogRequest callLogRequest, Provider provider) {
        String remedyId = provider.getRemediId();
        Long callerId = callLogRequest.getCallerId();
        String uniqueId = callLogRequest.getUniqueId();
        String sessionId = callLogRequest.getSessionId();
        callDurationService.createCallDuration(new CallDuration(remedyId, callerId, uniqueId, sessionId,
                parse(callLogRequest.getCallStartTime()),
                parse(callLogRequest.getCallEndTime())));
        if (isNotEmpty(callLogRequest.getCallLogRecords())) {
            for (CallLogRecord callLogRecord : callLogRequest.getCallLogRecords()) {
                String status = callLogRecord.getEndTime() != null ? "Completed" : "Started";
                CallLog callLog = new CallLog(provider.getRemediId(), callLogRequest.getCallerId(), callLogRequest.getUniqueId(),
                        callLogRequest.getSessionId(), callLogRequest.getCourseId(),
                        from(callLogRecord.getType()), parse(callLogRecord.getStartTime()), parse(callLogRecord.getEndTime()), status, callLogRecord.isRestarted());
                callLogService.createCallLog(callLog);
            }
        }
    }
}
