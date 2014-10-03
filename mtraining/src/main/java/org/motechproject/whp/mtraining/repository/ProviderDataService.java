package org.motechproject.whp.mtraining.repository;

import org.motechproject.mtraining.domain.ActivityRecord;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.whp.mtraining.domain.Provider;

import java.util.List;

/**
 * Interface for repository that persists simple records and allows CRUD.
 * MotechDataService base class will provide the implementation of this class as well
 * as methods for adding, deleting, saving and finding all instances.  In this class we
 * define and custom lookups we may need.
 */
public interface ProviderDataService extends MotechDataService<Provider> {

    @Lookup
    Provider findProviderById(@LookupField(name = "id") long id);

    @Lookup
    List<Provider> findProviderByCallerId(@LookupField(name = "callerId") Long callerId);

    @Lookup
    Provider findProviderByRemediId(@LookupField(name = "remediId") String remediId);
}
