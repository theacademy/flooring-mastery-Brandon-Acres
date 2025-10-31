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



        // use tax file.
        FlooringMasteryTaxDao taxDao = new FlooringMasteryTaxDaoFileImpl("Data/Taxes.txt");

        // use products file
        FlooringMasteryProductDao productDao = new FlooringMasteryProductDaoFileImpl("Data/Products.txt");

        FlooringMasteryOrderDao orderDao = new FlooringMasteryOrderDaoFileImpl("Orders");

        // instantiate service with daos wired
        // for now service not implemented
        FlooringMasteryServiceLayer myService = new FlooringMasteryServiceLayerImpl(orderDao, productDao, taxDao);

        // create controller, wire service and view
        FlooringMasteryController controller = new FlooringMasteryController(myView, myService);
        controller.run();
    }
}
