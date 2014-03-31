package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.domain.Provider;
import org.motechproject.whp.mtraining.reports.domain.CallLog;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public class AllCallLogs extends RepositorySupport<CallLog> {

    public Class getType() {
        return Provider.class;
    }
}