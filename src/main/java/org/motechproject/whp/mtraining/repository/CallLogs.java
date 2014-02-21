package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.domain.CallLog;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CallLogs extends RepositorySupport<CallLog> {

    @Transactional
    public void record(CallLog callLog) {
        save(callLog);
    }

    @Override
    Class getType() {
        return CallLog.class;
    }
}
