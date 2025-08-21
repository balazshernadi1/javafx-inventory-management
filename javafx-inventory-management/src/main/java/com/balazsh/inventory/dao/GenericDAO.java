package com.balazsh.inventory.dao;

import org.hibernate.Session;

import java.util.Collection;

/**
 * Generic Data Access Object interface defining standard CRUD operations.
 * Provides a common contract for database operations across all entity types
 * with session-based transaction management for consistency and flexibility.
 */
public interface GenericDAO <T> {

    /** Persists a single entity to the database */
    void save(T t, Session session);
    
    /** Persists multiple entities in a batch operation */
    void saveAll(Collection<T> collection, Session session);
    
    /** Retrieves an entity by its primary key identifier */
    T findById(Integer id, Session session);
    
    /** Retrieves all entities of the specified type */
    Collection<T> findAll(Session session);
    
    /** Removes an entity from the database */
    void delete(T t, Session session);
    
    /** Removes multiple entities in a batch operation */
    void deleteAll(Collection<T> collection, Session session);
    
    /** Updates an existing entity with modified values */
    void update(T t, Session session);

}
