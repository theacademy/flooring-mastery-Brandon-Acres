package com.sg.floormaster.validation;

import com.sg.floormaster.dao.FlooringMasteryPersistenceException;
import com.sg.floormaster.model.Order;
import com.sg.floormaster.model.Product;
import com.sg.floormaster.model.Tax;
import com.sg.floormaster.service.FlooringMasteryInvalidInputException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderValidationTest {

    @Test
    public void testValidateOrderDate() {
        // arrange
        LocalDate dateToValidate = LocalDate.parse("2000-01-01");
        LocalDate beforeDateToValidate = LocalDate.parse("1999-01-01");
        LocalDate sameAsDateToValidate = dateToValidate;
        LocalDate afterDateToValidate = LocalDate.parse("2001-01-01");

        // Act and assert
        // 1. Should return orderDate if dateOfInput is before orderDate.
        // i.e. the date given is after a certain date (like .now()).
        assertEquals(dateToValidate, OrderValidation.validateOrderDate(dateToValidate, beforeDateToValidate));

        // 2. should throw exception if orderDate is before dateOfInput.
        try {
            OrderValidation.validateOrderDate(dateToValidate, afterDateToValidate);
            fail("A date supplied which is before the date to validate against should throw exception.");
        } catch (FlooringMasteryInvalidInputException e) {
            // passes - order should be invalid as it is before the date to check against.
        }

        // 3. Should throw exception if orderDate is on dateOfInput.

        try {
            OrderValidation.validateOrderDate(dateToValidate, sameAsDateToValidate);
            fail("A date supplied which is on the date to validate against should throw exception.");
        } catch (FlooringMasteryInvalidInputException e) {
            // passes - order should be invalid as it is on the date to check against.
        }

        // 4. if null date given, returns valid input date.
        assertEquals(dateToValidate, OrderValidation.validateOrderDate(dateToValidate, null),
                "validateOrderDate with null input should return input date.");
    }

    // ----------- test validateCustomerName

    @Test
    public void testValidateCustomerName() {

        // 1. Ensure that valid name containing lowercase, upper case, comma, whitespace and full stop
        // is allowed and returns the input string.

        String validString = "abcdefghijklmnopqrstuvwxyz. , 123456789"; // all valid characters used.
        assertEquals(validString, OrderValidation.validateCustomerName(validString),
                "validateCustomerName should return original string with valid input.");

        // 2. test invalid characters throw exception
        try {
            OrderValidation.validateCustomerName("not !! validA£$$%%%");
            fail("Invalid customer name should throw exception.");
        } catch (FlooringMasteryInvalidInputException e) {
            // passes, exception thrown.
        }

        // 3. test only whitespace throws exception - cannot be blank.
        try {
            OrderValidation.validateCustomerName("      ");
            fail("customer name cannot be whitespace only.");
        } catch (FlooringMasteryInvalidInputException e) {
            // passes.
        }

        // 4. test empty string throws exception.
        try {
            OrderValidation.validateCustomerName("");
            fail("Empty string is not valid customer name.");
        } catch (FlooringMasteryInvalidInputException e) {
            // pass
        }

        // 5. test null string throws exception.
        try {
            OrderValidation.validateCustomerName(null);
        } catch (FlooringMasteryInvalidInputException e) {
            // pass.
        }
    }

    // ------------ Test ValidateState ----------

    @Test
    public void testValidateStates() {
        // 1. if state code given and tax list contains tax with that state code, return valid state code.
        List<Tax> taxes = new ArrayList<>();
        taxes.add(new Tax("Texas", "TX" , new BigDecimal("55")));

        assertEquals("TX", OrderValidation.validateState(taxes, "TX"),
                "TX should be returned from list of taxes containing texas.");

        // 2. if tax list does not contain tax with given state code, should throw exception.
        try {
            OrderValidation.validateState(taxes, "PY");
            fail("Trying to get state PY in tax list that doesn't contain it should throw exception.");
        } catch (FlooringMasteryInvalidInputException e) {
            // passes
        }
    }

    // ------------- Test validateProductType -----------
    @Test
    public void testValidateProductType() {
        // 1. if product type exists in list of products, should return product type
        List<Product> products = new ArrayList<>();
        products.add(new Product("Linoliem", new BigDecimal("22"), new BigDecimal("44")));
        products.add(new Product("Vinyl", new BigDecimal("11.1"), new BigDecimal("10.99")));

        assertEquals("Vinyl", OrderValidation.validateProductType(products, "Vinyl"),
                "Vinyl should be valid product type.");

        // 2. product that doesn't exist in products throws exception
        try {
            OrderValidation.validateProductType(products, "Enigma");
            fail("validating 'Enigma' should throw exception.");
        } catch (FlooringMasteryInvalidInputException e) {
            // pass
        }
    }

    // ----------- Test validateArea ----------
    @Test
    public void testValidateArea() {
        // 1. valid area above minimum of 100 should return the input area.
        BigDecimal validArea = new BigDecimal("1000.00");

        assertEquals(validArea, OrderValidation.validateArea(validArea));

        // 2. Area of exactly 100.00 succeeds
        BigDecimal validAreaMin = new BigDecimal("100.00");
        assertEquals(validAreaMin, OrderValidation.validateArea(validAreaMin));

        // 3. 99.99 throws exception  - below min of 100
        BigDecimal invalidAreaBoundary = new BigDecimal("99.99");
        try {
            OrderValidation.validateArea(invalidAreaBoundary);
            fail("area of 99.99 should throw exception.");
        } catch (FlooringMasteryInvalidInputException e) {
            // passes.
        }

        // 3. negative area fails
        try {
            OrderValidation.validateArea(new BigDecimal("-10"));
            fail("Negative area should is not valid.");
        } catch (FlooringMasteryInvalidInputException e) {
            // passes
        }
    }

    // -------------- test validateTaxRate -----------
    @Test
    public void testValidateTaxRate() {
        // 1. order with state code that exists in tax list and has equal taxrate returns valid taxRate
        Order validOrder = new Order();
        validOrder.setTaxRate(new BigDecimal("25.00"));
        validOrder.setState("CA");

        // create list of taxes containing california.
        List<Tax> taxes = new ArrayList<>();
        taxes.add(new Tax("California", "CA", new BigDecimal("25.00")));

        // assert that validating validOrder returns its tax rate
        assertEquals(validOrder.getTaxRate(), OrderValidation.validateTaxRate(taxes, validOrder),
                "Order with tax rate equal to the tax rate of corresponding tax object in tax list should return the taxRate");

        // 2. order with state code that exists in tax list but has different tax rate throws exception.
        Order differentTaxRate = new Order();
        differentTaxRate.setTaxRate(new BigDecimal("25.50"));
        differentTaxRate.setState("CA");

        // assert exception thrown
        try {
            OrderValidation.validateTaxRate(taxes, differentTaxRate);
             fail("Order with valid state code but different tax rate should throw exception.");
        } catch (FlooringMasteryInvalidInputException e) {
            // passes
        } catch (FlooringMasteryPersistenceException e) {
            fail("invalid taxRate shouldn't throw Persistence Exception.");
        }

        // 3. Order with state code not in tax list throws invaildInputException
        Order notInTaxList = new Order();
        notInTaxList.setTaxRate(new BigDecimal("109"));
        notInTaxList.setState("TX");

        // assert invalid input thrown
        try {
            OrderValidation.validateTaxRate(taxes, notInTaxList);
            fail("order with state code not in taxes list should throw exception.");
        } catch (FlooringMasteryInvalidInputException e) {
            // passes.
        } catch (FlooringMasteryPersistenceException e) {
            fail("order with state code not in tax list shouldn't throw persistence exception.");
        }

        // 4. tax list with multiple tax objects with same state code should throw persistence exception.
        taxes.add(new Tax("Subsaharan Africa", "CA", new BigDecimal("99")));

        // assert persistence exception occurs when trying to get taxrate for CA
        try {
            OrderValidation.validateTaxRate(taxes, validOrder);
            fail("tax list with multple entries for same tax abbreviation should throw exeption.");
        } catch (FlooringMasteryPersistenceException e) {
            // passes
        } catch (FlooringMasteryInvalidInputException e) {
            fail("Incorrect exception thrown.");
        }
    }

    // --------- validateCostPerSquareFoot ----------

    @Test
    public void testValidateCostPerSquareFoot() {
        // 1. valid cost returned if cost matches the entry in products list
        List<Product> products = new ArrayList<>();
        products.add(new Product("Sugar", new BigDecimal("11.50"), new BigDecimal("12.90")));

        Order validOrder = new Order();
        validOrder.setCostPerSquareFoot(new BigDecimal("11.50"));
        validOrder.setProductType("Sugar");
        assertEquals(validOrder.getCostPerSquareFoot(), OrderValidation.validateCostPerSquareFoot(products, validOrder));

        // 2. if product exists with same product type in product list but has different costPerSquareFoot,
        // exception thrown.

        Order invalidOrder = new Order();
        invalidOrder.setCostPerSquareFoot(new BigDecimal("11111"));
        invalidOrder.setProductType("Sugar");

        // assert exception thrown
        try {
            OrderValidation.validateCostPerSquareFoot(products, invalidOrder);
            fail("cost per square foot is different from entry in products list, should throw exception.");
        } catch (FlooringMasteryInvalidInputException e) {
            // passes
        } catch (FlooringMasteryPersistenceException e) {
            // wrong exception
            fail("Shouldn't throw persistence exception.");
        }

        // 3. throws exception if no product found with same product tye
        invalidOrder.setProductType("Square");
        try {
            OrderValidation.validateCostPerSquareFoot(products, invalidOrder);
            fail("No product exists with same product type as order, should throw exception.");
        } catch (FlooringMasteryInvalidInputException e) {
            // passes
        } catch (FlooringMasteryPersistenceException e) {
            fail("Wrong exception thrown.");
        }
    }

    // --------- test validate Labor cost per square foot ------------
    // identical to approach for cost pers quare foot.
    @Test
    public void testValidateLaborCostPerSquareFoot() {
        // 1. valid cost returned if cost matches the entry in products list
        List<Product> products = new ArrayList<>();
        products.add(new Product("Sugar", new BigDecimal("11.50"), new BigDecimal("12.90")));

        Order validOrder = new Order();
        validOrder.setLaborCostPerSquareFoot(new BigDecimal("12.90"));
        validOrder.setProductType("Sugar");
        assertEquals(validOrder.getLaborCostPerSquareFoot(), OrderValidation.validateLaborCostPerSquareFoot(products, validOrder));

        // 2. if product exists with same product type in product list but has different laborCostPerSquareFoot,
        // exception thrown.

        Order invalidOrder = new Order();
        invalidOrder.setLaborCostPerSquareFoot(new BigDecimal("11111"));
        invalidOrder.setProductType("Sugar");

        // assert exception thrown
        try {
            OrderValidation.validateLaborCostPerSquareFoot(products, invalidOrder);
            fail("Labor cost per square foot is different from entry in products list, should throw exception.");
        } catch (FlooringMasteryInvalidInputException e) {
            // passes
        } catch (FlooringMasteryPersistenceException e) {
            // wrong exception
            fail("Shouldn't throw persistence exception.");
        }

        // 3. throws exception if no product found with same product type
        invalidOrder.setProductType("Square");
        try {
            OrderValidation.validateCostPerSquareFoot(products, invalidOrder);
            fail("No product exists with same product type as order, should throw exception.");
        } catch (FlooringMasteryInvalidInputException e) {
            // passes
        } catch (FlooringMasteryPersistenceException e) {
            fail("Wrong exception thrown.");
        }
    }



}