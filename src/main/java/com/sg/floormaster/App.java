package com.sg.floormaster;

import com.sg.floormaster.controller.FlooringMasteryController;
import com.sg.floormaster.dao.*;
import com.sg.floormaster.model.Order;
import com.sg.floormaster.model.Product;
import com.sg.floormaster.model.Tax;
import com.sg.floormaster.service.FlooringMasteryServiceLayer;
import com.sg.floormaster.service.FlooringMasteryServiceLayerImpl;
import com.sg.floormaster.view.FlooringMasteryView;
import com.sg.floormaster.view.UserIO;
import com.sg.floormaster.view.UserIOConsoleImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class App {
    /**
     * Entry point into the Flooring Mastery program, initialises controller and runs
     * the program.
     * @param args array of optional string command-line arguments (unused).
     */
    public static void main(String[] args) {
        // to do: replace this with spring dependency injections

        UserIO myIo = new UserIOConsoleImpl();

        // instantiate view and wire IO to implement
        FlooringMasteryView myView = new FlooringMasteryView(myIo);

        // instantiate Daos
        // Tax
        Map<String, Tax> taxTest = new HashMap<>();
        Tax t1 = new Tax("Texas", "TX", new BigDecimal("12.00").setScale(2, RoundingMode.HALF_UP));
        taxTest.put(t1.getStateAbr(), t1);
        FlooringMasteryTaxDao taxDao = new FlooringMasteryTaxDaoFileImpl(taxTest);

        // Product
        Map<String, Product> productTest = new HashMap<>();
        Product p1 = new Product("Carpet",
                    new BigDecimal("11.2").setScale(2, RoundingMode.HALF_UP),
                    new BigDecimal("11.55").setScale(2, RoundingMode.HALF_UP));
        productTest.put(p1.getProductType(), p1);
        FlooringMasteryProductDao productDao = new FlooringMasteryProductDaoFileImpl(productTest);

        Map<LocalDate, Map<Integer, Order>> memoryOrders = new HashMap<>();
        // create a single order entry
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

        Map<Integer, Order> doobDayOrders = new HashMap<>();
        doobDayOrders.put(o1.getOrderNumber(), o1);
        memoryOrders.put(o1.getOrderDate(), doobDayOrders);
        FlooringMasteryOrderDao orderDao = new FlooringMasteryOrderDaoFileImpl(memoryOrders);

        // instantiate service with daos wired
        // for now service not implemented
        FlooringMasteryServiceLayer myService = new FlooringMasteryServiceLayerImpl(orderDao, productDao, taxDao);

        // create controller, wire service and view
        FlooringMasteryController controller = new FlooringMasteryController(myView, myService);
        controller.run();
    }
}
