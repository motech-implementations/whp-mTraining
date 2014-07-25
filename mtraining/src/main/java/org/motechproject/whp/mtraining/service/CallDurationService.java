package org.motechproject.whp.mtraining.service;

import org.motechproject.whp.mtraining.reports.domain.CallDuration;

import java.util.List;

public interface CallDurationService {
    
    CallDuration createCallDuration(CallDuration callDuration);

    CallDuration updateCallDuration(CallDuration callDuration);
    
    void deleteCallDuration (CallDuration callDuration);
    
    List<CallDuration> getAllCallDurations();

    CallDuration getCallDurationById(long id);
}
