package com.sg.floormaster.service;

import com.sg.floormaster.dao.FlooringMasteryNoSuchOrderException;
import com.sg.floormaster.dao.FlooringMasteryPersistenceException;
import com.sg.floormaster.model.Order;
import com.sg.floormaster.model.Product;
import com.sg.floormaster.model.Tax;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlooringMasteryServiceLayerImplTest {
    private FlooringMasteryServiceLayer service;
    private Order adaClone;

    public FlooringMasteryServiceLayerImplTest() {
    }

    @BeforeEach
    public void setUp() {
        ApplicationContext ctx =
                new ClassPathXmlApplicationContext("applicationContext.xml");
        service = ctx.getBean("serviceLayer", FlooringMasteryServiceLayerImpl.class);

        adaClone = new Order();
        adaClone.setOrderNumber(1);
        adaClone.setOrderDate(LocalDate.parse("2013-06-01"));
        adaClone.setCustomerName("Ada Lovelace");
        adaClone.setState("TX");
        adaClone.setProductType("Carpet");
        adaClone.setTaxRate(new BigDecimal("4.45").setScale(2, RoundingMode.HALF_UP));
        adaClone.setArea(new BigDecimal("249.00").setScale(2, RoundingMode.HALF_UP));
        adaClone.setCostPerSquareFoot(new BigDecimal("2.25").setScale(2, RoundingMode.HALF_UP));
        adaClone.setLaborCostPerSquareFoot(new BigDecimal("2.10").setScale(2, RoundingMode.HALF_UP));
        adaClone.setMaterialCost(new BigDecimal("560.25").setScale(2, RoundingMode.HALF_UP));
        adaClone.setLaborCost(new BigDecimal("522.90").setScale(2, RoundingMode.HALF_UP));
        adaClone.setTax(new BigDecimal("43.33").setScale(2, RoundingMode.HALF_UP));
        adaClone.setTotal(new BigDecimal("1126.48").setScale(2, RoundingMode.HALF_UP));
    }

    // -------- Test add Order --------
    @Test
    public void testAddValidOrder() {
        // add a new order with valid properties and different ID to onlyOrder in orderDAo

        Order testOrder = adaClone;
        testOrder.setOrderNumber(10);


        try {
            service.addOrder(testOrder);
        } catch (FlooringMasteryDuplicateOrderException e) {
            fail("Valid order should be added.");
        }
    }

    @Test
    public void testAddInvalidOrder() {
        // create an order with tax and state invalid
        Order invalidOrder = new Order();
        invalidOrder.setOrderDate(LocalDate.parse("2010-01-01"));
        invalidOrder.setState("CA");
        invalidOrder.setProductType("Canvas");

        // should throw exception
        try {
            service.addOrder(invalidOrder);
            fail("Exception should be thrown when trying to add invalid order.");
        } catch (FlooringMasteryInvalidInputException | FlooringMasteryPersistenceException e) {
            // pass - correct exception.
        } catch ( FlooringMasteryDuplicateOrderException e) {
            fail("Incorrect exception thrown from adding invalid order.");
        }
    }

    @Test
    public void testAddOrderWithSameIDAsExisting() {
        // adding an order with the same ID as an existing order should throw
        // duplicateOrder exception

        Order validOrderWithSameID = adaClone;

        // assert that duplicateOrder exception thrown
        try {
            service.addOrder(validOrderWithSameID);
            fail("order with same ID as an order in order dao should throw DuplicateOrder exception.");
        } catch (FlooringMasteryDuplicateOrderException e) {
            // pass - correct exception
        } catch (FlooringMasteryInvalidInputException | FlooringMasteryPersistenceException e) {
            fail("Incorrect exception thrown.");
        }
    }

    // ------------- TestGetNextOrderNumber --------
    @Test
    public void testGetNextOrderNumber() {
        // Every Call to getNext Order Number should be unique.
        List<Integer> orderNumbersRetrieved = new ArrayList<>();

        int nextOrderNumber = 0;
        for (int i = 0; i < 100; i++) {
            // assert that next order number doesn't equal order number stored.
            nextOrderNumber = service.getNextOrderNumber();
            assertNotEquals(service.getOrder(LocalDate.parse("2013-06-01"), 1).getOrderNumber(),
                    nextOrderNumber,
                    "new order number should not equal an order number of an order in the order dao.");

            assertFalse(orderNumbersRetrieved.contains(nextOrderNumber),
                    "Order Number should not have been retrieved already.");

            orderNumbersRetrieved.add(nextOrderNumber);
        }
    }


    // --------- test GetOrder ---------
    @Test
    public void testGetOrder() {
        // Arrange
        // Clone of order in order dao stub:
        Order cloneOrder = adaClone;

        // assert that order received equals order clone
        assertEquals(cloneOrder, service.getOrder(cloneOrder.getOrderDate(), cloneOrder.getOrderNumber()),
                "getOrder should retrieve clone of order in order dao.");

        // assert that getting order that doesn't exist returns null
        assertNull(service.getOrder(LocalDate.parse("1010-01-01"), 11));
    }

    // ------- Test Edit Order --------
    @Test
    public void testEditValidOrder() {
        // adjust name of clone in order dao
        Order cloneOrder = adaClone;
        cloneOrder.setCustomerName("Steve McGee");

        // assert that editOrder doesn't throw exception - valid order edit.
        try {
            service.editOrder(cloneOrder);
            // passes
        } catch (Exception e) {
            fail("Valid editing of order shouldn't throw exception.");
        }
    }

    @Test
    public void testEditInvalidOrder() {
        // assert that an invalid order (with tax and state name not in tax and product daos for example)
        // triggers InvalidInputException.
        Order cloneOrder = adaClone;
        cloneOrder.setState("WA");
        cloneOrder.setProductType("Laminate");

        // assert InvalidInputException thrown
        try {
            service.editOrder(cloneOrder);
            fail("Replacing an order with an invalid order should throw InvalidInputException.");
        } catch (FlooringMasteryInvalidInputException e) {
            // pass - correct exception
        } catch (Exception e) {
            fail("Incorrect exception thrown.");
        }
    }

    @Test
    public void testEditNoSuchOrder() {
        // verify that if order dao doesn't have an existing order with date and ID of incoming order,
        // throws NoSuchOrderException
        Order invalidDateAndID = adaClone;
        invalidDateAndID.setOrderNumber(11);
        invalidDateAndID.setOrderDate(LocalDate.parse("2014-06-01"));

        // assert No Such Order exception thrown
        try {
            service.editOrder(invalidDateAndID);
            fail("No Such Order Exception should be thrown.");
        } catch (FlooringMasteryNoSuchOrderException e) {
            // passes - correct exception.
        } catch (Exception e) {
            fail("Incorrect exception thrown.");
        }
    }

    // --------------- Test GetOrdersForDate() ---------
    @Test
    public void testGetOrdersForDate() {
        // test that single list with correct order is returned from getOrdersForDate with valid date and id:
        Order cloneOrder = adaClone;

        // assert that returned list has size 1
        List<Order> receivedOrders = service.getOrdersForDate(cloneOrder.getOrderDate());
        assertEquals(1, receivedOrders.size(), "Received list of orders should contain one element.");

        // assert that returned list contains our cloned order
        assertTrue(receivedOrders.contains(cloneOrder), "Received orders should contain Ada.");
    }

    // ------------ test removeOrder() -----------
    @Test
    public void testRemoveValidOrder() {
        Order validOrder = adaClone;

        // assert that removed order is equal to our validOrder.
        assertEquals(validOrder, service.removeOrder(validOrder.getOrderDate(), validOrder.getOrderNumber()),
                "Removed order should be clone of Ada.");
    }

    @Test
    public void testRemoveInvalidOrder() {
        Order invalidOrder = adaClone;
        invalidOrder.setOrderNumber(3);;

        assertNull(service.removeOrder(invalidOrder.getOrderDate(), invalidOrder.getOrderNumber()),
                "Removing invalid order should return null.");

        invalidOrder.setOrderNumber(1);
        invalidOrder.setOrderDate(LocalDate.parse("1999-01-01"));

        assertNull(service.removeOrder(invalidOrder.getOrderDate(), invalidOrder.getOrderNumber()),
                "Removing order with invaild date should return null.");

    }


    //  ---------------------- Test getTaxes() -------------
    @Test
    public void testGetTaxes() {
        // we know tax list should contain single entry - assert it is equal.
        Tax taxClone = new Tax("Texas", "TX",
                new BigDecimal("4.45").setScale(2, RoundingMode.HALF_UP));

        List<Tax> receivedTaxes = service.getTaxes();

        assertEquals(1, receivedTaxes.size(),
                "Received list of taxes should be of size 1.");

        assertTrue(receivedTaxes.contains(taxClone), "Taxes should contain clone of tax in tax dao.");
    }

    // -------------- Test getProducts() ------------
    @Test
    public void testGetProducts() {
        // know product list has single entry - assert received list contains this product.
        Product productClone = new Product("Carpet",
                new BigDecimal("2.25").setScale(2, RoundingMode.HALF_UP),
                new BigDecimal("2.10").setScale(2, RoundingMode.HALF_UP));

        List<Product> recievedProducts = service.getProducts();

        assertEquals(1, recievedProducts.size(),
                "Received list of producst should contain single element.");

        assertTrue(recievedProducts.contains(productClone),
                "List of products should contain clone of product Dao's product.");

    }

    // ----------- Test calculateOrderCosts() ----------------
    @Test
    public void testCalculateOrderCostsOfValidOrder() {
        Order validOrder = adaClone;

        // create new valid order with uncalculated costs from validOrder
        Order orderToCalculate = new Order();
        orderToCalculate.setOrderNumber(validOrder.getOrderNumber());
        orderToCalculate.setOrderDate(validOrder.getOrderDate());
        orderToCalculate.setCustomerName(validOrder.getCustomerName());
        orderToCalculate.setState(validOrder.getState());
        orderToCalculate.setTaxRate(validOrder.getTaxRate());
        orderToCalculate.setProductType(validOrder.getProductType());
        orderToCalculate.setCostPerSquareFoot(validOrder.getCostPerSquareFoot());
        orderToCalculate.setLaborCostPerSquareFoot(validOrder.getLaborCostPerSquareFoot());
        orderToCalculate.setArea(validOrder.getArea());

        // we must then calculate materialcost, labor cost, tax, total.

        try {
            service.calculateOrderCosts(orderToCalculate, null); // assume it has been entered as a date in the future.
        } catch (Exception e) {
            fail("No exception should be thrown from calculating costs of valid order.");
        }

        // validate that the calculated costs are the same as valid calculations.
        assertEquals(validOrder.getMaterialCost(), orderToCalculate.getMaterialCost(),
                "Material cost should be equal to valid material cost.");
        assertEquals(validOrder.getLaborCost(), orderToCalculate.getLaborCost(),
                "Labor cost must be equal.");
        assertEquals(validOrder.getTax(), orderToCalculate.getTax(),
                "Tax must be equal.");
        assertEquals(validOrder.getTotal(), orderToCalculate.getTotal(),
                "Total must be equal.");
    }

    @Test
    public void testCalculateOrderCostsWithInvalidOrder() {
        Order invalidOrder = adaClone;

        // change tax and products to invalid
        invalidOrder.setProductType("String");
        invalidOrder.setState("XX");

        // assert that invalidInput exception or persistence exception thrown
        try {
            service.calculateOrderCosts(invalidOrder, null);
            fail("Should not be able to calculate order costs for invalid order.");
        } catch (FlooringMasteryPersistenceException | FlooringMasteryInvalidInputException e) {
            // passes
        }
    }

    @Test
    public void testSaveOrders() {
        // just verify that it doesn't throw exception
        // stubbed order dao doesn't actually persist data.
        try {
            service.saveOrders();
        } catch (FlooringMasteryPersistenceException e) {
            fail("Saving orders shouldn't throw persistence exception.");
        }
    }



}