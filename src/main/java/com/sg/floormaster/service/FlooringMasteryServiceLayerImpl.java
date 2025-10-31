package com.sg.floormaster.service;

import com.sg.floormaster.dao.*;
import com.sg.floormaster.model.Order;
import com.sg.floormaster.model.Product;
import com.sg.floormaster.model.Tax;
import com.sg.floormaster.validation.OrderValidation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FlooringMasteryServiceLayerImpl implements FlooringMasteryServiceLayer {

    private FlooringMasteryOrderDao orderDao;
    private FlooringMasteryTaxDao taxDao;
    private FlooringMasteryProductDao productDao;
    // extensions:
    // auditDao
    // exportDao

    // Adjust to autowire/spring dependency:
    public FlooringMasteryServiceLayerImpl() {
        orderDao = new FlooringMasteryOrderDaoFileImpl();
        taxDao = new FlooringMasteryTaxDaoFileImpl();
        productDao = new FlooringMasteryProductDaoFileImpl();
    }

    public FlooringMasteryServiceLayerImpl(FlooringMasteryOrderDao orderDao,
                                           FlooringMasteryProductDao productDao,
                                           FlooringMasteryTaxDao taxDao) {
        this.orderDao = orderDao;
        this.productDao = productDao;
        this.taxDao = taxDao;
    }

    // Adjust to autowire dependencies
    public FlooringMasteryServiceLayerImpl(FlooringMasteryOrderDao orderDao,
                                       FlooringMasteryTaxDao taxDao,
                                       FlooringMasteryProductDao productDao) {
        this.orderDao = orderDao;
        this.taxDao = taxDao;
        this.productDao = productDao;
    }

    @Override
    public int getNextOrderNumber() {
        return orderDao.getNextOrderNumber();
    }

    @Override
    public void addOrder(Order order) throws FlooringMasteryDuplicateOrderException {
        // Check if there already exists an order with given id and orderDate.
        if (orderDao.getOrder(order.getOrderDate(), order.getOrderNumber()) != null) {
            throw new FlooringMasteryDuplicateOrderException(String.format(
                    "Cannot add order (date: %s, ID: %d), order already exists with same date and ID.",
                    order.getOrderDate().toString(), order.getOrderNumber()));
        }

        // Otherwise, can persist order - it should be valid from controller.
        orderDao.addOrder(order);
    }

    @Override
    public Order getOrder(LocalDate date, int orderId) {
        return orderDao.getOrder(date, orderId);
    }

    @Override
    public void editOrder(Order order) throws FlooringMasteryInvalidInputException,
                                              FlooringMasteryNoSuchOrderException,
                                              FlooringMasteryPersistenceException  {
        // validates the order first
        // provide null argument to date - we may be editing an order in the past,
        // therefore must not enforce the date to be in the future.

        // throws invalid input, persistence exception
        OrderValidation.validateOrder(order, getTaxes(), getProducts(), null);

        // validation passes, we attempt to persist to orderDao
        orderDao.editOrder(order); // throws noSuchOrder exception if no order found with that ID and date.
    }

    @Override
    public List<Order> getOrdersForDate(LocalDate date) {
        return orderDao.getOrdersForDate(date);
    }

    @Override
    public Order removeOrder(LocalDate date, int orderId) {
        return null;
    }

    @Override
    public List<Tax> getTaxes() {
        return taxDao.getAllTaxes();
    }

    @Override
    public List<Product> getProducts() {
        return productDao.getAllProducts();
    }

    @Override
    public void calculateOrderCosts(Order order, LocalDate date)
            throws FlooringMasteryInvalidInputException,
            FlooringMasteryPersistenceException {
        // first validate the object
        try {
            OrderValidation.validateOrder(order, getTaxes(), getProducts(), date); // throws FlooringMasteryInvalidInputException if it failed
        } catch (FlooringMasteryInvalidInputException e) {
            throw new FlooringMasteryInvalidInputException(
                    "could not calculate order properties: invalid order.", e);
        } catch (FlooringMasteryPersistenceException e) {
            throw new FlooringMasteryPersistenceException(
                    "Could not calculate order properties: persistence error.", e);
        }

        // otherwise have a fully validated order, only need to calculate:
        // 1. Material Cost
        // 2. Labor Cost
        // 3. Tax
        // 4. Total

        // MaterialCost = area * costPerSquareFoot
        order.setMaterialCost(order.getArea().multiply(
                order.getCostPerSquareFoot())
                .setScale(2, RoundingMode.HALF_UP));

        // LaborCost = area * laborCostPerSquareFoot
        order.setLaborCost(
                order.getArea().multiply(
                        order.getLaborCostPerSquareFoot())
                        .setScale(2, RoundingMode.HALF_UP));

        // Tax = (materialCost + laborCost) * (tax/100)
        // scale must be set to 2, with half_up rounding.
        BigDecimal materialPlusLaborCost = order.getMaterialCost().add(order.getLaborCost());
        BigDecimal taxRateDiv100 = order.getTaxRate().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        order.setTax(materialPlusLaborCost.multiply(taxRateDiv100).setScale(2, RoundingMode.HALF_UP));

        // Total = (materialCost + laborCost + tax)
        order.setTotal(materialPlusLaborCost.add(
                order.getTax())
                .setScale(2, RoundingMode.HALF_UP));

        // now valid order ready to be persisted.
    }


}
