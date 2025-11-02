package com.sg.floormaster.dao;

import com.sg.floormaster.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlooringMasteryOrderDaoFileImplTest {

    FlooringMasteryOrderDao testOrderDao;
    private final String TEST_ORDERS_DIRECTORY = "src/test/resources/Orders";

        // example order for stubbed memory implementation of order
    //        Map<LocalDate, Map<Integer, Order>> memoryOrders = new HashMap<>();
//        // create a single order entry
//        Order o1 = new Order();
//        o1.setOrderNumber(0);
//        o1.setOrderDate(LocalDate.now());
//        o1.setCustomerName("Doob");
//        o1.setState("California");
//        o1.setTax(new BigDecimal("11.2").setScale(2, RoundingMode.HALF_UP));
//        o1.setProductType("Yarn Ball");
//        o1.setArea(new BigDecimal("1113.2").setScale(2, RoundingMode.HALF_UP));
//        o1.setCostPerSquareFoot(new BigDecimal("1.2").setScale(2, RoundingMode.HALF_UP));
//        o1.setLaborCostPerSquareFoot(new BigDecimal("77.11").setScale(2, RoundingMode.HALF_UP));
//        o1.setMaterialCost(new BigDecimal("91.1").setScale(2, RoundingMode.HALF_UP));
//        o1.setLaborCost(new BigDecimal("1113.2").setScale(2, RoundingMode.HALF_UP));
//        o1.setTax(new BigDecimal("666.2").setScale(2, RoundingMode.HALF_UP));
//        o1.setTotal(new BigDecimal("8811.22").setScale(2, RoundingMode.HALF_UP));
//
//        Map<Integer, Order> doobDayOrders = new HashMap<>();
//        doobDayOrders.put(o1.getOrderNumber(), o1);
//        memoryOrders.put(o1.getOrderDate(), doobDayOrders);
//        FlooringMasteryOrderDao orderDao = new FlooringMasteryOrderDaoFileImpl(memoryOrders);


    // Start with an empty map before each test case.
    @BeforeEach
    void setUp() throws Exception {
        // create OrderDao with empty orders internal memory map:
        testOrderDao = new FlooringMasteryOrderDaoFileImpl(
                new HashMap<LocalDate, Map<Integer, Order>>());
        // ensures we always start with a valid empty test map in testOrderDao memory
    }


    // -------------------- Testing getOrdersForDate() ------------
    // In-memory Test Cases:

    // 1. orders.get(date) is null - either no date exists in outer map, or the date points to null value for internal Map
    // should return empty list in both cases.
    // 2. orders.get(date) is not null - an internal Map<Integer, Order> exists
    // 2a. the internal map is empty .values() is empty
    // 2b. the internal map contains orders .values() not empty.

    // 1. orders.get(date) is null
    @Test
    void testGetOrdersForDateEmptyAndNullMap() {


        // 1. orders.get(date) is null
        // Act: attempt to retrieve a list of orders for a date that isn't contained in the empty map:
        List<Order> receivedOrders = testOrderDao.getOrdersForDate(LocalDate.parse("2025-11-21"));

        assertEquals(0, receivedOrders.size(), "if an order date does not exist in the store, should return empty list.");

        // Arrange - create a new map with a date that points to null
        Map<LocalDate, Map<Integer, Order>> datePointsToNull = new HashMap<>();
        datePointsToNull.put(LocalDate.parse("2025-10-01"), null);
        // create new testOrderDao
        FlooringMasteryOrderDao datePointsToNullDao = new FlooringMasteryOrderDaoFileImpl(datePointsToNull);

        // act: retrieve orders for date that points to null:
        receivedOrders = datePointsToNullDao.getOrdersForDate(LocalDate.parse("2025-10-01"));
        assertEquals(0, receivedOrders.size(), "If an order date points to null in the store, should return empty list.");

    }

    @Test
    void testGetOrdersForDateInternalMapExists() {
        // 2. orders.get(date) is not null - an internal Map<Integer, Order> exists
        // 2a. the internal map is empty .values() is empty - returned list should be empty
        // 2b. the internal map contains ordres .values() not empty - returned list should be 1.

        // Case a. Arrange
        // create new orderDao containing map with one date and empty Map<Integer, Order>.
        Map<LocalDate, Map<Integer, Order>> singleDateWithEmptyOrderMap = new HashMap<>();
        LocalDate testDate = LocalDate.parse("2025-11-01");
        singleDateWithEmptyOrderMap.put(testDate, new HashMap<>());
        // create dao
        FlooringMasteryOrderDaoFileImpl testDao = new FlooringMasteryOrderDaoFileImpl(singleDateWithEmptyOrderMap);

        // act - getOrdersForDate() returns empty list
        assertEquals(0, testDao.getOrdersForDate(testDate).size(),
                "Date mapping to empty orders map should return list of size 0");

        // Case b.
        // Arrange
        // create new OrderDao with a date that points to non-empty map
        Map<LocalDate, Map<Integer, Order>> singleDateWithNonEmptyOrderMap = new HashMap<>();
        Map<Integer, Order> orderMap = new HashMap<>();
        // create order to add:
        Order o1 = new Order();
        o1.setOrderNumber(0);
        o1.setOrderDate(testDate);
        o1.setCustomerName("Doob");
        o1.setState("California");
        o1.setTax(new BigDecimal("11.2").setScale(2, RoundingMode.HALF_UP));
        o1.setProductType("Yarn Ball");
        o1.setArea(new BigDecimal("1113.2").setScale(2, RoundingMode.HALF_UP));
        o1.setCostPerSquareFoot(new BigDecimal("1.2").setScale(2, RoundingMode.HALF_UP));
        o1.setLaborCostPerSquareFoot(new BigDecimal("77.11").setScale(2, RoundingMode.HALF_UP));
        o1.setMaterialCost(new BigDecimal("91.1").setScale(2, RoundingMode.HALF_UP));
        o1.setLaborCost(new BigDecimal("1113.2").setScale(2, RoundingMode.HALF_UP));
        o1.setTax(new BigDecimal("666.2").setScale(2, RoundingMode.HALF_UP));
        o1.setTotal(new BigDecimal("8811.22").setScale(2, RoundingMode.HALF_UP));

        // add order to orderMap
        orderMap.put(o1.getOrderNumber(), o1);
        // add orderMap to outer map
        singleDateWithNonEmptyOrderMap.put(testDate, orderMap);

        // create dao
        testDao = new FlooringMasteryOrderDaoFileImpl(singleDateWithNonEmptyOrderMap);

        // test that received list of orders contains single value, and equal to o1.
        List<Order> receivedOrders = testDao.getOrdersForDate(testDate);

        assertEquals(1, receivedOrders.size(),
                "Order date pointing to order map with a single element should return list of size 1 from "
                    + "getOrdersForDate()");
        assertEquals(o1, receivedOrders.getFirst(), "received order should be equal to our test order.");
    }

    // file implementation tests

    @Test
    public void testGetOrdersForDateFromEmptyFile() {
        // getting orders from a date which has a corresponding valid file, but no order entries should return empty list.
        // source file: "src/test/resources/Orders/Orders_01012001.txt"
        // source directory: "src/test/resources/Orders"

        // create orderDao
        try {
            testOrderDao = new FlooringMasteryOrderDaoFileImpl("src/test/resources/Orders");
        } catch (FlooringMasteryPersistenceException e) {
            fail("Should be able to create order dao from src/test/resources/Orders directory.");
        }

        List<Order> receivedOrders = testOrderDao.getOrdersForDate(LocalDate.parse("2001-01-01"));

        // assert received list is empty.
        assertTrue(receivedOrders.isEmpty(), "Orders from empty source file should be empty.");
    }

    @Test
    public void testGetOrdersForDateFromNonExistentFile() {
        // getting orders for date which corresponds to no file in the orders directory should return empty list.
        // source directory: "src/test/resources/Orders"

        testOrderDao = new FlooringMasteryOrderDaoFileImpl(TEST_ORDERS_DIRECTORY);

        // create date for which there is no file
        LocalDate noFileDate = LocalDate.parse("2010-10-10");

        assertTrue(testOrderDao.getOrdersForDate(noFileDate).isEmpty(),
                "order list for data for which there is no file should be empty.");
    }

    @Test
    public void testGetOrdersForDateFromValidNonEmptyFile() {
        // get list containing two example orders from example orders file that is valid.

        testOrderDao = new FlooringMasteryOrderDaoFileImpl(TEST_ORDERS_DIRECTORY);

        // create the orders we expect to receive for date 06-01-2013
        // Ada Lovelace
        Order ada = new Order();
        ada.setOrderNumber(1);
        ada.setOrderDate(LocalDate.parse("2013-06-01"));
        ada.setCustomerName("Ada Lovelace");
        ada.setState("CA");
        ada.setProductType("Tile");
        ada.setTaxRate(new BigDecimal("25.00").setScale(2, RoundingMode.HALF_UP));
        ada.setArea(new BigDecimal("249.00").setScale(2, RoundingMode.HALF_UP));
        ada.setCostPerSquareFoot(new BigDecimal("3.50").setScale(2, RoundingMode.HALF_UP));
        ada.setLaborCostPerSquareFoot(new BigDecimal("4.15").setScale(2, RoundingMode.HALF_UP));
        ada.setMaterialCost(new BigDecimal("871.50").setScale(2, RoundingMode.HALF_UP));
        ada.setLaborCost(new BigDecimal("1033.35").setScale(2, RoundingMode.HALF_UP));
        ada.setTax(new BigDecimal("476.21").setScale(2, RoundingMode.HALF_UP));
        ada.setTotal(new BigDecimal("2381.06").setScale(2, RoundingMode.HALF_UP));

        // get orders for date
        List<Order> receivedOrders = testOrderDao.getOrdersForDate(LocalDate.parse("2013-06-01"));

        // assert:
        // list size should be 1
        assertEquals(1, receivedOrders.size(), "List size should be 1.");

        // liset should contain ada
        assertTrue(receivedOrders.contains(ada), "List of orders for 2013-06-01 should contain Ada.");

    }

    @Test
    public void testGetNextOrderNumberFromFiles() {
        // tests that the next order number returned from test/Orders returns correct order number.

        // maximum order number in src/test/resources/Orders is 3
        // next order number should return 4
        testOrderDao = new FlooringMasteryOrderDaoFileImpl(TEST_ORDERS_DIRECTORY);

        assertEquals(4, testOrderDao.getNextOrderNumber(), "Next order number should be 4.");
    }

    // ------------- Test addOrder() --------------

    @Test
    public void testAddOrderToExistingDate() {
        // test that adding order to a date that exists in orderDao's map works - it should appear if we call
        // getOrdersForDate after adding.


        // Arrange:
        LocalDate testDate = LocalDate.parse("2013-06-01");
        Order o1 = new Order();
        o1.setOrderNumber(4);
        o1.setOrderDate(testDate);
        o1.setCustomerName("Doob");
        o1.setState("California");
        o1.setTax(new BigDecimal("11.2").setScale(2, RoundingMode.HALF_UP));
        o1.setProductType("Yarn Ball");
        o1.setArea(new BigDecimal("1113.2").setScale(2, RoundingMode.HALF_UP));
        o1.setCostPerSquareFoot(new BigDecimal("1.2").setScale(2, RoundingMode.HALF_UP));
        o1.setLaborCostPerSquareFoot(new BigDecimal("77.11").setScale(2, RoundingMode.HALF_UP));
        o1.setMaterialCost(new BigDecimal("91.1").setScale(2, RoundingMode.HALF_UP));
        o1.setLaborCost(new BigDecimal("1113.2").setScale(2, RoundingMode.HALF_UP));
        o1.setTax(new BigDecimal("666.2").setScale(2, RoundingMode.HALF_UP));
        o1.setTotal(new BigDecimal("8811.22").setScale(2, RoundingMode.HALF_UP));

        // attempt to add order - should return null as no existing order exists with given ID and date
        testOrderDao = new FlooringMasteryOrderDaoFileImpl(TEST_ORDERS_DIRECTORY);

        assertNull(testOrderDao.addOrder(o1), "Adding order that isn't replacing another should return null.");

        // verify the order is added
        assertTrue(testOrderDao.getOrdersForDate(testDate).contains(o1), "Orders should now contain order for Doob");
    }

    @Test
    public void testAddOrderToNonExistingDate() {
        // test that adding order to a date that doesn't exist in orderDao's map works - it should appear if we call
        // getOrdersForDate after adding.


        // Arrange:
        LocalDate testDate = LocalDate.parse("2013-06-05");
        Order o1 = new Order();
        o1.setOrderNumber(4);
        o1.setOrderDate(testDate);
        o1.setCustomerName("Doob");
        o1.setState("California");
        o1.setTax(new BigDecimal("11.2").setScale(2, RoundingMode.HALF_UP));
        o1.setProductType("Yarn Ball");
        o1.setArea(new BigDecimal("1113.2").setScale(2, RoundingMode.HALF_UP));
        o1.setCostPerSquareFoot(new BigDecimal("1.2").setScale(2, RoundingMode.HALF_UP));
        o1.setLaborCostPerSquareFoot(new BigDecimal("77.11").setScale(2, RoundingMode.HALF_UP));
        o1.setMaterialCost(new BigDecimal("91.1").setScale(2, RoundingMode.HALF_UP));
        o1.setLaborCost(new BigDecimal("1113.2").setScale(2, RoundingMode.HALF_UP));
        o1.setTax(new BigDecimal("666.2").setScale(2, RoundingMode.HALF_UP));
        o1.setTotal(new BigDecimal("8811.22").setScale(2, RoundingMode.HALF_UP));

        // attempt to add order - should return null as no existing order exists with given ID and date
        testOrderDao = new FlooringMasteryOrderDaoFileImpl(TEST_ORDERS_DIRECTORY);

        assertNull(testOrderDao.addOrder(o1), "Adding order that isn't replacing another should return null.");

        // verify the order is added
        assertTrue(testOrderDao.getOrdersForDate(testDate).contains(o1), "Orders should now contain order for Doob");
    }

    // ----------- Test getOrder() -----------------

    @Test
    public void testGetOrderFromNonExistentDate() {
        // get order with a date that does not exist in orders store -> should return null

        // use default in-memory store from beforeEach
        assertNull(testOrderDao.getOrder(LocalDate.parse("2000-01-01"), 100),
                "getOrder() should return null when given an order that orderDao doesn't store.");
    }

    @Test
    public void testGetOrderFromExistingDateNonExistentID() {
        // get order from a date that orderDao does have orders for, but order ID doesn't exist - should return null.

        testOrderDao = new FlooringMasteryOrderDaoFileImpl(TEST_ORDERS_DIRECTORY);

        assertNull(testOrderDao.getOrder(LocalDate.parse("2013-06-01"), 100),
                "getOrder should return null when trying to retrieve order 100 on 06-01-2013.");
    }

    @Test
    public void testGetOrderFromExistingDateWithValidID() {
        // attempt to get Ada from Orders_06012013.txt

        testOrderDao = new FlooringMasteryOrderDaoFileImpl(TEST_ORDERS_DIRECTORY);

        Order ada = new Order();
        ada.setOrderNumber(1);
        ada.setOrderDate(LocalDate.parse("2013-06-01"));
        ada.setCustomerName("Ada Lovelace");
        ada.setState("CA");
        ada.setProductType("Tile");
        ada.setTaxRate(new BigDecimal("25.00").setScale(2, RoundingMode.HALF_UP));
        ada.setArea(new BigDecimal("249.00").setScale(2, RoundingMode.HALF_UP));
        ada.setCostPerSquareFoot(new BigDecimal("3.50").setScale(2, RoundingMode.HALF_UP));
        ada.setLaborCostPerSquareFoot(new BigDecimal("4.15").setScale(2, RoundingMode.HALF_UP));
        ada.setMaterialCost(new BigDecimal("871.50").setScale(2, RoundingMode.HALF_UP));
        ada.setLaborCost(new BigDecimal("1033.35").setScale(2, RoundingMode.HALF_UP));
        ada.setTax(new BigDecimal("476.21").setScale(2, RoundingMode.HALF_UP));
        ada.setTotal(new BigDecimal("2381.06").setScale(2, RoundingMode.HALF_UP));

        assertEquals(ada, testOrderDao.getOrder(ada.getOrderDate(), ada.getOrderNumber()),
                "getOrder() should return ada.");

    }

    // --------- test edit order -------------------

    @Test
    public void testEditOrderValidOrderChange() {
        // test that changing an order that exists in store results in getOrdersForDate() returning the order.
        // and that getOrder() returns the new order.

        testOrderDao = new FlooringMasteryOrderDaoFileImpl(TEST_ORDERS_DIRECTORY);

        Order newAda = new Order();
        newAda.setOrderNumber(1);
        newAda.setOrderDate(LocalDate.parse("2013-06-01"));
        newAda.setCustomerName("Ada Lovelace");
        newAda.setState("TX");
        newAda.setProductType("Carpet");
        newAda.setTaxRate(new BigDecimal("25.00").setScale(2, RoundingMode.HALF_UP));
        newAda.setArea(new BigDecimal("249.00").setScale(2, RoundingMode.HALF_UP));
        newAda.setCostPerSquareFoot(new BigDecimal("3.50").setScale(2, RoundingMode.HALF_UP));
        newAda.setLaborCostPerSquareFoot(new BigDecimal("4.15").setScale(2, RoundingMode.HALF_UP));
        newAda.setMaterialCost(new BigDecimal("871.50").setScale(2, RoundingMode.HALF_UP));
        newAda.setLaborCost(new BigDecimal("1033.35").setScale(2, RoundingMode.HALF_UP));
        newAda.setTax(new BigDecimal("476.21").setScale(2, RoundingMode.HALF_UP));
        newAda.setTotal(new BigDecimal("2381.06").setScale(2, RoundingMode.HALF_UP));

        // test dao doesn't throw exception when calling editOrder
        try {
            testOrderDao.editOrder(newAda);
        } catch (FlooringMasteryNoSuchOrderException e) {
            fail("editing Ada should be valid.");
        }

        // assert
        // getOrdersForDate() contains newAda
        assertTrue(testOrderDao.getOrdersForDate(newAda.getOrderDate()).contains(newAda),
                "getOrdersForDate() should contain newAda." );

        // getOrder returns newAda
        assertEquals(newAda, testOrderDao.getOrder(newAda.getOrderDate(), newAda.getOrderNumber()),
                "order dao should contain new ada.");
    }

    @Test
    public void testEditOrderWithInvalidID() {
        // edit order should throw NoSuchOrder exception if given order with an ID that isn't in the store.

        // arrange - create order with ID not in store
        testOrderDao = new FlooringMasteryOrderDaoFileImpl(TEST_ORDERS_DIRECTORY);
        Order invalidOrder = new Order();
        invalidOrder.setOrderNumber(-1000);

        // Act and Asert - ensure that editOrder throws exception
        try {
            testOrderDao.editOrder(invalidOrder);
            fail("edit Order should throw NoSuchOrderException if order given with ID not in order store.");
        } catch (FlooringMasteryNoSuchOrderException e) {
            // passes.
        }

    }

    @Test
    public void testEditOrderWithInvalidDate() {
        // edit order should throw NoSuchOrder exception if given order with an date that isn't in the store.

        // arrange - create order with date not in store
        testOrderDao = new FlooringMasteryOrderDaoFileImpl(TEST_ORDERS_DIRECTORY);
        Order invalidOrder = new Order();
        invalidOrder.setOrderDate(LocalDate.parse("1900-01-01"));

        // Act and Asert - ensure that editOrder throws exception
        try {
            testOrderDao.editOrder(invalidOrder);
            fail("edit Order should throw NoSuchOrderException if order given with date not in order store.");
        } catch (FlooringMasteryNoSuchOrderException e) {
            // passes.
        }
    }

    // ---------- test getAllOrders() ---------

    @Test
    public void testGetAllOrders() {

        Map<LocalDate, Map<Integer, Order>> ordersMap = new HashMap<>();

        // test that dao with empty orders map returns an equivalent empty orders map.
        assertEquals(ordersMap, testOrderDao.getAllOrders(), "empty order dao should return empty map of all orders.");


        // put some orders in the map, map returned should be equivalent.
        Map<Integer, Order> ordersOnADate = new HashMap<>();
        Order o1 = new Order();
        o1.setOrderDate(LocalDate.parse("2001-01-01"));
        o1.setOrderNumber(1);

        Order o2 = new Order();
        o2.setOrderDate(o1.getOrderDate());
        o1.setOrderNumber(2);
        // add to inner map
        ordersOnADate.put(o1.getOrderNumber(), o1);
        ordersOnADate.put(o2.getOrderNumber(), o2);

        // add inner map to ordersMap
        ordersMap.put(o1.getOrderDate(), ordersOnADate);

        // now add the same orders to the order dao
        testOrderDao.addOrder(o1);
        testOrderDao.addOrder(o2);

        // the map returned should be equivalent to ordersMap.
        assertEquals(ordersMap, testOrderDao.getAllOrders(),
                "test Dao's getAllOrders() should be equivalent to non-empty map.");
    }

    @Test
    public void testRemoveNonExistentOrder() {
        // ensure that removing an order that does not exist in store, returns null.

        // use empty map implementation from beforeEach

        assertNull(testOrderDao.getOrder(LocalDate.parse("2007-10-01"), 100),
                "Getting non existent order should return null.");

        // then ensure that removing an order with same date and ID returns null.
        assertNull(testOrderDao.removeOrder(LocalDate.parse("2007-10-01"), 100),
                "Removing non existent order should return null.");
    }

    @Test
    public void testRemoveValidOrder() {
        // Ensure that removing an order that is returned from getOrder() is removed from removeOrder()
        // use beforeEach() empty map implementation of orderDao

        // add a dummy order
        Order order = new Order();
        order.setOrderNumber(15);
        order.setOrderDate(LocalDate.parse("1874-01-01"));

        testOrderDao.addOrder(order);

        assertEquals(order, testOrderDao.getOrder(order.getOrderDate(), order.getOrderNumber()),
                "Should get dummy order.");

        assertEquals(order, testOrderDao.removeOrder(order.getOrderDate(), order.getOrderNumber()),
                "Removing dummy order should return the order.");

        // test that the resulting list of orders is empty.

        assertTrue(testOrderDao.getOrdersForDate(order.getOrderDate()).isEmpty(),
                "Orders on given date should be empty.");

        // test that you cannot retrieve the same order.
        assertNull(testOrderDao.getOrder(order.getOrderDate(), order.getOrderNumber()),
                "Should not be able to retrieve order after removing.");
    }






}