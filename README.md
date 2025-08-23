# javafx-inventory-management

A JavaFX application which connects to a database via Hibernate and mimics inventory management features.

# Introduction

WheelWorks Management is a desktop JavaFX app for bicycle retailers to manage inventory (bikes and the parts that make them), built with Hibernate and backed by Azure SQL Server. Staff can view stock, purchase/order items, create sale listings, run reports, and search inventory. Role-based access control (RBAC) keeps permissions clear across employee types.

This project was created for an Object-Oriented Software Development module to demonstrate software design, software engineering, and database modelling skills learned in class.

# Key Features

- Store and manage bikes and bike parts/accessories.
-	See the quantity of each product in stock that is stored or available for purchase.
-	Buy products to increase stock levels and sell products to decrease stock levels, while also logging each transaction.
-	Search for specific products by their name, category, or ID.
-	Generate single product reports with product details and stock level, or an all-stock report
-	Authentication and Authorisation by utilising some sort of Role-Based Access Control architecture.
-	Separation of duties by distinguishing different roles (e.g. employee and manager) and their respective permissions
-	Allow users with administrative roles to change users’ account status to either approved or disabled.

# Database

Data is stored in Azure SQL Server. The app relies on T-SQL features and is intended to run against Azure SQL.

## Modelling the database

Entities that were designed to be a part of the database.

| Entity            | Description                                                                      |
| ----------------- | -------------------------------------------------------------------------------- |
| `User`            | Stores user account information, credentials, and approval status.               |
| `Role`            | Defines user roles within the system for access control (e.g., Admin, User).     |
| `User_Role`       | Links users to their assigned roles in a many-to-many relationship.              |
| `Role_Permission` | Assigns a set of permissions to each role in a many-to-many relationship.        |
| `Permission`      | Defines a specific permission by combining a resource and an operation.          |
| `Resource`        | Lists system entities that can have permissions applied to them (e.g., Product). |
| `Operation`       | Defines actions that can be performed on resources (e.g., CREATE, DELETE).       |
| `ProductCategory` | Classifies products into different categories.                                   |
| `Product`         | Acts as the central table holding core, static information for every product.    |
| `Order`           | Records user order details, linking a user to the products they purchased.       |
| `Listing`         | Represents a product available for sale, including its price and quantity.       |
| `Stock`           | Tracks the inventory levels and stock details for each product.                  |
| `Product_Image`   | Associates one or more images with a specific product.                           |
| `Image`           | Stores the file path or reference for each image.                                |

ER diagram, showcasing entities defined above.

<img width="1006" height="788" alt="image" src="https://github.com/user-attachments/assets/dc245931-7879-46c5-a60d-be83abba3e81" />

### **A notable design choice**

**Requirement:** at least two product types stored in the database with distinct attributes.

To model this in the database, I evaluated two approaches:

1. Entity-Attribute-Value (EAV)

At first, EAV seemed attractive due to its complete flexibility. However, once I started thinking on a realistic product scale, it quickly made me realise that it is problematic for scalability, query complexity, constraints, and indexing
A quick search for my idea revealed to me that this is a major anti-pattern, a company-destroying anti-pattern. Thus, I dropped this idea, *partially*.

2. Table-Per-Product

A normalised Product table with shared attributes, alongside subtype tables (Bike, Brake) for unique attributes, linked 1-to-1 to the Product table. 
This solution seemed to have the most integrity for a relational database. However, adding a new product would've required the creation of a new table with its own unique attributes. Furthermore, adding and removing attributes would've broken the simplest normalisation rules. Finally, over time, deeply nested relationships accumulate.

**The chosen approach**

I went with a hybrid option. I kept the Product table with common shared attributes, but defined an NVARCHAR column to store unique product-specific attributes in a JSON format.

The formatting :

```
{
  attribute_list [
     name
     value
     unit
     data_type
  ]
  component_list[
     product_id
     display_name
  ]
}
```

*Why does this work here?*
- The workload is read-heavy with infrequent writes (apart from initialisation), so JSON reads are acceptable as it is greatly supported in T-SQL.
- Preserves flexibility without going full EAV. 

*Why could it be an issue*
- JSON column becomes a junk drawer, and complex entities with many attributes create a large JSON.
- Complex queries utilising the contents of the JSON column may become too large and unreadable.
- Breaks relational integrity.

**A better solution for the future**

Keep the Product table for shared attributes and create appropriate subtype tables for complex entities, e.g. Brake, Motor, Gear. Define the common attributes for each entity, but leave an NVARCHAR column for truly unique attributes.
This would result in greater relational integrity and would make querying less complex.

# Software Architecture

I have chosen a layered approach, which might seem like overengineering for the size of the project, but it has taught me a lot. This allowed me to create a modular application that is extensible.

## Data Access Object (DAO) layer

The purpose of the DAO layer is to decouple the application/business layer from the persistence layer. DAOs would only be responsible for persistence-related operations, in this case using Hibernate's Sessions and JPA-defined entities.
This allows the persistence layer to evolve and change over time (e.g. new data sources added) without affecting the business/application layer logic.

Class diagram showcasing the DAO layer.
<img width="940" height="792" alt="image" src="https://github.com/user-attachments/assets/1803a273-6f40-4d3c-89fa-c9c808872daf" />

The core idea is to have the GenericDAO define a shared interface and an abstract implementation for common database operations. While concrete DAOs (e.g. UserDAOImpl) inherit the GenericDAO operations defined and also implement their own entity-specific operations

**GenericDAO**
```java
**
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
```

**UserDAO**
```java
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
```

As seen on the diagram before, there are only two concrete implementations, the ProductDAOImpl and UserDAOImpl. 
Unfortunately, due to time constraints, I wasn't able to fully decouple other database-specific operations from the service layer into their own respective DAOs.

## Service layer

The service layer sits between the UI and external systems, mediating all application workflows (UI ↔ Service ↔ External). Each service has a single, well-defined responsibility—for example, an AuthService for authentication or a ProductService for product operations. Services coordinate any required DAOs and manage transactions, while shielding the UI from persistence concerns. They never expose persistence models (e.g., JPA entities) to the UI; instead, they return purpose-built Data Transfer Objects (DTOs) that aggregate only the data the UI needs.

Class diagram showcasing the service layer:
<img width="940" height="860" alt="image" src="https://github.com/user-attachments/assets/293087df-cc43-4a38-9214-68cb6b277abb" />

**Key responsibilities**
- Enforce business rules (stock limits, listing states, permissions checks upstream).
- Orchestrate multiple DAOs in a single unit of work.
- Translate persistence exceptions into domain exceptions.
- Provide read models for the UI (e.g., mapping Object[] → ProductEntry).

### Example 1: selling a product (creates listing + reduces stock)
Below is a trimmed version of ProductService.sellProduct(...), which validates stock levels, creates a sales listing, and updates inventory in one transaction. It also returns domain-specific exceptions with clear messages that can be displayed to the user.

```java
/** Processes product sale with stock validation and listing creation */
    public void sellProduct(int id, String saleMakerUser, int quantityRequested, int price) {
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Product productToSell = productDAO.findById(id, session);

            User user = userDAO.findUserByName(saleMakerUser, session);

            // Retrieve product stock information
            Stock stock = productToSell.getStocks()
                    .stream()
                    .findFirst()
                    .orElseThrow(()->new ProductProcessingException("No stock found for product id: " + id));

            // Validate sufficient stock for sale
            if (stock.getQuantity() <= quantityRequested) {
                throw new ProductProcessingException("Insufficient stock quantity for product id: " + id
                        + "Available quantity: " + stock.getQuantity() + ", Requested: " + quantityRequested);
            }

            // Create sales listing with pricing information
            Listing listing = new Listing();
            listing.setProduct(productToSell);
            listing.setQuantity(quantityRequested);
            listing.setUnitPrice(price);
            listing.setTotalPrice(price * quantityRequested);
            listing.setListingStatus("active");
            listing.setListedBy(user);

            // Update stock quantity (decrease for sale)
            stock.setQuantity(stock.getQuantity() - quantityRequested);

            session.merge(stock);
            session.persist(listing);
            session.flush();

            transaction.commit();

        }catch (NoResultException e) {
            throw new ProductProcessingException("Unexpected error has occurred");
        }
    }
```

## User-Interface and User Interaction Handling

I utilised the Model-View-Controller-Interactor (MVCI) framework, devised by [Dave Barrett](https://www.pragmaticcoding.ca/javafx/Mvci-Introduction).

In each MVCI, the following are true:
-	The Model layer represents the application state at a given point, it doesn’t contain any logic, just JavaFX properties that are bindable. It is completely unaware of any other components.
-	The View layer is responsible for creating the User-Interface (UI) and handling user interactions, it holds a reference to the Model and may bind properties of the Model to UI specific components. It is unaware of any sort of business logic.
-	The Interactor, as mentioned above, encapsulates all business logic and business service communication, it holds a reference to the Model to manipulate its properties. It is unaware of how the data may be represented.
-	The Controller is the main orchestrator. It instantiates all other components and is responsible for coordinating processes between the components. In a Multi-MVCI application, it may also hold a reference to other MVCI controllers.

This project is a Multi-MVCI application, meaning that a MVCI (DashBoard MVCI) is encapsulated within another MVCI (MainMVCI). 

See the class diagram below for a visualisation of such encapsulation. The diagram clearly shows that the ProductMVCI and UserMVCI are their own complete applications, which are then encapsulated within the DashboardMVCI.
<img width="940" height="577" alt="image" src="https://github.com/user-attachments/assets/6a257e04-7e02-4a57-821a-22a82c35f525" />

# Reflection

This project was fun. It pushed me to research, experiment, and learn. The codebase isn’t amazing, but the progress is. Evaluating trade-offs, studying real projects, and testing ideas drove real growth and set me up to build better things next time.

# Screenshots

**Product Page**
<img width="1516" height="786" alt="image" src="https://github.com/user-attachments/assets/332f9562-e348-48fa-ae9c-0dac0a5c707c" />

**User Page**
<img width="1515" height="787" alt="image" src="https://github.com/user-attachments/assets/b4a6d7b4-7d11-479d-9506-b3d64665625a" />

**Register Page**
<img width="1513" height="784" alt="image" src="https://github.com/user-attachments/assets/ecd999e6-cdff-4382-a993-40ef3e295151" />


















 

