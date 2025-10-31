package com.sg.floormaster.controller;

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

    // Currently built-in dependency injection
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

                // REMOVE: TRY-CATCH here when all user stories have been implemented
                try {
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
                            throw new UnsupportedOperationException("To do: remove order");
                            // break;
                        case 5:
                            throw new UnsupportedOperationException("To Do: Export All Data");
                            // break;
                        case 6:
                            keepGoing = false;
                            throw new UnsupportedOperationException("To Do: Quit");
                        default:
                            unknownCommand();

                    }
                } catch (UnsupportedOperationException e) {
                    // get view to display error message
                    view.displayErrorMessage(e.getMessage());
                }

            }
        } catch (Exception e) { // Edit to be more specific exceptions as they apear.
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
        }

        view.displayOrders(orders);


    }

    private void addOrder() {
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
        }
        // otherwise
        // display add order discarded.
        view.displayAddOrderDiscarded();
        // return to menu (do nothing, order will be collected from garbage).
    }

    private void editOrder() {
        // To do
    }

    private void removeOrder() {
        // To do
    }

    //Optional
     private void exportData() {

     }

    private void exitMessage() {
        // To do
    }

    private void unknownCommand() {
        view.displayUnknownCommand();
    }
}
