package org.motechproject.whp.mtraining.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.whp.mtraining.reports.domain.CallDuration;

public interface CallDurationDataService extends MotechDataService<CallDuration> {

    @Lookup
    CallDuration findCallDurationById(@LookupField(name = "id") long id);
}
