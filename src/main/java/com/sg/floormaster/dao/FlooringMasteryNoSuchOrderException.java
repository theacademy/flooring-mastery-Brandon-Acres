package com.sg.floormaster.dao;

public class FlooringMasteryNoSuchOrderException extends RuntimeException {
    public FlooringMasteryNoSuchOrderException(String message) {
        super(message);
    }

    public FlooringMasteryNoSuchOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
