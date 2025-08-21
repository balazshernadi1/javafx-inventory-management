package com.balazsh.inventory.dao;

import com.balazsh.inventory.entity.Product;

import java.util.Collection;
import java.util.List;

/**
 * Product Data Access Object implementation providing product-specific database operations.
 * Currently contains placeholder implementations for advanced search and filtering features.
 * Extends GenericDAOAbs to inherit standard CRUD operations for Product entities.
 */
public class ProductDAOImpl extends GenericDAOAbs<Product> implements ProductDAO {

    public ProductDAOImpl() {
        super(Product.class);
    }

    @Override
    public Collection<Product> filterByCategory(String category) {
        return List.of();
    }

    @Override
    public Collection<Product> filterByColour(String colour) {
        return List.of();
    }

    @Override
    public Collection<Product> filterByCost(int min, int max) {
        return List.of();
    }

    @Override
    public Collection<Product> filterByComponents(String components) {
        return List.of();
    }

    @Override
    public Collection<Product> searchByName(String name) {
        return List.of();
    }
}
