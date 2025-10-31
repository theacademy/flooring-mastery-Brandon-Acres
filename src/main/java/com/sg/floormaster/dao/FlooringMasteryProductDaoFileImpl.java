package com.sg.floormaster.dao;

import com.sg.floormaster.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlooringMasteryProductDaoFileImpl implements FlooringMasteryProductDao {

    private Map<String, Product> allProducts; // productType as Key

    // Implement when adding persistence
    // private final String PRODUCT_FILE;
    // private final String DELIMITER;

    public FlooringMasteryProductDaoFileImpl() {}

    public FlooringMasteryProductDaoFileImpl(Map<String, Product> products) {
        validateAllProducts(products);
        this.allProducts = products;
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(allProducts.values());
    }

    // private loadFile()
        // make call to validateProducts

    private void validateAllProducts(Map<String, Product> products) throws FlooringMasteryPersistenceException {
        // we are guaranteed that keys ensure product types are unique strings
        // 1. Check for null reference
        if (products == null) {
            throw new FlooringMasteryPersistenceException("Internal store of products cannot be null.");
        }

        // 2. Ensure that all productType keys align with the productType of the corresponding Product object.
        for (Product p  : products.values()) {
            if (!p.equals(products.get(p.getProductType()))) {
                throw new FlooringMasteryPersistenceException("Cannot have a productType key mapping to a Product" +
                        " with a different productType");
            }
        }
    }
}
