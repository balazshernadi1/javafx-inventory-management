package com.balazsh.inventory.dao;

import com.balazsh.inventory.entity.Product;

import java.util.Collection;

/**
 * Product Data Access Object interface extending GenericDAO with product-specific operations.
 * Provides advanced search and filtering capabilities for inventory management including
 * category-based queries, attribute filtering, and product discovery functionality.
 */
public interface ProductDAO extends GenericDAO<Product> {

    /** Filters products by their category classification */
    Collection<Product> filterByCategory(String category);
    
    /** Filters products by their color attribute */
    Collection<Product> filterByColour(String colour);
    
    /** Filters products by cost range for price-based searches */
    Collection<Product> filterByCost(int min, int max);
    
    /** Filters products by their component specifications */
    Collection<Product> filterByComponents(String components);
    
    /** Searches products by name for text-based product discovery */
    Collection<Product> searchByName(String name);

}
