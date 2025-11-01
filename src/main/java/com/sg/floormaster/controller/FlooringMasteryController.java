package com.sg.floormaster.controller;

import com.sg.floormaster.dao.FlooringMasteryNoSuchOrderException;
import com.sg.floormaster.dao.FlooringMasteryPersistenceException;
import com.sg.floormaster.model.Order;
import com.sg.floormaster.service.FlooringMasteryDuplicateOrderException;
import com.sg.floormaster.service.FlooringMasteryInvalidInputException;
import com.sg.floormaster.service.FlooringMasteryServiceLayer;
import com.sg.floormaster.view.FlooringMasteryView;

import java.time.LocalDate;
import java.util.List;


public class FlooringMasteryController {
    private FlooringMasteryView view;
    private FlooringMasteryServiceLayer service;

    public FlooringMasteryController(FlooringMasteryView view, FlooringMasteryServiceLayer service) {
        this.view = view;
        this.service = service;
    }

    public void run() {
        boolean keepGoing = true;
        int menuSelection = 0;

        try {
            while (keepGoing) {

                // get selection from user
                menuSelection = getMenuSelection();

                // execute option basd on menu selection
                switch(menuSelection) {
                    case 1:
                        displayOrders();
                        break;
                    case 2:
                        addOrder();
                        break;
                    case 3:
                        editOrder();
                        break;
                    case 4:
                        removeOrder();
                        break;
                    case 5:
                        exportData();
                        break;
                    case 6:
                        keepGoing = false;
                        saveOrders();
                        exitMessage();
                        break;
                    default:
                        unknownCommand();
                }
            }
        } catch (Exception e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    private int getMenuSelection() {
        return view.displayMainMenuAndGetSelection();
    }

    private void displayOrders() {
        // get date input from user
        LocalDate targetDate = view.getDateInput(null); // could search for date in past.

        // If list is empty, display error message
        List<Order> orders = service.getOrdersForDate(targetDate);

        if (orders.isEmpty()) {
            view.displayErrorMessage("There are no orders to view for that date.");
            return;
        }

        view.displayOrders(orders);


    }

    private void addOrder() {
        // Display banner
        view.displayAddOrderBanner();

        // get new Valid order:
        Order newValidOrder = view.getAddOrderInput(service.getTaxes(), service.getProducts());

        // calculate costs
        try {
            service.calculateOrderCosts(newValidOrder, LocalDate.now()); // not necessary to check date now but we do
        } catch (FlooringMasteryPersistenceException | FlooringMasteryInvalidInputException e) {
            view.displayErrorMessage("ERROR: Could not calculate valid order costs, from input order.");
            return; // return to main menu.
        }

        // display order summary
        view.displayOrderSummary(newValidOrder);

        // prompt confirmation
        if (view.getConfirmation()) {
            // user confirms to save changes
            // get next order number
            newValidOrder.setOrderNumber(service.getNextOrderNumber());
            // try to persist to service layer
            try {
                service.addOrder(newValidOrder);
            } catch (FlooringMasteryDuplicateOrderException e) {
                view.displayErrorMessage("Error occurred, couldn't add to the order store:\n"
                        + e.getMessage());
                return; // don't continue, error occurred during persistence.
            }
            // display success message
            view.displayAddOrderSuccess();
            return;
        }
        // otherwise
        // display add order discarded.
        view.displayAddOrderDiscarded();
        // return to menu (do nothing, order will be collected from garbage).
    }

    private void editOrder() {
        // display banner
        view.displayEditOrderBanner();

        // Get date from user
        LocalDate targetDate = view.getDateInput(null); // can be historical date.

        // Get ID from user
        int orderID;
        Order previousOrder;
        while (true) {
            orderID = view.getOrderNumberInput();

            // validate that this exists in storage:
            previousOrder = service.getOrder(targetDate, orderID);

            if (previousOrder != null) {
                // retrieval succeeds, escape
                break;
            }

            // retrieval failed, output error message
            view.displayErrorMessage("Could not find order with given ID and date. Try again.");
        }

        // get new order from view
        // new order may contain one or more unchanged attributes out of [customerName, state, productType, area].
        Order newOrder = view.getEditOrderInput(previousOrder, service.getTaxes(), service.getProducts());

        // Copy the Date to new order
        newOrder.setOrderDate(previousOrder.getOrderDate());

        // copy the order number
        newOrder.setOrderNumber(previousOrder.getOrderNumber());


        // check if any of [state, product, area] have changed - if so - recalculate order costs.
        // Only don't recalculate if every component is equal
        if (!(previousOrder.getState().equals(newOrder.getState())
                && previousOrder.getProductType().equals(newOrder.getProductType())
                && previousOrder.getArea().equals(newOrder.getArea()))) {
            // recompute the costs:
            service.calculateOrderCosts(newOrder, null); // date could be in the past.
        } else {
            // We can shallow copy values from previous order:
            newOrder.setLaborCost(previousOrder.getLaborCost());
            newOrder.setMaterialCost(previousOrder.getMaterialCost());
            newOrder.setTax(previousOrder.getTax());
            newOrder.setTotal(previousOrder.getTotal());
        }

        // now have fully-fledged, order.

        // display summary of new order info
        view.displayOrderSummary(newOrder);

        // prompt for whether edit should be saved.
        if (view.getConfirmation()) {
            // persist to database
            try {
                service.editOrder(newOrder);
            } catch (FlooringMasteryPersistenceException
                     | FlooringMasteryInvalidInputException
                     | FlooringMasteryNoSuchOrderException e) {
                view.displayErrorMessage("Could not add edited file to store: \n" + e.getMessage());
                return; // go back to main menu
            }

            // otherwise Order was successful
            view.displayEditOrderSuccess();
            return;
        }

        // If confirmation was negative, display discard banner
        view.displayDiscardEditOrder();



    }

    private void removeOrder() {
        // display remove order banner
        view.displayRemoveOrderBanner();

        // Continue until valid order date and number is entered:
        while (true) {
            // get date
            LocalDate targetDate = view.getDateInput(null); // can be in past.

            // get order number from user
            int targetOrderNumber = view.getOrderNumberInput();

            // fetch order
            Order existingOrder = service.getOrder(targetDate, targetOrderNumber);

            // if it exists:
            if (existingOrder != null) {
                // display order info
                view.displayOrderSummary(existingOrder);

                // prompt for confirmation
                if (view.getConfirmation()) {
                    // if yes, remove from store
                    Order removedOrder = service.removeOrder(targetDate, targetOrderNumber);
                    // if successful (non-null returned), display success message
                    if (removedOrder != null) {
                        view.displayRemoveOrderSuccess();
                    } else {
                        // otherwise display error message, couldn't find order.
                        view.displayErrorMessage("Couldn't find an order with given date and ID. Try again.");
                        // prompt for input again
                        continue;
                    }

                } else {
                    // if no, don't remove, display discard message
                    view.displayRemoveOrderDiscardMessage();
                }

                // return in both cases - entered valid input, either order was removed or user decided not to persist.
                return;
            }


            // else order is null, prompt again for input and orderID
            view.displayErrorMessage("Couldn't find an order with that date and ID. Try again.");
        }

    }

    //Optional
     private void exportData() {

     }

    private void exitMessage() {
        view.displayExitMessage();
    }

    private void unknownCommand() {
        view.displayUnknownCommand();
    }

    private void saveOrders() {
        // attempts to write currently stored data to files:
        try {
            service.saveOrders();
        } catch (FlooringMasteryPersistenceException e) {
            view.displayErrorMessage(e.getMessage());
        }

    }
}
