package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.domain.Provider;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public class Providers extends RepositorySupport<Provider> {


    @Transactional
    public Provider getByCallerId(Long callerId) {
        return filterByField("callerId", Long.class, callerId);
    }


    public Class getType() {
        return Provider.class;
    }
}
