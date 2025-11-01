package com.sg.floormaster.view;

import com.sg.floormaster.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlooringMasteryViewTest {
    FlooringMasteryView view;

    // add dependency injection later:
    public FlooringMasteryViewTest() {
        // get application context
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        this.view = ctx.getBean("view", FlooringMasteryView.class);
    }

    @Test
    public void testDisplayOrders() {
        // arrange, create orders to display.
        Order o1 = new Order();
        o1.setOrderNumber(0);
        o1.setOrderDate(LocalDate.now());
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

        List<Order> orders = new ArrayList<>();
        orders.add(o1);
        System.out.println("============ Testing displayOrders() output ===========");
        view.displayOrders(orders);
        System.out.println("========== End of displayOrder() test =============");
    }

    @Test
    public void testDisplayOrderInfo() {
        // arrange, create orders to display.
        Order o1 = new Order();
        o1.setOrderNumber(0);
        o1.setOrderDate(LocalDate.now());
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

        System.out.println("============ Testing displayOrderInfo() output ===========");
        view.displayOrderInfo(o1);
        System.out.println("========== End of displayOrderInfo() test =============");
    }
}