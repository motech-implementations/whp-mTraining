package org.motechproject.whp.mtraining.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.whp.mtraining.domain.Location;

import java.util.List;

/**
 * Interface for repository that persists simple records and allows CRUD.
 * MotechDataService base class will provide the implementation of this class as well
 * as methods for adding, deleting, saving and finding all instances.  In this class we
 * define and custom lookups we may need.
 */
public interface LocationDataService extends MotechDataService<Location> {

    @Lookup
    Location findLocationById(@LookupField(name = "id") long id);

    @Lookup
    Location findStateByName(@LookupField(name = "state") String state,
                             @LookupField(name = "level") Integer level);

    @Lookup
    Location findBlockByName(@LookupField(name = "block") String block,
                             @LookupField(name = "level") Integer level);

    @Lookup
    List<Location> retrieveLocationsByLevel(@LookupField(name = "level") Integer level);
}