package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.reports.domain.CallLog;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public class AllCallLogs extends RepositorySupport<CallLog> {

    public Class getType() {
        return CallLog.class;
    }

    @Transactional
    public void addAll(List<CallLog> callLogs) {
        bulkAdd(callLogs);
    }
}
