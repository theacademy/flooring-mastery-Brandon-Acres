package com.sg.floormaster.dao;

import com.sg.floormaster.model.Order;
import org.springframework.context.support.AbstractRefreshableApplicationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlooringMasteryOrderDaoStubImpl implements FlooringMasteryOrderDao {

    private Order onlyOrder;
    private int largestOrderNumber;

    public FlooringMasteryOrderDaoStubImpl() {
        onlyOrder = new Order();
        onlyOrder.setOrderNumber(1);
        onlyOrder.setOrderDate(LocalDate.parse("2013-06-01"));
        onlyOrder.setCustomerName("Ada Lovelace");
        onlyOrder.setState("TX");
        onlyOrder.setProductType("Carpet");
        onlyOrder.setTaxRate(new BigDecimal("4.45").setScale(2, RoundingMode.HALF_UP));
        onlyOrder.setArea(new BigDecimal("249.00").setScale(2, RoundingMode.HALF_UP));
        onlyOrder.setCostPerSquareFoot(new BigDecimal("2.25").setScale(2, RoundingMode.HALF_UP));
        onlyOrder.setLaborCostPerSquareFoot(new BigDecimal("2.10").setScale(2, RoundingMode.HALF_UP));
        onlyOrder.setMaterialCost(new BigDecimal("560.25").setScale(2, RoundingMode.HALF_UP));
        onlyOrder.setLaborCost(new BigDecimal("522.90").setScale(2, RoundingMode.HALF_UP));
        onlyOrder.setTax(new BigDecimal("43.33").setScale(2, RoundingMode.HALF_UP));
        onlyOrder.setTotal(new BigDecimal("1126.48").setScale(2, RoundingMode.HALF_UP));

        largestOrderNumber = onlyOrder.getOrderNumber();
    }

    public FlooringMasteryOrderDaoStubImpl(Order order) {
        onlyOrder = order;
        largestOrderNumber = order.getOrderNumber();
    }

    @Override
    public int getNextOrderNumber() {
        largestOrderNumber += 1;
        return largestOrderNumber;
    }

    @Override
    public Order addOrder(Order order) {
        if (order.equals(onlyOrder)) {
            return onlyOrder;
        }
        // otherwise return null
        return null;
    }

    @Override
    public Order getOrder(LocalDate date, int orderId) {
        if (orderId == onlyOrder.getOrderNumber() & date.equals(onlyOrder.getOrderDate())) {
            return onlyOrder;
        }
        // otherwise null
        return null;
    }

    @Override
    public Order editOrder(Order newOrder) throws FlooringMasteryNoSuchOrderException {
        // if order matches date and id of onlyOrder, replace onlyOrder, return old order.
        if (newOrder.getOrderNumber() == onlyOrder.getOrderNumber() &&
            newOrder.getOrderDate().equals(onlyOrder.getOrderDate())) {
            Order previousOrder = onlyOrder;
            onlyOrder = newOrder;
            return previousOrder;
        }
        // otherwise, order isn't found throw exception
        throw new FlooringMasteryNoSuchOrderException("No such order found.");
    }

    @Override
    public List<Order> getOrdersForDate(LocalDate date) {
        // if date equals our only order, return list with only order
        if (date.equals(onlyOrder.getOrderDate())) {
            List<Order> orders = new ArrayList<>();
            orders.add(onlyOrder);
            return orders;
        }
        // otherwise return empty list
        return new ArrayList<>();
    }

    @Override
    public Map<LocalDate, Map<Integer, Order>> getAllOrders() {
        // return map containing single order.
        Map<LocalDate, Map<Integer, Order>> orderMap = new HashMap<>();
        Map<Integer, Order> innerMap = new HashMap<>();
        innerMap.put(onlyOrder.getOrderNumber(), onlyOrder);
        orderMap.put(onlyOrder.getOrderDate(), innerMap);
        return orderMap;
    }

    @Override
    public Order removeOrder(LocalDate date, int orderId) {
        if (date.equals(onlyOrder.getOrderDate()) && orderId == onlyOrder.getOrderNumber()) {
            return onlyOrder;
        }
        return null;
    }

    @Override
    public void saveOrders() throws FlooringMasteryPersistenceException {
        // do nothing.
    }
}
