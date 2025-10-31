package com.sg.floormaster.dao;

import com.sg.floormaster.model.Order;
import com.sg.floormaster.service.FlooringMasteryInvalidInputException;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlooringMasteryOrderDaoFileImpl implements FlooringMasteryOrderDao{

    private Map<LocalDate, Map<Integer, Order>> orders;
    private final String ORDER_FOLDER;
    private final String DELIMITER = ",";

    // stores the largest order number that has been used for an order i.e. cannot be repeated.
    // note that the orders map may not yet contain an order with largestOrderNumber.
    // I.e. the largest order number stored in orders and largestOrderNumber are not directly tied.
    private int largestOrderNumber;

    // default constructor
    public FlooringMasteryOrderDaoFileImpl() {
        this("Orders");

    }

    public FlooringMasteryOrderDaoFileImpl(String orderDirectory) {
        orders = new HashMap<>();
        ORDER_FOLDER = orderDirectory;

        // load hashMap from file initially to initialise largestOrderNumber.
        loadFromFile();
        // call calculateLargestOrderNumber after loading.
        calculateLargestOrderNumber();
    }

    // add constructor that takes directory as input:

    public FlooringMasteryOrderDaoFileImpl(Map<LocalDate, Map<Integer, Order>> orders) {
        this.orders = orders;
        // calculate max order number
        calculateLargestOrderNumber();
        ORDER_FOLDER = "Orders";
    }

    private void calculateLargestOrderNumber() {
        // uses in-memory map to set largest order number ready for incrementing.

        // if map is null or void, set largest number to 0
        if (orders == null || orders.isEmpty()) {
            largestOrderNumber = 0;
        }

        // otherwise: create List<List<Int>>, iterate through each and find max.
        int currentMax = -1;
        List<Set<Integer>> orderNumbers = orders.values().stream().map((Map::keySet)).toList();

        for (Set<Integer> s : orderNumbers) {
            List<Integer> sList = (new ArrayList<>(s));
            sList.sort(Integer::compare); // ascending order.
            int sMax = sList.getLast(); // max value
            if (sMax > currentMax) {
                currentMax = sMax;
            }
        }

        // now have currentMax as max order number.
        largestOrderNumber = currentMax;
    }


    @Override
    public int getNextOrderNumber() {
        // increment largestOrderNumber to the next unused order ID number
        largestOrderNumber += 1;
        // return the new unused orderID number
        return largestOrderNumber;
    }

    @Override
    public Order addOrder(Order order) {
        // assumes it is passed a valid order with a valid input from the service layer

        // in-memory implementation:

        // if date already exists in the orders map - add to inner map
        // note that date key may exist, but inner map could be null,
        //
        // in either case (date key not in outer map or key does exist but inner map is null),
        // a new map containing the new object is set as the value for the outer map.
        if (orders.get(order.getOrderDate()) != null) {
            Map<Integer, Order> existingOrdersOnNewOrderDate = orders.get(order.getOrderDate());

            // map of orders on this date already exists, we put new order in this map,
            // and return a previous order with the same orderId if one existed.
            return existingOrdersOnNewOrderDate.put(order.getOrderNumber(), order);
        }

        // otherwise no existing orders exist for this new order's date
        // create new inner map, append the new order, and append this to the outer map.
        Map<Integer, Order> newMapOnNewOrderDate = new HashMap<>();
        newMapOnNewOrderDate.put(order.getOrderNumber(), order);
        // append to orders Map
        orders.put(order.getOrderDate(), newMapOnNewOrderDate);
        // return null as no previous order existed
        return null;
    }

    @Override
    public Order getOrder(LocalDate date, int orderId) {
        // check if date exits:
        if (orders.get(date) == null) {
            return null; // no order can be found.
        }
        // otherwise return result of querying inner order map.
        return orders.get(date).get(orderId);
    }

    @Override
    public Order editOrder(Order newOrder) throws FlooringMasteryNoSuchOrderException {

        // First check if querying for order date returns null
        if (orders.get(newOrder.getOrderDate()) == null ||
            orders.get(newOrder.getOrderDate()).get(newOrder.getOrderNumber()) == null) {
            throw new FlooringMasteryNoSuchOrderException("Existing order with ID " + newOrder.getOrderNumber()
            + " not found.");
        }

        // otherwise replace existing order with new order
        return orders.get(newOrder.getOrderDate()).put(newOrder.getOrderNumber(), newOrder);
    }

    @Override
    public List<Order> getOrdersForDate(LocalDate date) {
        // Could convert to a stream?
        return new ArrayList<>(orders.get(date).values());
    }

    @Override
    public Map<LocalDate, Map<Integer, Order>> getAllOrders() {
        // Returns a shallow copy so that no external layer can alter the Dao's structure.
        // Note that the map should not be altered
        return Map.copyOf(orders);
    }

    @Override
    public Order removeOrder(LocalDate date, int orderId) {
        // see if map exists for given date:
        if (orders.get(date) != null && orders.get(date).get(orderId) != null) {
            return orders.get(date).remove(orderId);
        }

        // otherwise no order was found, date may not yet exist, or order doesn't within date
        return null;
    }

    // load
        // make sure during marshalling/unmarshalling, replace order name's commas with special character like *, and put it back when you unmarshall.

    // be given name of directory?
    private void loadFromFile() throws FlooringMasteryPersistenceException {
        // need to get all files and filter by format
        // "Orders_MMDDYYYY.txt"
        // will validate that format is correct when trying to create date from the order.

        // get valid order files: //guaranteed to have format "Orders_dddddddd.txt" where d any integer.
        Map<String, LocalDate> validOrderFiles = getFilesInOrderDirectory();

        // for each file, load all entries into inner map, put inner map into outer map allOrders.

        for (String validOrderFileName : validOrderFiles.keySet()) {

            // create new inner Map<Integer, Order> ordersForCurrentFile
            Map<Integer, Order> ordersForCurrentFile = new HashMap<>();
            // open file
            Scanner scanner;
            try {
                // create scanner to read file
                scanner = new Scanner(new BufferedReader(new FileReader(ORDER_FOLDER+"/" + validOrderFileName)));
            } catch (FileNotFoundException e) {
                throw new FlooringMasteryPersistenceException("Couldn't load an order file.", e);
            }

            // currentline holds most recent line read from file
            String currentLine;

            // hold most recent unmarshalled Order
            Order currentOrder;

            // verify file contains non-empty first row
            if (!scanner.hasNextLine()) {
                // skip to next file
                scanner.close();
                continue;
            }

            // verify header is correct

            String headerLine = scanner.nextLine();
            String[] headers = headerLine.split(DELIMITER);
            if (headers.length != 12
                || !(headers[0].equals("OrderNumber")
                    && headers[1].equals("CustomerName")
                    && headers[2].equals("State")
                    && headers[3].equals("TaxRate")
                    && headers[4].equals("ProductType")
                    && headers[5].equals("Area")
                    && headers[6].equals("CostPerSquareFoot")
                    && headers[7].equals("LaborCostPerSquareFoot")
                    && headers[8].equals("MaterialCost")
                    && headers[9].equals("LaborCost")
                    && headers[10].equals("Tax")
                    && headers[11].equals("Total"))) {
                // skip to next file - header invalid.
                scanner.close();
                continue;
            }

            // have valid header - read input:

            while (scanner.hasNextLine()) {
                // while file has next line:
                // get unmarshalled order input
                currentLine = scanner.nextLine();
                currentOrder = unmarshallOrder(currentLine);

                // verify is not null - throw persistence exception
                if (currentOrder == null) {
                    scanner.close();
                    throw new FlooringMasteryPersistenceException("Error occurred parsing order properties");
                }

                // Must add order date to order:
                currentOrder.setOrderDate(validOrderFiles.get(validOrderFileName));

                // add to innerMap
                ordersForCurrentFile.put(currentOrder.getOrderNumber(), currentOrder);
            }

            // all lines processed in file add to entry in outer map
            orders.put(validOrderFiles.get(validOrderFileName), ordersForCurrentFile);

            // close scanner
            scanner.close();
        }

    }

    private Map<String, LocalDate> getFilesInOrderDirectory()  throws FlooringMasteryPersistenceException {

        Map<String, LocalDate> fileMap = new HashMap<>();

        // read all files in the directory.
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(ORDER_FOLDER))) {
            for (Path path : stream) {
                // validate that path is of correct format.
                String orderFileName = path.getFileName().toString();

                if (orderFileName.matches("Orders_\\d{8}.txt")) {
                    // valid pattern, check if the date is a valid date.
                    Pattern datePattern = Pattern.compile("\\d{8}");
                    Matcher dateMatcher = datePattern.matcher(orderFileName);

                    if (dateMatcher.find()) {
                        String orderDateAsText = dateMatcher.group();

                        // attempt to parse to dateTime with given format "MMddyyyy"
                        LocalDate orderDate;
                        try  {
                            orderDate = LocalDate.parse(orderDateAsText, DateTimeFormatter.ofPattern("MMddyyyy"));
                            // valid orderDate, add filename and order date to map of valid
                            fileMap.put(orderFileName, orderDate);
                        } catch (DateTimeParseException e) {
                            // invalid date format, don't add to the map of valid file names.
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new FlooringMasteryPersistenceException("Could not load data from orders directory.");
        }

        // return file map containing order file names with valid dates.
        return fileMap;
    }

    private Order unmarshallOrder(String orderAsText) {
        // expects input in following order:
        // "OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total"

        if (orderAsText == null || orderAsText.isEmpty()) return null;

        String[] orderPropertiesAsText = orderAsText.split(DELIMITER);

        // assume valid format

        // create attributes
        Order newOrder = new Order();

        // order number
        newOrder.setOrderNumber(Integer.parseInt(orderPropertiesAsText[0]));

        // Customer name:
        // an asterisk is used in place of commas to persist customer name, must convert asterisk back to comman.
        newOrder.setCustomerName(orderPropertiesAsText[1].replace('*', ','));

        // State:
        newOrder.setState(orderPropertiesAsText[2]);

        // TaxRate
        newOrder.setTaxRate(new BigDecimal(orderPropertiesAsText[3]).setScale(2, RoundingMode.HALF_UP));

        // Product Type
        newOrder.setProductType(orderPropertiesAsText[4]);

        // area
        newOrder.setArea(new BigDecimal(orderPropertiesAsText[5]).setScale(2, RoundingMode.HALF_UP));

        // Cost per square foot
        newOrder.setCostPerSquareFoot(new BigDecimal(orderPropertiesAsText[6]).setScale(2, RoundingMode.HALF_UP));

        // laborcost per square foot
        newOrder.setLaborCostPerSquareFoot(new BigDecimal(orderPropertiesAsText[7]).setScale(2, RoundingMode.HALF_UP));

        // material cost
        newOrder.setMaterialCost(new BigDecimal(orderPropertiesAsText[8]).setScale(2, RoundingMode.HALF_UP));

        // labord csot
        newOrder.setLaborCost(new BigDecimal(orderPropertiesAsText[9]).setScale(2, RoundingMode.HALF_UP));

        // tax
        newOrder.setTax(new BigDecimal(orderPropertiesAsText[10]).setScale(2, RoundingMode.HALF_UP));

        // total
        newOrder.setTotal(new BigDecimal(orderPropertiesAsText[11]).setScale(2, RoundingMode.HALF_UP));

        // return unmarshalled order
        return newOrder;
    }

    @Override
    public void saveOrders() throws FlooringMasteryPersistenceException {
        writeToFiles();
    }

    private void writeToFiles() throws FlooringMasteryPersistenceException {
        // overwrites previous order files.
        String orderHeaderLine = "OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total";

        // for each date, generate correct filename, then marshall all data and write to file.

        for (LocalDate fileDate : orders.keySet()) {
            PrintWriter out;

            // generate file path.
            String filePath = generateOrderFilePath(fileDate);

            try {
                out = new PrintWriter(new FileWriter(filePath));
            } catch (IOException e) {
                throw new FlooringMasteryPersistenceException("Could not save order data.");
            }

            // Write header to the file:
            out.println(orderHeaderLine);

            // write each Order to file (just use values of inner map)

            String orderAsText;
            List<Order> allOrdersForCurrentDate = getOrdersForDate(fileDate);

            for (Order currentOrder : allOrdersForCurrentDate) {
                // marshal to string
                orderAsText = marshallOrder(currentOrder);

                // write string to file
                out.println(orderAsText);

                // force printWriter to write to line
                out.flush();
            }
            out.close();
        }
    }

    private String generateOrderFilePath(LocalDate orderDate) {
        String dateFormatted = orderDate.format(DateTimeFormatter.ofPattern("MMddyyyy"));

        // valid order file = "<ORDER_FOLDER>/Orders_MMddyyyy.txt";
        return ORDER_FOLDER+"/"+"Orders_"+dateFormatted+".txt";
    }

    private String marshallOrder(Order order) {
        // Marshalls to format:
        // OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total
        // Note to preserve use of DELIMITER = ',', we must replace any commas in customerName with an asterisk
        // to allow unmarshalling to recognise delimiter.

        String orderAsText = "";

        // add OrderNumber
        orderAsText += order.getOrderNumber()
                    + DELIMITER;

        // add customer name
        // replace any commas with asterisk.
        orderAsText += order.getCustomerName().replace(',', '*')
                + DELIMITER;

        // add state
        orderAsText += order.getState()
                + DELIMITER;

        // taxrate
        orderAsText += order.getTaxRate().setScale(2, RoundingMode.HALF_UP).toString()
                + DELIMITER;

        // product type
        orderAsText += order.getProductType()
                + DELIMITER;

        // area
        orderAsText += order.getArea().setScale(2, RoundingMode.HALF_UP).toString()
                + DELIMITER;

        // cost per square foot
        orderAsText += order.getCostPerSquareFoot().setScale(2, RoundingMode.HALF_UP).toString()
                + DELIMITER;

        // labor cost per square foot
        orderAsText += order.getLaborCostPerSquareFoot().setScale(2, RoundingMode.HALF_UP).toString()
                + DELIMITER;

        // material cost
        orderAsText += order.getMaterialCost().setScale(2, RoundingMode.HALF_UP).toString()
                + DELIMITER;

        // labor cost
        orderAsText += order.getLaborCost().setScale(2, RoundingMode.HALF_UP).toString()
                + DELIMITER;

        // tax
        orderAsText += order.getTax().setScale(2, RoundingMode.HALF_UP).toString()
                + DELIMITER;

        // total
        orderAsText += order.getTotal().setScale(2, RoundingMode.HALF_UP).toString();
        // don't add delimiter

        return orderAsText;
    }
}
