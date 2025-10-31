package com.sg.floormaster.validation;

import com.sg.floormaster.dao.FlooringMasteryPersistenceException;
import com.sg.floormaster.model.Order;
import com.sg.floormaster.model.Product;
import com.sg.floormaster.model.Tax;
import com.sg.floormaster.service.FlooringMasteryDuplicateOrderException;
import com.sg.floormaster.service.FlooringMasteryInvalidInputException;

import javax.sound.sampled.Port;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderValidation {

    /**
     * Validates given order against all validation rules -
     * @param order order to validate
     * @param allTaxes all tax objects stored
     * @param allProducts all product objects stored
     * @throws FlooringMasteryInvalidInputException if any validation fails
     * @throws FlooringMasteryPersistenceException if a validation fails.
     */
    public static void validateOrder(Order order, List<Tax> allTaxes, List<Product> allProducts, LocalDate dateOfInput)
            throws FlooringMasteryInvalidInputException,
            FlooringMasteryPersistenceException {

        // Do not validate order ID, we may be editing an order which will have same ID as existing.

        // Validate following Order properties:
        /*
         * 1. Order Date - must be after given dateOfInput, or is automatically valid if date of Input is Null.
         * 2. Customer Name - cannot be blank and must be limited to characters [0-9][a-z][A-Z]','' ''.'.
         *                    can contain whitespace.
         * 3. State - state must be checked against all states - ensure it exists.
         * 4. Product Type - must be in stored products
         * 5. area - must be positive decimal, min size is 100 sq ft. Check BigDecimal has scale 2.
         * 6. taxRate - must equal the entry in the tax store for corresponding state
         * 7. costPerSquareFoot - must equal entry in Product Store
         * 8. laborCostPerSquareFoot - must equal entry in product store.
         * */

        // check order is not null
        if (order == null) {
            throw new FlooringMasteryInvalidInputException("order contains null reference.");
        }

        // 1. order date
        validateOrderDate(order.getOrderDate(), dateOfInput);

        // 2. Customer name
        // if the name contains anything except the allowed characters, throw exception.
        validateCustomerName(order.getCustomerName());

        // 3. State
        validateState(allTaxes, order.getState());

        // 4. ProductType - must be in list of products
        validateProductType(allProducts, order.getProductType());

        // 5. Area - positive decimal, min size 100 sq ft.
        validateArea(order.getArea());

        // 6. taxRate
        validateTaxRate(allTaxes, order);

        // 7. cost per square foot
        validateCostPerSquareFoot(allProducts, order);

        // 8. laborCostPerSquareFoot
        validateLaborCostPerSquareFoot(allProducts, order);

    }

    /**
     * Validates order date, returns given order date if it is valid, throws exception otherwise.
     * Order date is valid if it is after the given dateOfInput. i.e. an order date must be in the future.
     * if date of input null, returns original orderDate - i.e. date need not be after a certain date.
     * @param orderDate order's date to validate
     * @param dateOfInput date at which order was input into the system.
     * @return  orderDate if it is valid, throws exception otherwise.
     * @throws FlooringMasteryInvalidInputException if orderDate is before the dateOfInput.
     */
    public static LocalDate validateOrderDate(LocalDate orderDate, LocalDate dateOfInput) throws FlooringMasteryInvalidInputException{
        if (dateOfInput == null) {
            // If no input date given, then orderdate is valid.
            return orderDate;
        }
        if (!orderDate.isAfter(dateOfInput)) {
            throw new FlooringMasteryInvalidInputException("Order must be in the future");
        }
        // otherwise valid date
        return orderDate;
    }

    /**
     * returns given customerName if it is valid, throws FlooringMasteryInvalidInputException if not valid.
     * valid customer names must only contain upper or lower case characters, digits 0-9, commas, whitespace and full stops.
     * customerName cannot be empty or null.
     * @param customerName customer name to validate
     * @return customerName given if it is valid, throws exception if not.
     * @throws FlooringMasteryInvalidInputException if customerName not valid.
     */
    public static String validateCustomerName(String customerName) throws FlooringMasteryInvalidInputException{
        // if string is empty or null, throw invalidException
        if (customerName == null || customerName.isEmpty()) {
            throw new FlooringMasteryInvalidInputException("Customer name cannot be empty or null.");
        }

        // if the name contains anything except the allowed characters, throw exception.
        Pattern notLegalCharacters = Pattern.compile("[^a-zA-Z0-9., ]", Pattern.CASE_INSENSITIVE);
        Matcher matchCustomerName = notLegalCharacters.matcher(customerName);

        // if illegal character found, throw exception
        if (matchCustomerName.find()) {
            throw new FlooringMasteryInvalidInputException("Customer name can only contain characters [A-Z], [a-Z], [0-9], ',', '.', ' '.");
        }

        // otherwise return valid name
        return customerName;
    }

    /**
     * Validates that a given state name matches a tax object in given list of tax objects.
     * If Tax object exists with given state name, returns original orderState input.
     * Throws FlooringMasteryInvalidInputException if no such Tax Object found.
     * @param taxes list of all valid tax objects.
     * @param orderState state name of the order to be validated.
     * @return orderState if a Tax object exists with given stateName.
     * @throws FlooringMasteryPersistenceException if state name not valid.
     */
    public static String validateState(List<Tax> taxes, String orderState) throws FlooringMasteryInvalidInputException {
        List<String> validStates = taxes.stream().map(Tax::getState).toList();
        if (!validStates.contains(orderState)) {
            throw new FlooringMasteryInvalidInputException("State name wasn't found in store of states");
        }

        // otherwise return valid orderState
        return orderState;
    }

    /**
     * Validates that a given productType matches a product object in given list of product objects.
     * If product exists with given productType, returns original orderProductType input
     * Throws FlooringMasteryInvalidInputException if no such product object found.
     * @param products list of all valid products.
     * @param orderProductType product type of order to be validated.
     * @return orderProductType if product object exists with given product type.
     * @throws FlooringMasteryInvalidInputException if no product found.
     */
    public static String validateProductType(List<Product> products, String orderProductType)
                                            throws FlooringMasteryInvalidInputException {

        List<String> validProductTypes = products.stream().map(Product::getProductType).toList();
        if (!validProductTypes.contains(orderProductType)) {
            throw new FlooringMasteryInvalidInputException("Order's product type wasn't found in store of valid productTypes.");
        }

        // otherwise return valid product type
        return orderProductType;
    }

    /**
     * Validates that given order area is positive and greater than the minimum area of 100 square feet.
     * Returns orderArea if valid, throws FlooringMasteryInvalidInputException if not.
     * @param orderArea order's area to validate
     * @return orderArea if valid, throw exception otherwise.
     * @throws FlooringMasteryInvalidInputException if orderArea not above minimum.
     */
    public static BigDecimal validateArea(BigDecimal orderArea) throws FlooringMasteryInvalidInputException {
        // define minimum area.
        BigDecimal minArea = new BigDecimal("100.00").setScale(2, RoundingMode.HALF_UP);
        if (orderArea.compareTo(minArea) < 0) {
            throw new FlooringMasteryInvalidInputException("Order's area is less than minimum size "+ minArea.toString());

        }

        // otherwise return valid area
        return orderArea;
    }

    /**
     * Validates that order has valid tax rate corresponding to the tax object in the store of taxes.
     * @param taxes list of all taxes
     * @param order order to validate.
     * @return valid tax rate of given order object.
     * @throws FlooringMasteryInvalidInputException
     * @throws FlooringMasteryPersistenceException
     */
    public static BigDecimal validateTaxRate(List<Tax> taxes, Order order)
                                            throws FlooringMasteryInvalidInputException,
                                                    FlooringMasteryPersistenceException{
        // get the Tax object corresponding to this order's state
        List<Tax> taxesWithStateOfOrder = taxes.stream()
                .filter((t) -> t.getState().equals(order.getState()))
                .toList();
        if (taxesWithStateOfOrder.size() > 1) {
            throw new FlooringMasteryPersistenceException("more than one Tax record found for given state.");
        }
        else if (taxesWithStateOfOrder.isEmpty()) {
            throw new FlooringMasteryInvalidInputException("No tax record found for order's state.");
        }
        // then validate order has same info as the single valid tax object.
        if (taxesWithStateOfOrder.getFirst().getTaxRate().compareTo(order.getTaxRate()) != 0) {
            throw new FlooringMasteryInvalidInputException("order has different tax rate to tax store for the given state.");
        }

        // otherwise validTaxRate
        return order.getTaxRate();
    }

    /**
     * Validates that cost per square foot of given order matches that found in the store of products.
     * Throws exception if no product found to match product type of given order, if more than one product exists with same
     * product type, or if the order has a different cost per square foot to the corresponding product record.
     * @param products list of products to search for valid product record.
     * @param order order to validate
     * @return valid cost per square foot of the order that matches the product record.
     * @throws FlooringMasteryInvalidInputException if no corresponding product record found for order's product type,
     * or if order has different cost per square foot to corresponding product object.
     * @throws FlooringMasteryPersistenceException if product list contains two products with same product type.
     */
    public static BigDecimal validateCostPerSquareFoot(List<Product> products, Order order)
                                                    throws FlooringMasteryInvalidInputException,
                                                            FlooringMasteryPersistenceException {
        // get the product object corresponding to this order's product type
        List<Product> validProducts = products.stream()
                .filter((p) -> p.getProductType().equals(order.getProductType()))
                .toList();
        if (validProducts.size() > 1) {
            throw new FlooringMasteryPersistenceException("more than one Product found for given product type.");
        }
        else if (validProducts.isEmpty()) {
            throw new FlooringMasteryInvalidInputException("No product record found for order's productType.");
        }
        // then validate order has same cost per square foot as the single valid product object
        if (validProducts.getFirst().getCostPerSquareFoot().compareTo(order.getCostPerSquareFoot()) != 0) {
            throw new FlooringMasteryInvalidInputException(
                    "order has different cost per square foot to product stored for the given productType.");
        }

        // otherwise valid costPerSquareFoot
        return order.getCostPerSquareFoot();
    }

    /**
     * Validates that labor cost per square foot of given order matches that of product object in store of products.
     * Throws exception if no product found to match product type of given order, if more than one product exists with same
     * product type, or if the order has a different labor cost per square foot to the corresponding product record.
     * @param products list of products to search for valid product record.
     * @param order order to validate
     * @return valid labor cost per square foot of the order that matches the product record.
     * @throws FlooringMasteryInvalidInputException if no corresponding product found for order's product type,
     * or if order has different labor cost per square foot to corresponding product object.
     * @throws FlooringMasteryPersistenceException if product list contains two products with same product type.
     */
    public static BigDecimal validateLaborCostPerSquareFoot(List<Product> products, Order order)
            throws FlooringMasteryInvalidInputException,
            FlooringMasteryPersistenceException {
        // get the product object corresponding to this order's product type
        List<Product> validProducts = products.stream()
                .filter((p) -> p.getProductType().equals(order.getProductType()))
                .toList();
        if (validProducts.size() > 1) {
            throw new FlooringMasteryPersistenceException("more than one Product record found for given product type.");
        }
        else if (validProducts.isEmpty()) {
            throw new FlooringMasteryInvalidInputException("No product record found for order's productType.");
        }
        // then validate order has same info as the single valid product object.
        if (validProducts.getFirst().getLaborCostPerSquareFoot().compareTo(order.getLaborCostPerSquareFoot()) != 0) {
            throw new FlooringMasteryInvalidInputException(
                    "order has different labor cost per square foot to product stored for the given productType.");
        }

        // otherwise valid costPerSquareFoot
        return order.getLaborCostPerSquareFoot();
    }
}
