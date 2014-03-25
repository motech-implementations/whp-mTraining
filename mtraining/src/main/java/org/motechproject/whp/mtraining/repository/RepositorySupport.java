package org.motechproject.whp.mtraining.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.jdo.FetchGroup;
import javax.jdo.FetchPlan;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
abstract public class RepositorySupport<T> {

    @Autowired
    protected PersistenceManagerFactory persistenceManagerFactory;

    //Required for proxy creation by cglib
    protected RepositorySupport() {
    }

    public RepositorySupport(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }

    @Transactional
    public T add(T persistenceCapable) {
        PersistenceManager persistenceManager = persistenceManagerFactory.getPersistenceManager();
        T persistent = persistenceManager.makePersistent(persistenceCapable);
        return persistenceManager.detachCopy(persistent);
    }

    @Transactional
    public List<T> all() {
        List<T> allItems = new ArrayList<>();
        PersistenceManager persistenceManager = getPersistenceManager();
        configureFetchGroup(persistenceManager.getFetchPlan());
        Query query = persistenceManager.newQuery(getType());
        Collection results = persistenceManager.detachCopyAll((List) query.execute("*"));
        for (Object result : results) {
            allItems.add((T) result);
        }
        return allItems;
    }

    @Transactional
    public List<T> allInOrder(String orderFieldName, String order) {
        List<T> allItems = new ArrayList<>();
        PersistenceManager persistenceManager = getPersistenceManager();
        Query query = persistenceManager.newQuery(getType());
        query.setOrdering(orderFieldName + " " + order);
        Collection results = persistenceManager.detachCopyAll((List) query.execute("*"));
        for (Object result : results) {
            allItems.add((T) result);
        }
        return allItems;
    }


    @Transactional
    public void deleteAll() {
        PersistenceManager persistenceManager = getPersistenceManager();
        Query query = persistenceManager.newQuery(getType());
        query.deletePersistentAll();
    }

    protected T filterByField(String fieldName, Class fieldType, Object filterValue) {
        PersistenceManager persistenceManager = getPersistenceManager();
        Query query = persistenceManager.newQuery(getType());
        query.setFilter(String.format("%s == filter", fieldName));
        query.declareParameters(String.format("%s filter", fieldType.getName()));
        query.setUnique(true);
        T result = (T) query.execute(filterValue);
        return persistenceManager.detachCopy(result);
    }


    protected PersistenceManager getPersistenceManager() {
        PersistenceManager persistenceManager = persistenceManagerFactory.getPersistenceManager();
        FetchPlan fetchPlan = persistenceManager.getFetchPlan();
        fetchPlan.setGroup(FetchGroup.ALL);
        return persistenceManager;
    }


    @Transactional
    public boolean delete(Long id) {
        boolean deleted = false;
        PersistenceManager persistenceManager = getPersistenceManager();
        Object objectById = persistenceManager.getObjectById(getType(), id);
        if (objectById != null) {
            persistenceManager.deletePersistent(objectById);
            deleted = true;
        }
        return deleted;
    }

    protected void configureFetchGroup(FetchPlan fetchPlan) {
        fetchPlan.setFetchSize(FetchPlan.FETCH_SIZE_GREEDY);
    }

    protected void setPersistenceManagerFactory(PersistenceManagerFactory persistenceManagerFactory) {
        this.persistenceManagerFactory = persistenceManagerFactory;
    }

    abstract Class getType();
}

