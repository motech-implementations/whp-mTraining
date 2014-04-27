package org.motechproject.whp.mtraining.repository;

import org.motechproject.whp.mtraining.domain.Provider;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.jdo.PersistenceManager;

@Repository
public class Providers extends RepositorySupport<Provider> {

    @Transactional
    public Provider getByCallerId(Long callerId) {
        return filterByField("callerId", Long.class, callerId);
    }

    public Class getType() {
        return Provider.class;
    }

    @Transactional
    public void addOrUpdate(Provider provider) {
        Provider providerFromDb = getByRemediId(provider.getRemediId());
        if (providerFromDb != null) {
            update(providerFromDb.getId(), provider);
        } else {
            add(provider);
        }
    }

    @Transactional
    public Provider getByRemediId(String remediId) {
        return filterByField("remediId", String.class, remediId);
    }

    @Transactional
    private void update(Long id, Provider newProvider) {
        PersistenceManager persistenceManager = getPersistenceManager();
        Provider oldProvider = persistenceManager.getObjectById(Provider.class, id);
        updateValues(oldProvider, newProvider);
        persistenceManager.close();
    }

    private void updateValues(Provider oldProvider, Provider newProvider) {
        oldProvider.setCallerId(newProvider.getCallerId());
        oldProvider.setLocation(newProvider.getLocation());
        oldProvider.setProviderStatus(newProvider.getProviderStatus());
    }
}
