package com.sg.floormaster.view;

import javax.xml.transform.Source;

public class FlooringMasteryView {

    private UserIO io;

    public FlooringMasteryView(UserIO io) { this.io = io; }

    public int displayMainMenuAndGetSelection() {
        String menuBanner = "*".repeat(30);
        // display header:
        System.out.println(menuBanner);

        // Display menu:
        System.out.println("* <<Flooring Program>>");
        System.out.println("* 1. Display Orders");
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
    }
}
