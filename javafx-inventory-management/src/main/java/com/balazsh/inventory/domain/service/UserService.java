package com.balazsh.inventory.domain.service;

import com.balazsh.inventory.dao.UserDAO;
import com.balazsh.inventory.domain.model.UserEntry;
import com.balazsh.inventory.entity.User;
import com.balazsh.inventory.util.HibernateUtil;
import com.balazsh.inventory.util.exceptions.UserException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * User service handling business logic for user management operations.
 * Provides user account administration including pending user retrieval,
 * user approval, deletion, and entity-to-model mapping with proper
 * transaction management and error handling.
 */
public class UserService {

    private final UserDAO userDAO; // Data access for user operations

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /** Maps database User entities to UserEntry models for UI display */
    public List<UserEntry> mapUsers(List<User> users) {
        List<UserEntry> userEntries = new ArrayList<>();

        users.forEach(user -> {
            UserEntry userEntry = new UserEntry();
            userEntry.accountStatusProperty().set(user.getAccountStatus());

            // Extract first role from user's role collection
            user.getUserRoles().stream()
                .findFirst()
                .ifPresent(userRole -> userEntry.roleSelectedProperty().set(userRole.getRole().getRoleName()));
            
            userEntry.usernameProperty().set(user.getUsername());
            userEntry.idProperty().set(user.getId());

            userEntries.add(userEntry);
        });
        return userEntries;
    }

    /** Retrieves all users with pending approval status with transaction management */
    public List<UserEntry> getAllPendingUsers(){
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            List<User> users = userDAO.getAllPendingUsers(session);
            List<UserEntry> userEntries = mapUsers(users);

            tx.commit();
            return userEntries;

        }catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback(); {}
            throw new UserException("Error while getting pending users");
        }
    }

    /** Deletes user account by ID with transaction rollback on failure */
    public void deleteUser(int userId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            userDAO.deleteUserById(userId, session);
            tx.commit();
        }catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback(); {}
            throw new UserException("Error while deleting user");
        }
    }

    /** Approves user role and activates account with transaction management */
    public void approveUserRole(int userId){
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            userDAO.approveUserById(userId, session);
            tx.commit();
        }catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new UserException("Error while approving user role");
        }
    }

}
