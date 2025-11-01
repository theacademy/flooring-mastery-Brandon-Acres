package com.sg.floormaster.dao;

import com.sg.floormaster.model.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
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


    // ----- TestGetAllProducts --------
    @Test
    public void testGetAllProductsFromEmptyValidProductsFile() {
        // test the list returned from getAllProducts when supplied with a products file with valid header
        // but no product items has size zero.

        // Act and assert - ensure no exception thrown as this is valid input
        FlooringMasteryProductDao productDao = null;
        try {
            productDao =
                    new FlooringMasteryProductDaoFileImpl("src/test/resources/Data/EmptyProducts.txt");
        } catch (FlooringMasteryPersistenceException e) {
            // fail - valid header so dao should be created
            fail("Dao should be created from empty Products.txt with valid header.");
        }

        assertEquals(0, productDao.getAllProducts().size(),
                "Empty products file should return empty list of products");

    }

    @Test
    public void testGetAllProductsFromNonEmptyValidProductsFile() {
        // test list returned matches expected size from valid Products.txt file
        // check the products listed are equal the products we expect.

        Product carpet = new Product("Carpet",
                                    new BigDecimal("2.25").setScale(2, RoundingMode.HALF_UP),
                                    new BigDecimal("2.10").setScale(2, RoundingMode.HALF_UP));

        Product laminate = new Product("Laminate",
                                        new BigDecimal("1.75").setScale(2, RoundingMode.HALF_UP),
                                        new BigDecimal("2.10").setScale(2, RoundingMode.HALF_UP));


        FlooringMasteryProductDao productDao = null;
        // try to create dao:
        try {
            productDao =
                    new FlooringMasteryProductDaoFileImpl("src/test/resources/Data/TwoProducts.txt");
        } catch (FlooringMasteryPersistenceException e) {
            // fail - valid header and entries
            fail("Dao should be created from non-empty TwoProducts.txt with valid header.");
        }

        // assert:
        // get products:
        List<Product> receivedProducts = productDao.getAllProducts();
        // check getAllProducts() list has size 2
        assertEquals(2, receivedProducts.size(),
                "Product list should be of size 2 from TwoProducts.txt");

        // check that the products returned are equal to our expected products
        assertTrue(receivedProducts.contains(carpet),
                "product dao from TwoProducts.txt should contain carpet.");

        assertTrue(receivedProducts.contains(laminate),
                "product dao from TwoProducts.txt should contain laminate.");
    }


    // ----- Test Instantiation From Product text Files ---------
    @Test
    public void testCreateProductDaoWithValidProductFile() {
        // try to instantiate dao with valid products file - "src/test/resources/Data/Products.txt"
        try {
            FlooringMasteryProductDao validProductDao =
                    new FlooringMasteryProductDaoFileImpl("src/test/resources/Data/Products.txt");
        } catch (FlooringMasteryPersistenceException e) {
            fail("ProductDao should be able to parse valid Product info in Products.txt");
        }
    }

    @Test
    public void testCreateProductDaoWithEmptyProductsFile() {
        // try to instantiate product dao with products containing valid header and no data
        // source: "src/test/resources/Data/EmptyProducts.txt"

        try {
            FlooringMasteryProductDao validProductDao =
                    new FlooringMasteryProductDaoFileImpl("src/test/resources/Data/EmptyProducts.txt");
        } catch (FlooringMasteryPersistenceException e) {
            fail("ProductDao should be able to parse valid EmptyProducts.txt with no products, but valid header.");
        }
    }

    @Test
    public void testCreateProductDaoWithEmptyFile() {
        // try to instantiate product dao with empty product file - no header.
        // should return persistence exception - product file must contain valid header.
        // source: "src/test/resources/Data/EmptyFile.txt"

        try {
            FlooringMasteryProductDao validProductDao =
                    new FlooringMasteryProductDaoFileImpl("src/test/resources/Data/EmptyFile.txt");
            fail("ProductDao shouldn't be able to be instantiated from empty file with no header line.");
        } catch (FlooringMasteryPersistenceException e) {
            // passes test - shouldn't be able to create dao.
        }
    }

    @Test
    public void testCreateProductDaoWithInvalidHeaderFile() {
        // try to instantiate product dao with invalid headers
        // should return persistence exception - product file must contain valid header.
        // sources: "src/test/resources/Data/HeaderTooShortProducts.txt"
        //          "src/test/resources/Data/InvalidHeaderProducts.txt"

        try {
            FlooringMasteryProductDao validProductDao =
                    new FlooringMasteryProductDaoFileImpl("src/test/resources/Data/HeaderTooShortProducts.txt");
            fail("ProductDao shouldn't be able to be instantiated from header without full list of attributes.");
        } catch (FlooringMasteryPersistenceException e) {
            // passes test - shouldn't be able to create dao.
        }

        try {
            FlooringMasteryProductDao validProductDao =
                    new FlooringMasteryProductDaoFileImpl("src/test/resources/Data/InvalidHeaderProducts.txt");
            fail("ProductDao shouldn't be able to be instantiated from header without misspelt header.");
        } catch (FlooringMasteryPersistenceException e) {
            // passes test - shouldn't be able to create dao.
        }
    }

    @Test
    public void testCreateProductDaoFromNonExistentFile() {
        // try to instantiate product dao from non-existent file
        // should return persistence exception.

        try {
            FlooringMasteryProductDao invalidProductDao =
                    new FlooringMasteryProductDaoFileImpl("src/test/resources/Data/FileDoesNotExist.txt");
            fail("ProductDao shouldn't be able to be instantiated from non-existent file.");
        } catch (FlooringMasteryPersistenceException e) {
            // passes test - shouldn't be able to create dao.
        }
    }




}