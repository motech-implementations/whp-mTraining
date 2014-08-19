package org.motechproject.whp.mtraining.service.impl;


import org.motechproject.whp.mtraining.reports.domain.CallDuration;
import org.motechproject.whp.mtraining.repository.CallDurationDataService;
import org.motechproject.whp.mtraining.service.CallDurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("callDurationService")
public class CallDurationServiceImpl implements CallDurationService {

    @Autowired
    private CallDurationDataService callDurationDataService;
    
    @Override
    public CallDuration createCallDuration(CallDuration callDuration) {
        return callDurationDataService.create(callDuration);
    }

    @Override
    public CallDuration updateCallDuration(CallDuration callDuration) {
        return callDurationDataService.update(callDuration);
    }

    @Override
    public void deleteCallDuration(CallDuration callDuration) {
        callDurationDataService.delete(callDuration);
    }

    @Override
    public List<CallDuration> getAllCallDurations() {
        return callDurationDataService.retrieveAll();
    }

    @Override
    public CallDuration getCallDurationById(long id) {
        return callDurationDataService.findCallDurationById(id);
    }
}
