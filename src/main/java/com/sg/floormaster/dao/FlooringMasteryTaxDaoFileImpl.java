package com.sg.floormaster.dao;

import com.sg.floormaster.model.Tax;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class FlooringMasteryTaxDaoFileImpl implements FlooringMasteryTaxDao {

    // Map with tax code as key and values as Tax objects.
    // note that tax code therefore is unique inherently
    // However, extra implementation required to ensure that state names are unique
    private Map<String, Tax> allTaxes;

    // Implement when adding persistence
    private final String TAX_FILE;
    private final String DELIMITER = ",";

    public FlooringMasteryTaxDaoFileImpl(Map<String, Tax> taxes) throws FlooringMasteryPersistenceException{
        validateAllTaxes(taxes);
        this.allTaxes = taxes;
        this.TAX_FILE = "Data/Taxes.txt";
    }

    public FlooringMasteryTaxDaoFileImpl() {
        this("Data/Taxes.txt");
    }

    public FlooringMasteryTaxDaoFileImpl(String taxTextFile) throws FlooringMasteryPersistenceException {
        this.TAX_FILE = taxTextFile;
        allTaxes = new HashMap<>();
        loadFile();
        validateAllTaxes(allTaxes);
    }


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

        // We are guaranteed all state names are unique as allTaxes has a set of keys of state names
        // verify that all state codes are also unique
        HashSet<String> stateCodes = new HashSet<>();
        for (Tax t : taxes.values()) {
            // Ensure that the state name key points to a tax object with the same state name.
            if (!t.equals(taxes.get(t.getState()))) {
                throw new FlooringMasteryPersistenceException("Cannot have a state mapping to a Tax" +
                        " with a different state name");
            }
            if (!stateCodes.add(t.getStateAbr())) {
                throw new FlooringMasteryPersistenceException("Cannot have multiple tax entries with the same state code.");
            }
        }

    }


    private void loadFile() throws FlooringMasteryPersistenceException{

        // Tax file MUST have header line as first line of file qual to:
        // State,StateName,TaxRate

        // load file
        Scanner scanner;

        // open file
        try {
            // create scanner to read file
            scanner = new Scanner(new BufferedReader(new FileReader(TAX_FILE)));
        } catch (FileNotFoundException e) {
            throw new FlooringMasteryPersistenceException("Couldn't load tax data into memory.", e);
        }

        // current line holds most recent line read from file
        String currentLine;

        // hold current most recent unmarshalled Tax
        Tax currentTax;

        // if file is empty - return persistence exception - should contain header:
        if (!scanner.hasNextLine()) {
            throw new FlooringMasteryPersistenceException("Invalid tax file - no header line found.");
        }

        // Verify that header is correct

        String headerLine = scanner.nextLine();
        String[] headers = headerLine.split(DELIMITER);
        if (headers.length != 3 || !(headers[0].equals("State")
            && headers[1].equals("StateName")
            && headers[2].equals("TaxRate"))) {
            throw new FlooringMasteryPersistenceException("Invalid Tax File Header.");

        }


        // iterate over TAX_FILE body decode each line into tax
        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();
            // unmarshall line into tax
            currentTax = unmarshallTax(currentLine);

            // if tax is null throw exception - invalid tax
            if (currentTax == null) {
                throw new FlooringMasteryPersistenceException("Invalid tax read in tax file.");
            }
            // otherwise add to memory
            // use state name as key
            allTaxes.put(currentTax.getState(), currentTax);
        }

        // close scanner
        scanner.close();
    }

    private Tax unmarshallTax(String taxAsText) {
        /*
         * Expected input format for taxAsText entry in tax file.
         * <taxCode>,<stateName>,<taxRate>
         * We enforce taxCode to be uppercase, and taxRate to have scale 2.
         */
        // if empty, return null
        if (taxAsText == null || taxAsText.isEmpty()) return null;

        String[] taxPropertiesAsText = taxAsText.split(DELIMITER);

        // assume valid format

        return new Tax(taxPropertiesAsText[1], taxPropertiesAsText[0],
                new BigDecimal(taxPropertiesAsText[2]).setScale(2, RoundingMode.HALF_UP));

    }

}
