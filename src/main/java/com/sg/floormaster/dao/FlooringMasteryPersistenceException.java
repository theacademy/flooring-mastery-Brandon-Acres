package com.sg.floormaster.dao;

public class FlooringMasteryPersistenceException extends RuntimeException {
    public FlooringMasteryPersistenceException(String message) {
        super(message);
    }

    public FlooringMasteryPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
