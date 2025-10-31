package com.sg.floormaster.dao;

import com.sg.floormaster.model.Order;

import java.time.LocalDate;
import java.util.*;

public class FlooringMasteryOrderDaoFileImpl implements FlooringMasteryOrderDao{

    private Map<LocalDate, Map<Integer, Order>> orders;

    // stores the largest order number that has been used for an order i.e. cannot be repeated.
    // note that the orders map may not yet contain an order with largestOrderNumber.
    // I.e. the largest order number stored in orders and largestOrderNumber are not directly tied.
    private int largestOrderNumber;

    // default constructor
    public FlooringMasteryOrderDaoFileImpl() {
        orders = new HashMap<>();
        largestOrderNumber = -1;

        // later: can load hashMap from file initially to initialise largestOrderNumber.
    }

    public FlooringMasteryOrderDaoFileImpl(Map<LocalDate, Map<Integer, Order>> orders) {
        this.orders = orders;
        // calculate max order number
        calculateLargestOrderNumber();

    }

    private void calculateLargestOrderNumber() {
        // uses in-memory map to set largest order number ready for incrementing.

        // if map is null or void, set largest number to 0
        if (orders == null || orders.isEmpty()) {
            largestOrderNumber = 0;
        }

        // otherwise: create List<List<Int>>, iterate through each and find max.
        int currentMax = -1;
        List<Set<Integer>> orderNumbers = orders.values().stream().map((Map::keySet)).toList();

        for (Set<Integer> s : orderNumbers) {
            List<Integer> sList = (new ArrayList<>(s));
            sList.sort(Integer::compare); // ascending order.
            int sMax = sList.getLast(); // max value
            if (sMax > currentMax) {
                currentMax = sMax;
            }
        }

        // now have currentMax as max order number.
        largestOrderNumber = currentMax;
    }


    @Override
    public int getNextOrderNumber() {
        // increment largestOrderNumber to the next unused order ID number
        largestOrderNumber += 1;
        // return the new unused orderID number
        return largestOrderNumber;
    }

    @Override
    public Order addOrder(Order order) {
        // assumes it is passed a valid order with a valid input from the service layer

        // in-memory implementation:

        // if date already exists in the orders map - add to inner map
        // note that date key may exist, but inner map could be null,
        //
        // in either case (date key not in outer map or key does exist but inner map is null),
        // a new map containing the new object is set as the value for the outer map.
        if (orders.get(order.getOrderDate()) != null) {
            Map<Integer, Order> existingOrdersOnNewOrderDate = orders.get(order.getOrderDate());

            // map of orders on this date already exists, we put new order in this map,
            // and return a previous order with the same orderId if one existed.
            return existingOrdersOnNewOrderDate.put(order.getOrderNumber(), order);
        }

        // otherwise no existing orders exist for this new order's date
        // create new inner map, append the new order, and append this to the outer map.
        Map<Integer, Order> newMapOnNewOrderDate = new HashMap<>();
        newMapOnNewOrderDate.put(order.getOrderNumber(), order);
        // append to orders Map
        orders.put(order.getOrderDate(), newMapOnNewOrderDate);
        // return null as no previous order existed
        return null;
    }

    @Override
    public Order getOrder(LocalDate date, int orderId) {
        // check if date exits:
        if (orders.get(date) == null) {
            return null; // no order can be found.
        }
        // otherwise return result of querying inner order map.
        return orders.get(date).get(orderId);
    }

    @Override
    public Order editOrder(Order newOrder) throws FlooringMasteryNoSuchOrderException {

        // First check if querying for order date returns null
        if (orders.get(newOrder.getOrderDate()) == null ||
            orders.get(newOrder.getOrderDate()).get(newOrder.getOrderNumber()) == null) {
            throw new FlooringMasteryNoSuchOrderException("Existing order with ID " + newOrder.getOrderNumber()
            + " not found.");
        }

        // otherwise replace existing order with new order
        return orders.get(newOrder.getOrderDate()).put(newOrder.getOrderNumber(), newOrder);
    }

    @Override
    public List<Order> getOrdersForDate(LocalDate date) {
        // Could convert to a stream?
        return new ArrayList<>(orders.get(date).values());
    }

    @Override
    public Map<LocalDate, Map<Integer, Order>> getAllOrders() {
        // Returns a shallow copy so that no external layer can alter the Dao's structure.
        // Note that the map should not be altered
        return Map.copyOf(orders);
    }

    @Override
    public Order removeOrder(LocalDate date, int orderId) {
        return null;
    }

    // load
        // make sure during marshalling/unmarshalling, replace order name's commas with special character like *, and put it back when you unmarshall.
}
