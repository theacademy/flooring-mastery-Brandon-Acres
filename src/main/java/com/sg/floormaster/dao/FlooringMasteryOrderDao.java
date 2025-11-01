package com.sg.floormaster.dao;

import com.sg.floormaster.model.Order;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FlooringMasteryOrderDao {

    /**
     * Returns next available orderNumber such that it will be unique
     * Next order number will be one greater than the current largest order number stored.
     *
     * @return int new unique ID number that no existing order shares.
     */
    int getNextOrderNumber();

    /**
     * Adds given order to store of orders. Replaces existing order with same order Id.
     *
     * If there is already an order with the same id, it will return that Order object,
     * otherwise will return null.
     *
     * @param order order object to be added to store.
     * @return the Order object previously associated with the incoming Order's id.
     */
    Order addOrder(Order order);

    /**
     * Returns the Order associated with the given order date and order ID
     *
     * returns null if no such order exists
     * @param date Order date of the order to retrieve
     * @param orderId Order ID of the order to retrieve.
     * @return the Order object associated with the given date and id, null if no order exists.
     */
    Order getOrder(LocalDate date, int orderId);

    /**
     * Edits the existing order that has the same order ID as the given Order if one exists.
     *
     * If an existing order has the same order ID as the incoming Order, it will replace the order
     * If no existing order for the same order ID and date currently exists, will throw NoSuchOrderException.
     *
     * @param newOrder new order object to add to the store of orders.
     * @return previous order object stored with same ID and date as incoming order.
     * @throws FlooringMasteryNoSuchOrderException if there exists no order currently stored with incoming order's ID.
     */
    Order editOrder(Order newOrder) throws FlooringMasteryNoSuchOrderException;

    /**
     * Returns list of Order objects for the specified date.
     *
     * Returns empty list if no orders are found for the specified date
     * @param date order date of which all returned orders have.
     * @return
     */
    List<Order> getOrdersForDate(LocalDate date);

    /**
     * Returns nested Map containing all orders. The returned outer map's keys represent order dates
     * and the inner map's keys are orderIDs.
     * Note this is NOT PREFERRED method of interacting with orders as it allows for direct manipulation of persistence layer.
     * @return Nested map containing all order objects. Outer Map's keys are order LocalDate,
     * inner map's keys are order ID.
     *
     */
    Map<LocalDate, Map<Integer, Order>> getAllOrders();

    /**
     * Removes from the store of orders the order associated with the given order date
     * and order ID.
     *
     * Returns the Order object being removed or null if no order exists with given ID and date.
     * @param date orderDate of order to be removed
     * @param orderId orderId of order to be removed.
     * @return Removed Order object or null if no order exists with given order date and ID.
     */
    Order removeOrder(LocalDate date, int orderId);

    /**
     * Persists curent order information to storage.
     * @throws FlooringMasteryPersistenceException if error occurs while trying to persist data.
     */
    void saveOrders() throws FlooringMasteryPersistenceException;
}
