package com.sg.floormaster.controller;

import com.sg.floormaster.service.FlooringMasteryServiceLayer;
import com.sg.floormaster.view.FlooringMasteryView;


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
                            throw new UnsupportedOperationException("To Do: Display Orders.");
                            // break;
                        case 2:
                            throw new UnsupportedOperationException("To Do: Add order.");
                            // break;
                        case 3:
                            throw new UnsupportedOperationException("To Do: Edit Order.");
                            // break;
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
            // get view to display error message.
        }
    }

    private int getMenuSelection() {
        return view.displayMainMenuAndGetSelection();
    }

    private void displayOrders() {
        // To do
    }

    private void addOrder() {
        // To do
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
        // to do
    }
}
