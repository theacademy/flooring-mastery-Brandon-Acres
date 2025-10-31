package com.sg.floormaster.view;

import com.sg.floormaster.dao.FlooringMasteryPersistenceException;
import com.sg.floormaster.model.Order;
import com.sg.floormaster.model.Product;
import com.sg.floormaster.model.Tax;
import com.sg.floormaster.service.FlooringMasteryInvalidInputException;
import com.sg.floormaster.validation.OrderValidation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;



public class FlooringMasteryView {

    private UserIO io;
    private final int BANNER_LENGTH = 50;
    private final String BANNER_CHAR = "*";

    public FlooringMasteryView(UserIO io) { this.io = io; }

    public int displayMainMenuAndGetSelection() {
        String menuBanner = "*".repeat(30);
        // display header:
        System.out.println(menuBanner);

        // Display menu:
        System.out.println("* <<Flooring Program>>");
        System.out.println("* 1. Display Orders by Date");
        System.out.println("* 2. Add an Order");
        System.out.println("* 3. Edit an Order");
        System.out.println("* 4. Remove an Order");
        System.out.println("* 5. Export All Data");
        System.out.println("* 6. Quit");
        System.out.println(menuBanner);

        // get selection:
        return io.readInt("Please Select an option.", 1, 6);

    }

    public void displayErrorMessage(String errorMsg) {

        io.print(errorMsg);
        io.print("");
    }


    public void displayOrders(List<Order> orders) {
        // display header:
        displayOpenBanner("Orders:");


        // print separator
        String separator = "|" + "-".repeat(100);
        io.print(" " + separator.substring(1));

        // display order table headers:
        String header = String.format("| %-10s | %-10s | %-15s | %-14s | %-20s | %-12s ",
                "Order ID",
                "Order Date",
                "Customer Name",
                "State",
                "Product Type",
                "Total");
        io.print(header);
        String headerLine2 = String.format("| %-10s | %-10s | %-15s | %-14s | %-20s | %-12s ",
                "",
                "(MM-DD-YY)",
                "",
                "",
                "",
                "($)");
        io.print(headerLine2);

        // print separator
        io.print(separator);

        // print orders:
        // date displayed MM-DD-YYYY
        for (Order order : orders) {
            String orderRow = String.format("| %-10d | %-10s | %-15s | %-14s | %-20s | %-12s ",
                    order.getOrderNumber(),
                    order.getOrderDate().format(DateTimeFormatter.ofPattern("MM-dd-yyyy")),
                    order.getCustomerName(),
                    order.getState(),
                    order.getProductType(),
                    order.getTotal());
            io.print(orderRow);
        }

        //io.print(String.format(("-".repeat(50) + " %-15s " + "-".repeat(50) + "\n"), "End of Orders"));
        displayCloseBanner("End of Orders");

    }

    public void displayOrderInfo(Order order) {
        // display opening banner:
        displayOpenBanner("Order Info: ");

        // display order info:
        String formatStringInt = "%-30s : %d";
        String formatStringString = "%-30s : %s";

        io.print(String.format(formatStringInt, "Order ID", order.getOrderNumber()));
        io.print("");

        io.print(String.format(formatStringString, "Order Date", order.getOrderDate()
                                .format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))));
        io.print("");

        io.print(String.format(formatStringString, "Customer name", order.getCustomerName()));
        io.print("");

        io.print(String.format(formatStringString, "State", order.getState()));
        io.print("");

        io.print(String.format(formatStringString, "Tax Rate (%)", order.getTaxRate()));
        io.print("");

        io.print(String.format(formatStringString, "Product Type", order.getProductType()));
        io.print("");

        io.print(String.format(formatStringString, "Area (Square Feet)", order.getArea()));
        io.print("");

        io.print(String.format(formatStringString, "Cost Per Square Foot ($)", order.getCostPerSquareFoot()));
        io.print("");

        io.print(String.format(formatStringString, "Labor Cost Per Square Foot ($)", order.getLaborCostPerSquareFoot()));
        io.print("");

        io.print(String.format(formatStringString, "Material Cost ($)", order.getMaterialCost()));
        io.print("");

        io.print(String.format(formatStringString, "Labor Cost ($)", order.getLaborCost()));
        io.print("");

        io.print(String.format(formatStringString, "Tax ($)", order.getTax()));
        io.print("");

        io.print(String.format(formatStringString, "Total ($)", order.getTotal()));

        // closing banner:
        displayCloseBanner("End of Order Info");
    }

    private void displayOpenBanner(String bannerTitle) {
        io.print(BANNER_CHAR.repeat(BANNER_LENGTH));
        io.print(bannerTitle);
        io.print(BANNER_CHAR.repeat(BANNER_LENGTH));
    }

    private void displayCloseBanner(String bannerTitle) {
        io.print(BANNER_CHAR.repeat(BANNER_LENGTH));
        io.print(bannerTitle);
        io.print(BANNER_CHAR.repeat(BANNER_LENGTH));
        io.print("");
    }

    public LocalDate getDateInput(LocalDate dateToValidateAgainst) {
        // until valid date entered, prompt for input.
        while (true) {
            String dateInput = io.readString("Enter date in format: MM-dd-yyyy");

            // attempt to parse as local date:
            LocalDate orderDate;
            try {
                orderDate = LocalDate.parse(dateInput, DateTimeFormatter.ofPattern("MM-dd-yyyy"));
            } catch (DateTimeParseException e) {
                io.print("Invalid date input, try again.");
                continue;
            }

            // validate the date against date:
            try {
                io.print("");
                OrderValidation.validateOrderDate(orderDate, dateToValidateAgainst);
                return orderDate;
            } catch (FlooringMasteryInvalidInputException e) {
                io.print("Date cannot be on or before today. Try again.");
            }
        }
    }

    // --- ADD ORDER -----

    public void displayAddOrderBanner() {
        displayOpenBanner("Add Order:");
    }

    public Order getAddOrderInput(List<Tax> taxes, List<Product> products) {
        // Prompt for each of order date, customer name, state, product type, area.
        // continue to prompt until valid input is provided.

        // create new order to add to:
        Order newOrder = new Order();

        // 1. prompt for order date:
        io.print("Enter an order date. (Must be after today: "
                + LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
                + ").");

        newOrder.setOrderDate(getDateInput(LocalDate.now()));


        // 2. Prompt for customer name:
        newOrder.setCustomerName(getCustomerNameInput());
        io.print("");

        // 3. Get state
        newOrder.setState(getStateInput(taxes));
        // Then add the corresponding tax rate from taxes
        // note null pointer exception may occur - but we have guaranteed that tax exists, so should not be null.
        newOrder.setTaxRate(taxes.stream()
                .filter(t -> t.getState()
                .equals(newOrder.getState())).
                findFirst()
                .get()
                .getTaxRate()); // we know tax exists.
        io.print("");

        // 4. Get Product Type
        // displayProduct information
        displayProductInformation(products);
        newOrder.setProductType(getProductTypeInput(products));
        io.print("");
        // add corresponding cost per square foot
        Product newOrderProduct = products.stream()
                                    .filter(p -> p.getProductType()
                                    .equals(newOrder.getProductType()))
                                    .findFirst()
                                    .get();

        newOrder.setCostPerSquareFoot(newOrderProduct.getCostPerSquareFoot());

        // add corresponding labor cost per square foot.
        newOrder.setLaborCostPerSquareFoot(newOrderProduct.getLaborCostPerSquareFoot());


        // 5. Get Area
        newOrder.setArea(getAreaInput());
        io.print("");

        // return new order with partially complete, valid data from user
        return newOrder;
    }

    private void displayProductInformation(List<Product> products) {
        // displays product information
        io.print("Available products:");
        io.print("-".repeat(BANNER_LENGTH));

        // display product information
        // display header:
        int productTypePadding = 20;
        int costPerSquareFootPadding = 25;
        int laborCostPerSquareFootPadding = 30;

        String formatInfo = " %-"
                + productTypePadding
                + "s | %-" + costPerSquareFootPadding
                + "s | %-" + laborCostPerSquareFootPadding + "s";
        io.print(String.format(formatInfo, "Product Type", "Cost Per Square Foot ($)",
                               "Labor Cost Per Square Foot ($)"));

        for (Product p : products) {
            io.print(String.format(formatInfo,
                    p.getProductType(),
                    p.getCostPerSquareFoot(),
                    p.getLaborCostPerSquareFoot()));
        }
        io.print("-".repeat(BANNER_LENGTH));
        io.print("");
    }

    private String getCustomerNameInput() {
        // until valid customer name entered, prompt for input.
        while (true) {
            // Validate input:
            String input = io.readString(
                    "Enter Customer Name (can contain letters, digits, spaces, \",\", or \".\"):");
            try {
                return getValidCustomerName(input);
            } catch (FlooringMasteryInvalidInputException e) {
                io.print(e.getMessage());
            }
        }
    }

    private String getValidCustomerName(String customerNameInput) throws FlooringMasteryInvalidInputException {
        try {

            return OrderValidation.validateCustomerName(customerNameInput);
        } catch (FlooringMasteryInvalidInputException e) {
            throw new FlooringMasteryInvalidInputException("One or more invalid characters. Try again.\n", e);
        }
    }

    private String getStateInput(List<Tax> taxes) {
        // until valid state name entered, prompt for input.
        while (true) {
            // Validate input - first letter capitalised.
            try {
                String stateInput = io.readString(
                        "Enter State Name").strip().toLowerCase();
                return getValidState(taxes, stateInput);
            } catch (FlooringMasteryInvalidInputException | FlooringMasteryPersistenceException e) {
                io.print(e.getMessage());
            }
        }
    }

    private String getValidState(List<Tax> taxes, String stateInput) throws FlooringMasteryInvalidInputException{
        try {
            String capitalisedState = stateInput.substring(0, 1).toUpperCase() + stateInput.substring(1);;
            return OrderValidation.validateState(taxes, capitalisedState);
        } catch (FlooringMasteryInvalidInputException | FlooringMasteryPersistenceException e) {
            throw new FlooringMasteryInvalidInputException("State Name wasn't found. Try again.\n", e);
        }
    }

    private String getProductTypeInput(List<Product> products) {
        // until valid product type entered, prompt for input.
        while (true) {
            // Validate input - first letter capitalised.
            try {
                String productInput = io.readString(
                        "Enter product type").strip().toLowerCase();
                return getValidProductType(products, productInput);
            } catch (FlooringMasteryInvalidInputException e) {
                io.print(e.getMessage());
            }
        }
    }

    private String getValidProductType(List<Product> products, String productInput) throws
                                                                FlooringMasteryInvalidInputException {
        try {
            // capitalise first letter:
            String capitalisedProduct = productInput.substring(0, 1).toUpperCase() + productInput.substring(1);;
            return OrderValidation.validateProductType(products, capitalisedProduct);
        } catch (FlooringMasteryInvalidInputException e) {
            throw new FlooringMasteryInvalidInputException("Product type wasn't found. Try again.\n", e);
        }
    }

    private BigDecimal getAreaInput() {
        // until valid area entered, prompt for input.
        while (true) {
            // Validate input - greater than min area.
            try {
                String areaInput = io.readString(
                        "Enter order area (min 100 square feet):");
                return getValidArea(areaInput);
            } catch (FlooringMasteryInvalidInputException | NumberFormatException e) {
                io.print(e.getMessage());
            }
        }
    }

    private BigDecimal getValidArea(String areaInput) {
        // Validate input - greater than min area.
        try {
            // try to convert to big Decimal
            BigDecimal areaBD = new BigDecimal(areaInput).setScale(2, RoundingMode.HALF_UP);

            return OrderValidation.validateArea(areaBD);
        } catch (FlooringMasteryInvalidInputException e) {
            throw new FlooringMasteryInvalidInputException("area must be greater than or equal to 100. Try again.\n", e);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Couldn't read that number, enter a decimal value. e.g. 101.0\n");
        }
    }

    public void displayOrderSummary(Order order) {
        io.print("Please review your order below:\n");
        displayOrderInfo(order);
    }

    public void displayAddOrderSuccess() {
        io.print("!!!! Successfully added new order. !!!!");
        io.print("");
    }

    public void displayAddOrderDiscarded() {
        io.print("Discarded new order. No changes made.");
        io.print("");
    }

    // --- EDIT ORDER ----
    public void displayEditOrderBanner() {
        displayOpenBanner("Edit Order:");
    }

    public int getOrderNumberInput() {
        // controller must handle validation of ID
        int inputInt =  io.readInt("Enter an Order ID:");
        io.print("");
        return inputInt;
    }

    public Order getEditOrderInput(Order previousOrder, List<Tax> taxes, List<Product> products) {
        // create new order that may need changes made:

        Order newEditedOrder = new Order();

        // 1. get customerName input
        String optionalCustomerName = getOptionalCustomerNameInput(previousOrder.getCustomerName());
        // if empty, copy previous order value, otherwise use new value.
        if (optionalCustomerName.isEmpty()) {
            newEditedOrder.setCustomerName(previousOrder.getCustomerName());
        } else {
            newEditedOrder.setCustomerName(optionalCustomerName);
        }
        io.print("");

        // 2. Get state
        String optionalState = getOptionalStateInput(taxes, previousOrder.getState());
        // if empty, copy previous value, otherwise use new value
        if (optionalState.isEmpty()) {
            newEditedOrder.setState(previousOrder.getState());
            newEditedOrder.setTaxRate(previousOrder.getTaxRate());
        } else {
            // fetch new tax rate
            newEditedOrder.setState(optionalState);
            // Then add the corresponding tax rate from taxes
            // note null pointer exception may occur - but we have guaranteed that tax exists, so should not be null.
            newEditedOrder.setTaxRate(taxes.stream()
                    .filter(t -> t.getState()
                            .equals(newEditedOrder.getState())).
                    findFirst()
                    .get()
                    .getTaxRate()); // we know tax exists.
        }
        io.print("");

        // 3. get product type
        String optionalProductType = getOptionalProductType(products, previousOrder.getProductType());
        if (optionalProductType.isEmpty()) {
            newEditedOrder.setProductType(previousOrder.getProductType());
            newEditedOrder.setCostPerSquareFoot(previousOrder.getCostPerSquareFoot());
            newEditedOrder.setLaborCostPerSquareFoot(previousOrder.getLaborCostPerSquareFoot());
        } else {
            // fetch new product types from products
            newEditedOrder.setProductType(optionalProductType);
            // add corresponding cost per square foot
            Product newOrderProduct = products.stream()
                    .filter(p -> p.getProductType()
                            .equals(newEditedOrder.getProductType()))
                    .findFirst()
                    .get();

            newEditedOrder.setCostPerSquareFoot(newOrderProduct.getCostPerSquareFoot());

            // add corresponding labor cost per square foot.
            newEditedOrder.setLaborCostPerSquareFoot(newOrderProduct.getLaborCostPerSquareFoot());
        }
        io.print("");

        // 4. get area
        String optionalArea = getOptionalAreaInput(previousOrder.getArea());
            if (optionalArea.isEmpty()) {
                newEditedOrder.setArea(previousOrder.getArea());
            } else {
                // must convert string to BigDecimal
                BigDecimal newArea = new BigDecimal(optionalArea).setScale(2, RoundingMode.HALF_UP);
                newEditedOrder.setArea(newArea);
            }
        io.print("");


        // return new partially complete order
        return newEditedOrder;

    }

    // Wrappers around input methods above - but allow for empty string input to represent wanting to keep data.
    private String getOptionalCustomerNameInput(String previousCustomerName) {
        while (true) {
            // get input
            String input = io.readString(
                    "Enter Customer Name (" + previousCustomerName+
                            ") \n(can contain letters, digits, spaces, \",\", or \".\"):");

            // if empty string, or null, return empty string - indicating user wishes to keep old customer name.
            if (input == null || input.isEmpty()) {
                return "";
            }

            // otherwise attempt to validate the input:
            try {
                return getValidCustomerName(input);
            } catch (FlooringMasteryInvalidInputException e) {
                io.print(e.getMessage());
            }
        }
    }

    private String getOptionalStateInput(List<Tax> taxes, String previousState) {
        while (true) {
            // get input
            String stateInput = io.readString(
                    "Enter State Name (" + previousState + "):").strip().toLowerCase();
            // if empty or null, return empty string
            if (stateInput == null || stateInput.isEmpty()) {
                return "";
            }

            // otherwise process potential new input.
            try {
                return getValidState(taxes, stateInput);
            } catch (FlooringMasteryInvalidInputException | FlooringMasteryPersistenceException e) {
                io.print(e.getMessage());
            }

        }
    }

    private String getOptionalProductType(List<Product> products, String previousProduct) {
        // until valid product type entered or empty string, prompt for input.
        while (true) {
            // get input:
            String productInput = io.readString(
                    "Enter product type (" + previousProduct + "):").strip().toLowerCase();

            // if null or empty, return empty string
            if (productInput == null || productInput.isEmpty()) {
                return "";
            }

            // process new data:
            try {
                return getValidProductType(products, productInput);
            } catch (FlooringMasteryInvalidInputException e) {
                io.print(e.getMessage());
            }
        }
    }

    private String getOptionalAreaInput(BigDecimal previousArea) {
        // until valid area entered, prompt for input.
        while (true) {
            // Validate input - greater than min area.
            String areaInput = io.readString(
                    "Enter order area ("+previousArea+
                            ")\n(min 100 square feet):");

            // if null or empty, return empty string:
            if (areaInput == null || areaInput.isEmpty()) {
                return "";
            }
            try {

                return getValidArea(areaInput).toString();
            } catch (FlooringMasteryInvalidInputException | NumberFormatException e) {
                io.print(e.getMessage());
            }
        }
    }

    public void displayEditOrderSuccess() {
        io.print("!!!! Successfully Saved Edited Order !!!!");
        io.print("");
    }

    public void displayDiscardEditOrder() {
        io.print("Edited order discarded, no changes made.");
        io.print("");
    }

    // --- REMOVE ORDER -----
    public void displayRemoveOrderBanner() {
        displayOpenBanner("Remove Order:");
    }

    public void displayRemoveOrderSuccess() {
        io.print("!!!! Successfully Removed Order !!!!");
        io.print("");
    }

    public void displayRemoveOrderDiscardMessage() {
        io.print("Remove order discarded, no changes made.");
        io.print("");
    }

    public boolean getConfirmation() {
        displayOpenBanner("Confirm Change:");

        while (true) {
            String input = io.readString("Are you happy with these changes? (Enter y/n)")
                    .strip()
                    .toLowerCase();

            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            }

            // otherwise, didn't enter valid choice
            io.print("Invalid choice, try again.");
        }
    }

    // display export data success

    public void displayExitMessage() {
        // todo
    }

    public void displayUnknownCommand() {
        // todo
    }



}
