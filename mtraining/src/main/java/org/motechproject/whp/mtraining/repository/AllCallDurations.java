package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.reports.domain.CallDuration;
import org.springframework.stereotype.Repository;


@Repository
public class AllCallDurations extends RepositorySupport<CallDuration> {

    public Class getType() {
        return CallDuration.class;
    }
}
