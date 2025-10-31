package com.sg.floormaster.dao;

import com.sg.floormaster.model.Tax;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlooringMasteryTaxDaoFileImpl implements FlooringMasteryTaxDao {

    // Map with tax code as key and values as Tax objects.
    // note that tax code therefore is unique inherently
    // However, extra implementation required to ensure that state names are unique
    private Map<String, Tax> allTaxes;

    // Implement when adding persistence
//    private final TAX_FILE;
//    private final DELIMITER;

    public FlooringMasteryTaxDaoFileImpl(Map<String, Tax> taxes) throws FlooringMasteryPersistenceException{
        validateAllTaxes(taxes);
        this.allTaxes = taxes;
    }

    public FlooringMasteryTaxDaoFileImpl() {}


    @Override
    public List<Tax> getAllTaxes() {
        // edit to validate that taxes are all unique
        return new ArrayList<>(allTaxes.values());
    }

    // Allows for verifying injected maps (for in-memory testing)
    private void validateAllTaxes(Map<String, Tax> taxes) throws FlooringMasteryPersistenceException {

        // Raise exception if given null pointer - map should not be null
        if (taxes == null) {
            throw new FlooringMasteryPersistenceException("Internal store of taxes cannot be null");
        }

        // We are guaranteed all state codes are unique as allTaxes has a set of keys
        // verify that all state names are also unique
        HashSet<String> stateNames = new HashSet<>();
        for (Tax t : taxes.values()) {
            // Ensure that the state abbreviation key points to a tax object with the same state abbreviation.
            if (!t.equals(taxes.get(t.getStateAbr()))) {
                throw new FlooringMasteryPersistenceException("Cannot have a state abbreviation mapping to a Tax" +
                        " with a different abbreviation");
            }
            if (!stateNames.add(t.getState())) {
                throw new FlooringMasteryPersistenceException("Cannot have multiple tax entries with the same state name.");
            }
        }

    }

    // implement when adding persistence.
    // private void loadFile()
        // if when adding value to the map - returns a value from put, throw PersistenceException
        // makes call to validateAllTaxes - pass this.allTaxes
}
