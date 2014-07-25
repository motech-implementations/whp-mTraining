package org.motechproject.whp.mtraining.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.whp.mtraining.reports.domain.CallLog;

public interface CallLogDataService extends MotechDataService<CallLog> {

    @Lookup
    CallLog findCallLogById(@LookupField(name = "id") long id);
}
