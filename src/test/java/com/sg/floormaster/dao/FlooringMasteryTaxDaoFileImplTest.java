package com.sg.floormaster.dao;

import com.sg.floormaster.model.Tax;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FlooringMasteryTaxDaoFileImplTest {
    // add an implementation later for more exensive stateful testing.

    public FlooringMasteryTaxDaoFileImplTest() {}


    // Test that the map-validation functions correctly.
    @Test
    public void testCreateValidNonEmptyTaxDaoMemoryStorage() {

        // Arrange
        // create map with three valid tax code and unique names
        Map<String, Tax> nonEmptyTaxMap = new HashMap<>();
        Tax texas = new Tax("Texas",
                   "TX",
                        new BigDecimal("25").setScale(2, RoundingMode.HALF_UP));
        nonEmptyTaxMap.put(texas.getStateAbr(), texas);

        Tax california = new Tax("California",
                                "CA",
                                new BigDecimal("6.00").setScale(2, RoundingMode.HALF_UP));
        nonEmptyTaxMap.put(california.getStateAbr(), california);

        Tax kentucky = new Tax("Kentucky",
                              "KY",
                                new BigDecimal("9.25").setScale(2, RoundingMode.HALF_UP));
        nonEmptyTaxMap.put(kentucky.getStateAbr(), kentucky);

        // Act and assert - ensure that no exception is thrown when constructing taxDao with map injected in constructor
        try {
            FlooringMasteryTaxDao taxDao = new FlooringMasteryTaxDaoFileImpl(nonEmptyTaxMap);
        } catch (FlooringMasteryPersistenceException e) {
            fail("Expected creation of valid tax map threw Persistence Exception.");
        }
    }

    @Test
    public void testCreateValidEmptyTaxDaoMemoryStorage() {
        // Arrange - create empty map
        Map<String, Tax> emptyTaxMap = new HashMap<>();

        // Act and Assert
        // assert that no exception occurs when trying to create taxDao with valid empty map
        try {
            FlooringMasteryTaxDao taxDao = new FlooringMasteryTaxDaoFileImpl(emptyTaxMap);
        } catch (FlooringMasteryPersistenceException e) {
            fail("Instantiation of taxDao with valid empty Map of taxes shouldn't throw Exception.");
        }
    }

    @Test
    public void testCreateInvalidNonEmptyTaxDaoMemoryStorage() {
        // Arrange - create map with two entries
        // both have unique ID
        Map<String, Tax> invalidDuplicateStateMap = new HashMap<>();

        Tax texas = new Tax("Texas",
                "TX",
                new BigDecimal("25").setScale(2, RoundingMode.HALF_UP));
        invalidDuplicateStateMap.put(texas.getStateAbr(), texas);

        // add new tax with same name as another state.
        Tax california = new Tax("Texas",
                "CA",
                new BigDecimal("6.00").setScale(2, RoundingMode.HALF_UP));
        invalidDuplicateStateMap.put(california.getStateAbr(), california);

        Tax kentucky = new Tax("Kentucky",
                "KY",
                new BigDecimal("9.25").setScale(2, RoundingMode.HALF_UP));
        invalidDuplicateStateMap.put(kentucky.getStateAbr(), kentucky);

        // Act - try to create instance of map-injected TaxDao
        try {
            // Assert
            FlooringMasteryTaxDao invalidDao = new FlooringMasteryTaxDaoFileImpl(invalidDuplicateStateMap);
            fail("TaxDao should not be created with a map that contains taxes with duplicated state names.");
        } catch (FlooringMasteryPersistenceException e) {
            // Correct case - should throw Persistence exception.

        }
    }

    @Test
    public void testInvalidDaoCreationWithMisalignedStateCode() {
        // Arrange
        // Create map
        Map<String, Tax> invalidMisalignedStateCode = new HashMap<>();

        // Add a valid state code-tax pair:
        Tax kentucky = new Tax("Kentucky",
                "KY",
                new BigDecimal("9.25").setScale(2, RoundingMode.HALF_UP));
        invalidMisalignedStateCode.put(kentucky.getStateAbr(), kentucky);


        // add an invalid tax with a state code key that does not equal the Tax's state code.
        Tax texas = new Tax("Texas",
                "TX",
                new BigDecimal("25").setScale(2, RoundingMode.HALF_UP));
        invalidMisalignedStateCode.put("CA", texas); // Incorrect tax code.

        // Act - Test creation of TaxDao with injected map
        try {
            FlooringMasteryTaxDao invalidTaxDao = new FlooringMasteryTaxDaoFileImpl(invalidMisalignedStateCode);
            fail("state code key and tax element with different state code should be caught as invalid element.");
        } catch (FlooringMasteryPersistenceException e) {
            // Passed test - threw exception.
        }

    }

    @Test
    public void testGetAllTaxesFromEmptyMapInjection() {
        // Arrange - Create empty map
        Map<String, Tax> emptyTaxes = new HashMap<>();

        FlooringMasteryTaxDao emptyTaxDao = new FlooringMasteryTaxDaoFileImpl(emptyTaxes);

        List<Tax> receivedTaxes = emptyTaxDao.getAllTaxes();

        assertEquals(0, receivedTaxes.size(),  "TaxDao with no Tax elements should return list with no elements");
    }

//    @Test
//    public void testGetAllTaxesFromNullMapInjection() {
//        // Arrange - new TAxDao with null passed as parameter
//
//        // Act
//        try {
//            FlooringMasteryTaxDao emptyTaxDao = new FlooringMasteryTaxDaoFileImpl(null);
//            fail("Should not be able to initialise TaxDao with a null tax map parameter");
//        } catch (FlooringMasteryPersistenceException e) {
//            // Test passes - threw Exception.
//        }
//
//
//    }

    @Test
    public void testGetAllTaxesFromNonEmptyMapInjection() {
        // arrange
        // create non-empty map
        // Arrange
        // create map with three valid tax code and unique names
        Map<String, Tax> nonEmptyTaxMap = new HashMap<>();
        Tax texas = new Tax("Texas",
                "TX",
                new BigDecimal("25").setScale(2, RoundingMode.HALF_UP));
        nonEmptyTaxMap.put(texas.getStateAbr(), texas);

        Tax california = new Tax("California",
                "CA",
                new BigDecimal("6.00").setScale(2, RoundingMode.HALF_UP));
        nonEmptyTaxMap.put(california.getStateAbr(), california);

        Tax kentucky = new Tax("Kentucky",
                "KY",
                new BigDecimal("9.25").setScale(2, RoundingMode.HALF_UP));
        nonEmptyTaxMap.put(kentucky.getStateAbr(), kentucky);

        // Act:
        // Create TaxDao
        FlooringMasteryTaxDao taxDao = new FlooringMasteryTaxDaoFileImpl(nonEmptyTaxMap);
        // Get all taxes
        List<Tax> receivedTaxes = taxDao.getAllTaxes();

        // Assert
        // check not null
        assertNotNull(receivedTaxes, "List of all Taxes must not be null");

        // check size is as expected
        assertEquals(nonEmptyTaxMap.size(), receivedTaxes.size(),
                ("List of returned Taxes should have size " + nonEmptyTaxMap.size()));

        // Check that three taxes are contained within returned list
        assertTrue(receivedTaxes.contains(texas), "Returned taxes should contain Texas tax object");
        assertTrue(receivedTaxes.contains(california), "Returned taxes should contain California tax object");
        assertTrue(receivedTaxes.contains(kentucky), "Returned taxes should contain Kentucky tax object");


    }

}