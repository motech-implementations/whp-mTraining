package org.motechproject.whp.mtraining.service.impl;

import org.motechproject.whp.mtraining.reports.domain.CallLog;
import org.motechproject.whp.mtraining.repository.CallLogDataService;
import org.motechproject.whp.mtraining.service.CallLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("callLogService")
public class CallLogServiceImpl implements CallLogService{

    @Autowired
    private CallLogDataService callLogDataService;
    
    @Override
    public CallLog createCallLog(CallLog callLog) {
        return callLogDataService.create(callLog);
    }

    @Override
    public CallLog updateCallLog(CallLog callLog) {
        return callLogDataService.update(callLog);
    }

    @Override
    public void deleteCallLog(CallLog callLog) {
        callLogDataService.delete(callLog);
    }

    @Override
    public List<CallLog> getAllCallLog() {
        return callLogDataService.retrieveAll();
    }

    @Override
    public CallLog getCallLogById(long id) {
        return callLogDataService.findCallLogById(id);
    }
}
