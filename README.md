# javafx-inventory-management

A JavaFX application which connects to a database via Hibernate and mimics inventory management features.

# Introduction

WheelWorks Management is a desktop JavaFX app for bicycle retailers to manage inventory (bikes and the parts that make them), built with Hibernate and backed by Azure SQL Server. Staff can view stock, purchase/order items, create sale listings, run reports, and search inventory. Role-based access control (RBAC) keeps permissions clear across employee types.

This project was created for an Object-Oriented Software Development module to demonstrate software design, software engineering, and database modelling skills learned in class.

# Key Features

•	Store and manage bikes and bike parts/accessories.
•	See the quantity of each product in stock that is stored or available for purchase.
•	Buy products to increase stock levels and sell products to decrease stock levels, while also logging each transaction.
•	Search for specific products by their name, category, or ID.
•	Generate single product reports with product details and stock level, or an all-stock report
•	Authentication and Authorisation by utilising some sort of Role-Based Access Control architecture.
•	Separation of duties by distinguishing different roles (e.g. employee and manager) and their respective permissions
•	Allow users with administrative roles to change users’ account status to either approved or disabled.

# Database

Data is stored in Azure SQL Server. The app relies on T-SQL features and is intended to run against Azure SQL

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

**The choosen approach**

I went with a hybrid option. I kept the Product table with common shared attributes, but defined an NVARCHAR column to store unique product-specific attributes in a JSON format.

The formatting :

```json
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





 

