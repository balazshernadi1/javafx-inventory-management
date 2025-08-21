package com.balazsh.inventory.domain.service;

import com.balazsh.inventory.dao.ProductDAO;
import com.balazsh.inventory.dao.UserDAO;
import com.balazsh.inventory.domain.model.ProductEntry;
import com.balazsh.inventory.entity.*;
import com.balazsh.inventory.entity.json.UniqueAttributes;
import com.balazsh.inventory.util.HibernateUtil;
import com.balazsh.inventory.util.exceptions.ProductProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.NoResultException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Product service handling business logic for inventory management operations.
 * Provides product transactions (buy/sell), stock management, data retrieval,
 * and reporting functionality.
 */
public class ProductService {

    private final ProductDAO productDAO; // Data access for product operations
    private final UserDAO userDAO; // Data access for user operations

    public ProductService(ProductDAO productDAO, UserDAO userDAO) {
        this.productDAO = productDAO;
        this.userDAO = userDAO;
    }

    /** Maps database query result objects to ProductEntry models for UI display */
    private List<ProductEntry> mapObjectListToProductEntryList(List<Object[]> productObjectList) {
        List<ProductEntry> productEntryList = new ArrayList<>();
        for (Object[] productObject : productObjectList) {
            ProductEntry productEntry = new ProductEntry();
            productEntry.setId((Integer) productObject[0]);
            productEntry.setName((String) productObject[1]);
            productEntry.setCategory((String) productObject[2]);
            productEntry.setNumberOfProductsAvailableForPurchase((Integer) productObject[3]);
            productEntry.setInStock((Integer) productObject[4]);
            productEntry.setImage((String) productObject[5]);
            productEntryList.add(productEntry);
        }
        return productEntryList;
    }

    /** Retrieves all products with stock and listing information for dashboard display */
    public List<ProductEntry> fetchProducts() {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Complex query joining products with stocks, listings, and images
            List<Object[]> productObjectList = session.createQuery(
                    "SELECT p.id, p.name, p.category.categoryName, " +
                            "SIZE(p.listings), s.quantity, i.filePath " +
                            "FROM Product p " +
                            "LEFT JOIN p.stocks s " +
                            "LEFT JOIN ProductImage pi ON pi.product.id = p.id " +
                            "LEFT JOIN pi.image i",
                    Object[].class
            ).getResultList();

            transaction.commit();
            return mapObjectListToProductEntryList(productObjectList);

        }catch (Exception e) {
            if (transaction != null && !transaction.isActive()) {
                transaction.rollback();
            }
            throw new ProductProcessingException("Unexpected error has occurred");
        }
    }

    /** Processes product purchase (restocking) with stock validation and order creation */
    public void buyProduct(int id, String orderMakerUser, int quantityRequested) {
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Product productToBuy = productDAO.findById(id, session);

            User user = userDAO.findUserByName(orderMakerUser, session);

            // Retrieve product stock information
            Stock stock = productToBuy.getStocks()
                    .stream()
                    .findFirst()
                    .orElseThrow(()->new ProductProcessingException("No stock found for product id: " + id));

            // Validate against maximum stock capacity
            if (quantityRequested > stock.getMaxStock()) {
                throw new ProductProcessingException("Max stock has been reached for product id: " + id);
            }

            // Update stock quantity (increase for purchase/restock)
            stock.setQuantity(stock.getQuantity() + quantityRequested);

            // Create purchase order record
            Order order = new Order();
            order.setProduct(productToBuy);
            order.setUser(user);
            order.setQuantity(quantityRequested);
            order.setCost(productToBuy.getCost());

            session.persist(order);
            session.merge(stock);

            transaction.commit();

        }catch (NoResultException e) {
            throw new ProductProcessingException("Unexpected error has occurred");
        }
    }

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

    /** Generates stock summary report for selected products and exports to file */
    public void printProductStockDetailsToFile(List<Integer> productIds) {
        List<Object[]> productStockDetails;
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();

            // Aggregate query for stock summary with financial calculations
            productStockDetails = session.createQuery(
                    "SELECT " +
                            "p.id, p.name, " +
                            "SUM(s.quantity), " +
                            "COALESCE(SUM(l.totalPrice), 0), " +
                            "SUM(s.quantity * p.cost) " +
                            "FROM Product p " +
                            "LEFT JOIN p.listings l " +
                            "LEFT JOIN p.stocks s " +
                            "WHERE p.id IN (:productIds) " +
                            "GROUP BY p.id, p.name", Object[].class)
                    .setParameterList("productIds", productIds)
                    .getResultList();

            transaction.commit();
        }catch (Exception e){
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new ProductProcessingException("Unexpected error has occurred");
        }
        writeProductStockDetailsToFile(productStockDetails);
    }

    /** Writes stock summary data to formatted text file */
    public void writeProductStockDetailsToFile(List<Object[]> productStockDetails){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("product_stock_details.txt"))){
            
            for (Object[] productStockDetail : productStockDetails) {
                StringBuilder stringBuilder = new StringBuilder();
                
                stringBuilder.append("Product Id: ").append(productStockDetail[0]).append("\n");
                stringBuilder.append("Name: ").append(productStockDetail[1]).append("\n");
                stringBuilder.append("Stock quantity: ").append(productStockDetail[2]).append("\n");
                stringBuilder.append("Total sale value: ").append(productStockDetail[3]).append("\n");
                stringBuilder.append("Total cost value: ").append(productStockDetail[4]).append("\n");
                stringBuilder.append("=".repeat(20)).append("\n");

                writer.write(stringBuilder.toString());
            }

        }catch (IOException e){
            throw new ProductProcessingException("Error while writing to file");
        }
    }

    /** Generates comprehensive product details report for selected products */
    public void printProductDetailsToFile(List<Integer> productIds) {
        List<Object[]> productDetails;
        Transaction transaction = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            
            // Query including product attributes and relationships
            productDetails = session.createQuery(
                    "select " +
                            "p.id,p.name, p.category.categoryName ,p.colour,p.cost,p.description, " +
                            "p.uniqueAttributes, SIZE(p.listings), SIZE(p.orders), s.quantity  " +
                            "from Product p left join p.stocks s where p.id in (:productIds)", Object[].class)
                    .setParameterList("productIds", productIds)
                    .getResultList();

            if (productDetails.isEmpty()){
                throw new ProductProcessingException("No products found for the given IDs");
            }
            transaction.commit();
        }catch (Exception e){
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new ProductProcessingException("Unexpected error has occurred");
        }
        writeToFile(productDetails);
    }

    /** Writes detailed product information to formatted text file with JSON attribute parsing */
    private void writeToFile(List<Object[]> productDetails) {
        ObjectMapper mapper = new ObjectMapper();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("product_details.txt"))) {

            for (Object[] productDetail : productDetails) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Product ID: ").append(productDetail[0]).append("\n");
                stringBuilder.append("Name: ").append(productDetail[1]).append("\n");
                stringBuilder.append("Category: ").append(productDetail[2]).append("\n");
                stringBuilder.append("Colour: ").append(productDetail[3]).append("\n");
                stringBuilder.append("Cost: ").append(productDetail[4]).append("\n");
                stringBuilder.append("Description: ").append(productDetail[5]).append("\n");
                
                // Handle unique attributes JSON parsing and formatting
                if (productDetail[6] != null) {
                    try {
                        UniqueAttributes uniqueAttributes = mapper.readValue(
                                productDetail[6].toString(), UniqueAttributes.class);
                        
                        // Format attributes
                        stringBuilder.append("Product Attributes:\n");
                        if (uniqueAttributes.getAttributeList() != null && !uniqueAttributes.getAttributeList().isEmpty()) {
                            uniqueAttributes.getAttributeList().forEach(attr -> {
                                stringBuilder.append("  - ").append(attr.getAttributeName())
                                           .append(": ").append(attr.getAttributeValue());
                                if (attr.getUnit() != null && !attr.getUnit().isEmpty()) {
                                    stringBuilder.append(" ").append(attr.getUnit());
                                }
                                stringBuilder.append(" (").append(attr.getDataType()).append(")\n");
                            });
                        } else {
                            stringBuilder.append("  No attributes\n");
                        }
                        
                        // Format components
                        stringBuilder.append("Product Components:\n");
                        if (uniqueAttributes.getComponentList() != null && !uniqueAttributes.getComponentList().isEmpty()) {
                            uniqueAttributes.getComponentList().forEach(comp -> {
                                stringBuilder.append("  - ").append(comp.getDisplayName())
                                           .append(" (ID: ").append(comp.getProductId()).append(")\n");
                            });
                        } else {
                            stringBuilder.append("  No components\n");
                        }
                        
                    } catch (Exception jsonException) {
                        stringBuilder.append("Product Attributes: Error parsing JSON - ")
                                   .append(productDetail[6]).append("\n");
                    }
                } else {
                    stringBuilder.append("Product Attributes: None\n");
                }
                
                stringBuilder.append("Number of Listings: ").append(productDetail[7]).append("\n");
                stringBuilder.append("Number of Orders: ").append(productDetail[8]).append("\n");
                stringBuilder.append("In stock: ").append(productDetail[9]).append("\n");
                stringBuilder.append("=".repeat(20)).append("\n");

                writer.write(stringBuilder.toString());
            }

        } catch (IOException e) {
            throw new ProductProcessingException("Failed to write product details to file");
        }
    }

}
