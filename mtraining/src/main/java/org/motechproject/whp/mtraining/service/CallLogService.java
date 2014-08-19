package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.reports.domain.CallLog;

import java.util.List;

public interface CallLogService {

    CallLog createCallLog(CallLog callLog);

    CallLog updateCallLog(CallLog callLog);

    void deleteCallLog (CallLog callLog);

    List<CallLog> getAllCallLog();

    CallLog getCallLogById(long id);
}
