package org.motechproject.whp.mtraining.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.whp.mtraining.domain.CoursePlan;

/**
 * Interface for repository that persists simple records and allows CRUD.
 * MotechDataService base class will provide the implementation of this class as well
 * as methods for adding, deleting, saving and finding all instances.  In this class we
 * define and custom lookups we may need.
 */
public interface CoursePlanDataService extends MotechDataService<CoursePlan> {

    @Lookup
    CoursePlan findCourseByName(@LookupField(name = "name") String coursePlanName);

    @Lookup
    CoursePlan findCoursePlanById(@LookupField(name = "id") long id);


}
