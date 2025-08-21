package com.balazsh.inventory.dao;

import com.balazsh.inventory.entity.Permission;
import com.balazsh.inventory.entity.User;
import org.hibernate.Session;

import java.util.List;

/**
 * User Data Access Object implementation providing user-specific database operations.
 * Implements authentication queries, role-based permission retrieval, and administrative
 * user management functions including account approval and deactivation.
 */
public class UserDaoImpl extends GenericDAOAbs<User> implements UserDAO {

    public UserDaoImpl() {
        super(User.class);
    }

    /** Retrieves user by username for authentication using parameterized HQL query */
    @Override
    public User findUserByName(String username, Session session){
        return session.createQuery("from User where username = :username", User.class)
                .setParameter("username", username)
                .getSingleResult();
    }

    /** Retrieves all permissions for a user through role-permission joins */
    @Override
    public List<Permission> getAllUserPermissions(User user, Session session) {
        return session.createQuery("select distinct p from UserRole ur " +
                        "join ur.role r join r.rolePermissions rp join rp.permission p where ur.user = :user"
                        , Permission.class)
                .setParameter("user", user)
                .getResultList();
    }

    /** Retrieves all users awaiting administrative approval */
    @Override
    public List<User> getAllPendingUsers(Session session){
        return session.createQuery("from User u where u.accountStatus = :status", User.class).setParameter("status", "pending").list();
    }

    /** Soft delete: sets user status to disabled rather than physical deletion */
    @Override
    public void deleteUserById(int id, Session session) {
        session.createMutationQuery("update User u set u.accountStatus = 'disabled' where u.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    /** Activates pending user account by updating status to approved */
    @Override
    public void approveUserById(int id, Session session) {
        session.createMutationQuery("update User u set u.accountStatus = 'approved' where u.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }
}
