package org.motechproject.whp.mtraining.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.whp.mtraining.domain.ManyToManyRelation;
import org.motechproject.whp.mtraining.domain.ParentType;

import java.util.List;

/**
 * Interface for repository that persists simple records and allows CRUD.
 * MotechDataService base class will provide the implementation of this class as well
 * as methods for adding, deleting, saving and finding all instances.  In this class we
 * define and custom lookups we may need.
 */
public interface ManyToManyRelationDataService extends MotechDataService<ManyToManyRelation> {

    @Lookup
    ManyToManyRelation findRelationById(@LookupField(name = "id") long id);

    @Lookup
    List<ManyToManyRelation> findRelations(@LookupField(name = "parentType") ParentType parentType,
                                           @LookupField(name = "parentId") Long parentId,
                                           @LookupField(name = "childId") Long childId);

}
