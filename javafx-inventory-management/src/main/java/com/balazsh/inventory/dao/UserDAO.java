package com.balazsh.inventory.dao;

import com.balazsh.inventory.entity.Permission;
import com.balazsh.inventory.entity.User;
import org.hibernate.Session;

import java.util.List;

/**
 * User Data Access Object interface extending GenericDAO with user-specific operations.
 * Provides authentication support, permission management, and administrative functions
 * for user account lifecycle management including approval and deactivation.
 */
public interface UserDAO extends GenericDAO<User> {

    /** Retrieves all permissions assigned to a user through their roles */
    List<Permission> getAllUserPermissions(User user, Session session);
    
    /** Finds a user by their unique username for authentication */
    User findUserByName(String username, Session session);
    
    /** Retrieves all users with pending approval status for administrative review */
    List<User> getAllPendingUsers(Session session);
    
    /** Deactivates a user account by setting status to disabled */
    void deleteUserById(int id, Session session);
    
    /** Approves a pending user account by updating their status */
    void approveUserById(int id, Session session);
}
