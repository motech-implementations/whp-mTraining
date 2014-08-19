package org.motechproject.whp.mtraining.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.whp.mtraining.domain.Flag;

import java.util.List;

/**
 * Interface for repository that persists simple records and allows CRUD.
 * MotechDataService base class will provide the implementation of this class as well
 * as methods for adding, deleting, saving and finding all instances.  In this class we
 * define and custom lookups we may need.
 */
public interface FlagDataService extends MotechDataService<Flag>{

    @Lookup
    Flag findFlagById(@LookupField(name = "moduleIdentifier") long id);

    @Lookup
    List<Flag> findFlagForUser(@LookupField(name = "moduleIdentifier") String externalId);
}
