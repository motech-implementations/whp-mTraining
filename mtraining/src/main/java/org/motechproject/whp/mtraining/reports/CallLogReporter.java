package org.motechproject.whp.mtraining.reports;

import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.reports.domain.CallDuration;
import org.motechproject.whp.mtraining.reports.domain.CallLog;
import org.motechproject.whp.mtraining.repository.AllCallDurations;
import org.motechproject.whp.mtraining.repository.AllCallLogs;
import org.motechproject.whp.mtraining.web.domain.CallLogRecord;
import org.motechproject.whp.mtraining.web.domain.CallLogRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.mtraining.util.ISODateTimeUtil.parse;
import static org.motechproject.whp.mtraining.csv.domain.CallLogRecordType.from;

@Component
public class CallLogReporter {

    private AllCallLogs allCallLogs;
    private AllCallDurations allCallDurations;

    @Autowired
    public CallLogReporter(AllCallLogs allCallLogs, AllCallDurations allCallDurations) {
        this.allCallLogs = allCallLogs;
        this.allCallDurations = allCallDurations;
    }

    public void report(CallLogRequest callLogRequest, Provider provider) {
        List<CallLog> callLogs = new ArrayList<>();
        String remediId = provider.getRemediId();
        Long callerId = callLogRequest.getCallerId();
        String uniqueId = callLogRequest.getUniqueId();
        String sessionId = callLogRequest.getSessionId();
        allCallDurations.add(new CallDuration(remediId, callerId, uniqueId, sessionId,
                parse(callLogRequest.getCallStartTime()),
                parse(callLogRequest.getCallEndTime())));
        for (CallLogRecord callLogRecord : callLogRequest.getCallLogRecords()) {
            String status = callLogRecord.getEndTime() != null ? "Completed" : "Started";
            CallLog callLog = new CallLog(provider.getRemediId(), callLogRequest.getCallerId(), callLogRequest.getUniqueId(),
                    callLogRequest.getSessionId(), callLogRecord.getContentId(), callLogRecord.getVersion(),
                    from(callLogRecord.getType()), parse(callLogRecord.getStartTime()), parse(callLogRecord.getEndTime()), status, callLogRecord.isRestarted());
            callLogs.add(callLog);
        }
        allCallLogs.addAll(callLogs);
    }
}
