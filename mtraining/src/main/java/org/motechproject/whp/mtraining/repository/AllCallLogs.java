package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.reports.domain.CallLog;
import org.springframework.stereotype.Repository;


@Repository
public class AllCallLogs extends RepositorySupport<CallLog> {

    public Class getType() {
        return CallLog.class;
    }
}
