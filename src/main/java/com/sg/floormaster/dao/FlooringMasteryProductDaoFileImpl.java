package com.sg.floormaster.dao;

import com.sg.floormaster.model.Product;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class FlooringMasteryProductDaoFileImpl implements FlooringMasteryProductDao {

    private Map<String, Product> allProducts; // productType as Key

    // Implement when adding persistence
     private final String PRODUCT_FILE;
     private final String DELIMITER = ",";

    public FlooringMasteryProductDaoFileImpl() throws FlooringMasteryPersistenceException {
        this("Data/Products.txt");

    }

    public FlooringMasteryProductDaoFileImpl(Map<String, Product> products) {
        validateAllProducts(products);
        this.allProducts = products;
        this.PRODUCT_FILE = "Data/Products.txt";

    }

    public FlooringMasteryProductDaoFileImpl(String productTextFile) throws FlooringMasteryPersistenceException {
        this.PRODUCT_FILE = productTextFile;
        // initialise map
        allProducts = new HashMap<>();
        loadFile();
        validateAllProducts(allProducts);
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(allProducts.values());
    }

    private void loadFile() throws FlooringMasteryPersistenceException {
        // Loads persisted data from PRODUCT_FILE to memory

        // PRODUCT_FIL must have header line as first line of file equal to:
        // "ProductType,CostPerSquareFoot,LaborCostPerSquareFoot"

        // create scanner
        Scanner scanner;

        // open file
        try {
            scanner = new Scanner(new BufferedReader(new FileReader(PRODUCT_FILE)));
        } catch (FileNotFoundException e) {
            throw new FlooringMasteryPersistenceException("Couldn't load file data into memory.");
        }

        // current line holds most recent line read from file
        String currentLine;

        // holds most recent unmarshalled Product
        Product currentProduct;

        // if file empty - return persistence exception - should contain at least header row
        if (!scanner.hasNextLine()) {
            throw new FlooringMasteryPersistenceException("Invalid Products file - no header line found");
        }

        // verify header is correct format

        String headerLine = scanner.nextLine();
        String[] headers = headerLine.split(DELIMITER);
        if (headers.length != 3 || !(headers[0].equals("ProductType")
                && headers[1].equals("CostPerSquareFoot")
                && headers[2].equals("LaborCostPerSquareFoot"))) {
            throw new FlooringMasteryPersistenceException("Invalid Product File Header.");
        }

        // otherwise valid header - iterate over product file
        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine();

            // unmarshall line into product
            currentProduct = unmarshallProduct(currentLine);

            // if product null throw exception - invalid product
            if (currentProduct == null) {
                throw new FlooringMasteryPersistenceException("Invalid product read in product file.");
            }

            // otherwise add to memory
            allProducts.put(currentProduct.getProductType(), currentProduct);
        }

        // close scanner
        scanner.close();
    }

    private Product unmarshallProduct(String productAsText) throws FlooringMasteryPersistenceException {
        /*
         * Expected input format for productAsText:
         * <ProductType>,<costPerSquareFoot>,<laborCostPerSquareFoot>
         */

        // if empty, return null - no product can be made
        if (productAsText == null || productAsText.isEmpty()) return null;

        String[] productPropertiesAsText = productAsText.split(DELIMITER);

        // try to return valid product.
        try {
            return new Product(productPropertiesAsText[0],
                    new BigDecimal(productPropertiesAsText[1]).setScale(2, RoundingMode.HALF_UP),
                    new BigDecimal(productPropertiesAsText[2]).setScale(2, RoundingMode.HALF_UP));
        } catch (Exception e) {
            throw new FlooringMasteryPersistenceException("Could not parse product.", e);
        }


    }


    private void validateAllProducts(Map<String, Product> products) throws FlooringMasteryPersistenceException {
        // we are guaranteed that keys ensure product types are unique strings
        // 1. Check for null reference
        if (products == null) {
            throw new FlooringMasteryPersistenceException("Internal store of products cannot be null.");
        }

        // 2. Ensure that all productType keys align with the productType of the corresponding Product object.
        for (Product p  : products.values()) {
            if (!p.equals(products.get(p.getProductType()))) {
                throw new FlooringMasteryPersistenceException("Cannot have a productType key mapping to a Product" +
                        " with a different productType");
            }
        }
    }
}
