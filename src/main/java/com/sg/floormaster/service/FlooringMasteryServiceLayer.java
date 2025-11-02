package com.sg.floormaster.service;

import com.sg.floormaster.dao.FlooringMasteryNoSuchOrderException;
import com.sg.floormaster.dao.FlooringMasteryPersistenceException;
import com.sg.floormaster.model.Order;
import com.sg.floormaster.model.Product;
import com.sg.floormaster.model.Tax;

import java.time.LocalDate;
import java.util.List;

public interface FlooringMasteryServiceLayer {

    /**
     * Retrieve next available order number - guaranteed to be unique and not currently
     * used by another order in the system.
     *
     * @return unique number representing new order ID
     */
    int getNextOrderNumber();

    /**
     * Adds a new order to the store of orders. If an order with the same date and ID
     * already exists, throws FlooringMasteryDuplicateOrderException.
     * @param order new (already valid) order to add to the order store.
     */
    void addOrder(Order order) throws FlooringMasteryDuplicateOrderException,
                                        FlooringMasteryInvalidInputException,
                                            FlooringMasteryPersistenceException;

    /**
     * Retrieves an existing order from order store with given date and order ID.
     * If no order found for that date and order ID, will return null.
     * @param date date of order to retrieve.
     * @param orderId ID of order to retrieve.
     * @return order object with given date and ID from the store, null if no such order found.
     */
    Order getOrder(LocalDate date, int orderId);

    /**
     * Replaces an existing order with a new order object with the same order date and ID.
     * Throws FlooringMasterInvalidInputException if order is not valid.
     * Throws FlooringMasterNoSuchOrderException if no order is currently stored with same date and ID.
     * @param order new order to replace an existing order with and will be persisted.
     * @throws FlooringMasteryNoSuchOrderException if no order can be retrieved with new order's date and ID.
     * @throws FlooringMasteryInvalidInputException if the incoming order is not a valid order.
     * @throws FlooringMasteryPersistenceException if an error occurs when persisting the order,
     * or if the persistence layer holds an invalid state.
     */
    void editOrder(Order order) throws FlooringMasteryInvalidInputException,
                                       FlooringMasteryNoSuchOrderException,
                                       FlooringMasteryPersistenceException;

    /**
     * Returns list of all order objects stored with the provided date. If no orders are found,
     * will return an empty list.
     * @param date date of all orders to retrieve.
     * @return list of order objects which have an orderDate equal to supplied order.
     */
    List<Order> getOrdersForDate(LocalDate date);

    /**
     * Removes an order from the stored orders with the given order date and ID.
     * Returns the order object removed from the store if one existed, null if no order
     * with given orderDate and ID existed.
     * @param date orderDate of target order to remove.
     * @param orderId order ID of target order to remove.
     * @return order object with given orderDate and orderID which was removed from store, null
     * if no order was found.
     */
    Order removeOrder(LocalDate date, int orderId);

    // exportData()

    /**
     * Returns list of all Tax objects currently stored in the system. If none exist, returns empty list.
     * @return list of all Tax objects currently stored in system.
     */
    List<Tax> getTaxes();

    /**
     * Returns list of all Product objects currently stored in the system. If none exist, returns empty list.
     * @return list of all Product orders stored.
     */
    List<Product> getProducts();

    /**
     * Calculates MaterialCost, LaborCost, Tax, and Total properties of given Order object.
     * If order object's current properties do not meet business requirements,
     * throws FlooringMasteryInvalidInputException
     * @param order order whose properties are calculated and stored in the object.
     */
    void calculateOrderCosts(Order order, LocalDate date)
            throws FlooringMasteryInvalidInputException,
            FlooringMasteryPersistenceException;


    /**
     * Saves current order data stored in memory, throws persistence exception if error occurs when trying to persist data.
     * @throws FlooringMasteryPersistenceException if error occurs when trying to persist orders.
     */
    void saveOrders() throws FlooringMasteryPersistenceException;
}

