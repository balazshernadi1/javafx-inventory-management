package com.balazsh.inventory.dao;

import org.hibernate.Session;
import java.util.Collection;

/**
 * Abstract base implementation of GenericDAO providing common CRUD operations.
 * Uses Hibernate session methods for database persistence and retrieval.
 * Concrete DAOs extend this class and inherit standard functionality while
 * adding entity-specific operations as needed.
 */
public abstract class GenericDAOAbs<T> implements GenericDAO<T> {

    private final Class<T> clazz; // Entity class type for generic operations

    protected GenericDAOAbs(Class<T> clazz) {
        this.clazz = clazz;
    }

    /** Persists entity using Hibernate session.persist() */
    @Override
    public void save(T t, Session session) {
        session.persist(t);
    }

    /** Batch save operation - currently not implemented */
    @Override
    public void saveAll(Collection<T> collection, Session session) {
    }

    /** Finds entity by primary key using session.find() */
    @Override
    public T findById(Integer id, Session session) {
        return session.find(clazz, id);
    }

    /** Updates entity using session.merge() for detached entities */
    @Override
    public void update(T t, Session session) {
        session.merge(t);
    }

    /** Retrieves all entities using HQL query based on class name */
    @Override
    public Collection<T> findAll(Session session) {
        String tableName = clazz.getSimpleName();
        return session.createQuery("from " + tableName, clazz).getResultList();
    }

    /** Removes entity using session.remove() */
    @Override
    public void delete(T t, Session session) {
        session.remove(t);
    }

    /** Batch delete operation - currently not implemented */
    @Override
    public void deleteAll(Collection<T> collection, Session session) {
    }
}
