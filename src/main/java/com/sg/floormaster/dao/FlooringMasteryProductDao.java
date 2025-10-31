package com.sg.floormaster.dao;

import com.sg.floormaster.model.Product;

import java.util.List;

public interface FlooringMasteryProductDao {

    /**
     * Get a list of all the Tax objects currently stored in the system
     * @return List of all Tax objects currently stored in the system.
     */
    List<Product> getAllProducts();
}
