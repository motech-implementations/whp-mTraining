package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.domain.Module;
import org.springframework.stereotype.Repository;

@Repository
public class AllTestModules extends RepositorySupport<Module> {
    @Override
    Class getType() {
        return Module.class;
    }
}
