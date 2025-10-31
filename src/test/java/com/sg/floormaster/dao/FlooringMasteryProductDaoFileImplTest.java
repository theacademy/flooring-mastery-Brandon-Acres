package com.sg.floormaster.dao;

import com.sg.floormaster.model.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FlooringMasteryProductDaoFileImplTest {
    // add instance for more stateful testing after implementing persistence

    public FlooringMasteryProductDaoFileImplTest() {}

    @Test
    public void testCreateValidEmptyMemoryProductDao() {
        // Arrange - create empty map
        Map<String, Product> emptyProducts = new HashMap<>();

        // Act - attempt to create ProductDao
        try {
            FlooringMasteryProductDao productDao = new FlooringMasteryProductDaoFileImpl(emptyProducts);
            // Test passes - valid creation
        } catch (FlooringMasteryPersistenceException e) {
            fail("Instantiation of ProductDao with valid empty Map of Products shouldn't throw Exception.");
        }
    }

    @Test
    public void testCreateValidNonEmptyMemoryProductDao() {
        // Arrange - create map and populate with valid entries
        Map<String, Product> nonEmptyProducts = new HashMap<>();

        Product carpet = new Product("Carpet",
                new BigDecimal("2.25").setScale(2, RoundingMode.HALF_UP),
                new BigDecimal("2.10").setScale(2, RoundingMode.HALF_UP));
        Product laminate = new Product("Laminate",
                new BigDecimal("1.75").setScale(2, RoundingMode.HALF_UP),
                new BigDecimal("2.10").setScale(2, RoundingMode.HALF_UP));
        Product tile = new Product("Tile",
                new BigDecimal("3.50").setScale(2, RoundingMode.HALF_UP),
                new BigDecimal("4.15").setScale(2, RoundingMode.HALF_UP));

        // add each to map
        nonEmptyProducts.put(carpet.getProductType(), carpet);
        nonEmptyProducts.put(laminate.getProductType(), laminate);
        nonEmptyProducts.put(tile.getProductType(), tile);

        // Act and assert - ensure no exception thrown as this is valid input
        try {
            FlooringMasteryProductDao productDao = new FlooringMasteryProductDaoFileImpl();
        } catch (FlooringMasteryPersistenceException e) {
            fail("Expected creation of valid product with non-empty memory storage should not throw exception.");
        }
    }

    @Test
    public void testInvalidDaoCreationWithMisalignedProductType() {
        // Arrange - create map and populate with valid entries and one invalid entry.
        Map<String, Product> nonEmptyProducts = new HashMap<>();

        Product carpet = new Product("Carpet",
                new BigDecimal("2.25").setScale(2, RoundingMode.HALF_UP),
                new BigDecimal("2.10").setScale(2, RoundingMode.HALF_UP));
        Product laminate = new Product("Laminate",
                new BigDecimal("1.75").setScale(2, RoundingMode.HALF_UP),
                new BigDecimal("2.10").setScale(2, RoundingMode.HALF_UP));
        Product tile = new Product("Tile",
                new BigDecimal("3.50").setScale(2, RoundingMode.HALF_UP),
                new BigDecimal("4.15").setScale(2, RoundingMode.HALF_UP));

        // add each to map
        nonEmptyProducts.put(carpet.getProductType(), carpet);
        nonEmptyProducts.put("flowers", laminate); // misaligned product type - should cause exception thrown later.
        nonEmptyProducts.put(tile.getProductType(), tile);

        // Act and assert - ensure no exception thrown as this is valid input
        try {
            FlooringMasteryProductDao productDao = new FlooringMasteryProductDaoFileImpl(nonEmptyProducts);
            fail("product should be pointed to with a productTye key equal to the product object's product type.");
        } catch (FlooringMasteryPersistenceException e) {
            // Passes - Identified misalignment between key productType and actual Product's productType
        }
    }

    // Memory implementation test
//    @Test
//    public void testInvalidDaoCreationWithNullMap() {
//        // Arrange/Act - attempt to create productDao with null reference to a map
//        try {
//            FlooringMasteryProductDao productDao = new FlooringMasteryProductDaoFileImpl(null);
//            fail("Should not be able to initialise ProductDao with null product map parameter.");
//        } catch (FlooringMasteryPersistenceException e) {
//            // test passes - threw Exception.
//        }
//    }
}