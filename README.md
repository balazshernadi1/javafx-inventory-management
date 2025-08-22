# javafx-inventory-management

A JavaFX application which connects to a database via Hibernate and mimics inventory management features.

# Introduction

This project was part of the Object-Oriented Software Development module, which required me to apply design and develop an inventory management system.

WheelWorks Management is a desktop application designed for internal use by employees in the bicycle retail industry to manage inventory.  The system focuses on managing two types of products: bikes and parts that make those bikes up. Employees can use the system to view stock levels, order and purchase stock, put stock up for sale, generate and view stock reports, and search for specific stock items. Furthermore, the application incorporates role-based access control to distinguish between employee roles and their permissions. Thus, creating a clear separation of duty for each type of employee.   

## Database

Data is stored in a relational database, more specifically in an Azure SQL Server. The application will not be able to retrieve and store data reliably without it being deployed on an Azure SQL Server. This is due to the use of T-SQL.

### Design

Below is a simple table that lists all entities within the database, along with a brief description of each entity.

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

Followed by the entity relationship diagram below.

<img width="1006" height="788" alt="image" src="https://github.com/user-attachments/assets/dc245931-7879-46c5-a60d-be83abba3e81" />

**A significant design choice**

A requirement was that there had to be at least two types of product stored in the database. 
Naturally, different products will have unique attributes, which left me with two options to implement them in the database.

1. Follow the Entity-Attribute-Value design

Initially, I thought this was a great idea as it gives full flexibility. However, once I started thinking about scalability and query performance, I realised it is not the ideal solution. 
I quickly found tons of articles about how this is a major anti-pattern and enterprise database destroyer.

2. One Table Per Product

Have a Product table with common attributes to all other products. Then, for each new product with unique attributes, create a new table referencing the Product table and declaring its unique attributes.
This solution is the most appropriate, given that I was working with a relational database. However it is not flexible, adding a new product requires creating a new table, removing an attribute and adding new attributes creates normalisation problems, and finally it can lead to nested table mess with cycles. 





 

