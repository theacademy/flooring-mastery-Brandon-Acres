package com.sg.floormaster.dao;

import com.sg.floormaster.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlooringMasteryOrderDaoFileImplTest {

    FlooringMasteryOrderDao testOrderDao;

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


    // Testing getOrdersForDate
    // Test Cases:

    // 1. orders.get(date) is null - either no date exists in outer map, or the date points to null value for internal Map
    // should return empty list in both cases.
    // 2. orders.get(date) is not null - an internal Map<Integer, Order> exists
    // 2a. the internal map is empty .values() is empty
    // 2b. the internal map contains ordres .values() not empty.

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
}