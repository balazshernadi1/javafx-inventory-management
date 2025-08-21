package com.balazsh.inventory.domain.service;

import com.balazsh.inventory.dao.UserDAO;
import com.balazsh.inventory.domain.model.ActiveUserDetails;
import com.balazsh.inventory.entity.*;
import com.balazsh.inventory.util.exceptions.AuthenticationException;
import com.balazsh.inventory.util.HibernateUtil;
import com.balazsh.inventory.util.enums.OPERATION;
import com.balazsh.inventory.util.enums.RESOURCE;
import jakarta.persistence.NoResultException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Authentication service handling user login, registration, and session management.
 * Provides secure authentication with password validation, account status checking,
 * permission mapping.
 */
public class AuthService {

    private final UserDAO userDAO; // Data access for user operations

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /** Maps User entity and permissions to ActiveUserDetails for session management */
    private ActiveUserDetails mapToActiveUserDetails(User user, List<Permission> userPermissions) {
        // Group permissions by resource type with associated operations
        Map<RESOURCE, List<OPERATION>> permissions =
                userPermissions.stream()
                        .collect(Collectors.groupingBy(
                                p -> RESOURCE.valueOf(p.getResource().getResourceName().toUpperCase()),
                                Collectors.mapping(
                                        p -> OPERATION.valueOf(p.getOperation().getOperationName().toUpperCase()),
                                        Collectors.toList()
                                )
                        ));

        // Extract role names from user's role collection
        String[] roles = user.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getRoleName())
                .toArray(String[]::new);

        return new ActiveUserDetails(user.getUsername(), permissions, roles);
    }

    /** Authenticates user with password validation and account status verification */
    public ActiveUserDetails login(String username, String password){
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            User user = userDAO.findUserByName(username, session);

            // Validate password credentials
            if(!user.getPassword().equals(password)){
                throw new AuthenticationException("Invalid password");
            }

            // Check account activation and status
            if (user.getAccountStatus().equals("pending") || user.getAccountStatus().equals("disabled")) {
                throw new AuthenticationException("Your account is disabled or has not been activated yet");
            }

            // Retrieve user permissions for role-based access control
            List<Permission> userPermissions = userDAO.getAllUserPermissions(user, session);

            tx.commit();
            return mapToActiveUserDetails(user, userPermissions);

        }catch (NoResultException nr){
            throw new AuthenticationException("Invalid username");
        }catch (Exception e){
            throw new AuthenticationException("Unexpected error has occurred");
        }
    }

    /** Registers new user with role assignment and pending approval status */
    public void register(String username, String password, String role) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Check for existing username to prevent duplicates
            try {
                User existingUser = userDAO.findUserByName(username, session);
                if (existingUser != null) {
                    throw new AuthenticationException("Username already exists");
                }
            } catch (NoResultException ignored) {
                //This is what is required
            }

            // Retrieve role entity for assignment
            Role userRole = session.createQuery("from Role where roleName = :roleName", Role.class)
                    .setParameter("roleName", role)
                    .getSingleResult();

            // Create new user with pending status
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setAccountStatus("pending");

            userDAO.save(newUser, session);

            // Assign role to user
            UserRole userRoleEntity = new UserRole();
            userRoleEntity.setUser(newUser);
            userRoleEntity.setRole(userRole);

            session.persist(userRoleEntity);

            tx.commit();
        } catch (AuthenticationException e){
            if (tx != null) tx.rollback();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new AuthenticationException("Unexpected error has occurred");
        }
    }
}
