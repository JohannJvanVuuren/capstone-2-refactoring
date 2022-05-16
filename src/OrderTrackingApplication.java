import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Scanner;

import javax.swing.text.MaskFormatter;

/*
 * This is a simple program to track orders for the Quick Food company. This
 * company takes orders from customers, allocate a driver to deliver the order
 * based on customer and restaurant location and driver workload. It finally
 * sends the customer an invoice.
 */
public class OrderTrackingApplication {

  /*
   * ArrayList to store the different meal types that are added to the order.
   * Declared "globally" because it is needed in the main() and
   * calculateCostOfOrder() method.
   */
  public static ArrayList<Meal> listOfMealsOrdered = new ArrayList<>();

  public static void main(String[] args) {

    /*
     * Declaration and initialisation of a variable to keep track of the order
     * number of each order.
     */
    int orderNumber = 0;

    /* While loop to allow the user to exit the program when so chosen. */
    while (true) {

      orderNumber = orderNumber + 1;
      System.out.println("Welcome to the Order Tracking Application.");

      /*
       * Creation of instances of the Customer and Restaurant objects needed for the
       * driver allocation and invoice creation.
       */
      Customer currentCustomer = createCustomer();
      Restaurant currentRestaurant = createRestaurant();

      /*
       * While loop to allow multiple meal types to be added to the same order number
       * and to exit when there are no more meals to add.
       */
      while (true) {

        /*
         * Creating an instance of Meal for each item ordered and adding it the
         * totalOrder ArrayList.
         */
        Meal itemOrdered = getOrderInfo();
        listOfMealsOrdered.add(itemOrdered);

        /*
         * Determination via user input whether the while loop should continue running.
         * I.e., Whether there are more items to add.
         */
        String addAnotherItemToList = getInputFromUser("Would you like to add another item? (Y/N)");

        if (addAnotherItemToList.equalsIgnoreCase("n")) {
          break;
        }

      }

      String specialInstructions = getInputFromUser("Are there any special instructions?");

      /*
       * Calculation of the total cost of the order using the calculateCostOfOrder()
       * method.
       */
      double totalCostOfOrder = calculateCostOfOrder();

      /*
       * Allocation of the driver using the allocateDriver() method which outputs a
       * string that either tells the customer that there are no drivers in their area
       * or a string with the name of the driver that will do the delivery.
       */
      String allocatedDriverName = allocateDriver(currentRestaurant.getLocation(), currentCustomer.getLocation());

      /*
       * Creation and printing to file of the invoice using the createInvoice() method
       * where all the parameters are the elements needed for the invoice. The
       * writeInvoiceToFile() method is used to write the information to file, line by
       * line.
       */
      createInvoice(currentCustomer, currentRestaurant, orderNumber, specialInstructions, totalCostOfOrder,
          allocatedDriverName);

      /*
       * Input from the user (via the getInputFromUser() method) to either continue
       * with another customer or to terminate the program. Then updating of the while
       * statement's conditional variable based on the user's input. If false the
       * program terminates, else it continues.
       */
      String anotherCustomer = getInputFromUser("Would you like to continue to another customer? (Y/N)");

      if (anotherCustomer.equalsIgnoreCase("n")) {
        System.out.println("Thank you for using the Order Tracking Application. Goodbye.");
        break;
      }

    }

  }

  /*
   * Generic method for reading input from user to keep code as DRY as possible.
   */
  public static String getInputFromUser(String inputRequest) {
    /*
     * https://www.javatpoint.com/java-bufferedreader-class. Reference used for the
     * use of BufferReader with InputStreamReader.
     */
    String readInput = "";
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    System.out.println(inputRequest);
    try {
      readInput = reader.readLine();
    } catch (IOException e) {
      e.getMessage();
      e.printStackTrace();
      System.out.println("Error: IOException - getInputFromUser()");
    }

    return readInput;

  }

  /*
   * Method to create and return an instance of the Customer object for the
   * current customer. Invoked in main.
   */
  public static Customer createCustomer() {
    System.out.println("Please provide the customer details.");

    /*
     * Reading user input of variables needed to construct a Customer object by
     * means of the generic getInputFromUser() method.
     */
    String name = getInputFromUser("First name: ");
    String surname = getInputFromUser("Surname: ");
    String contactNumber = getInputFromUser("Contact number:(No spaces or punctuation.) ");
    String address = getInputFromUser("Address: ");
    String location = getInputFromUser("Location (City): ");
    String email = getInputFromUser("Email: ");

    /* Creation of a Customer object using the constructor. */
    Customer currentCustomer = new Customer(name, surname, contactNumber, address, location, email);

    return currentCustomer;

  }

  /*
   * Method to create and return an instance of the Restaurant object for the
   * current customer. Invoked in main.
   */
  public static Restaurant createRestaurant() {
    System.out.println("Please enter the restaurant's details.");

    /*
     * Reading user input of variables needed to construct a Restaurant object by
     * use of the generic getInputFromUser() method.
     */
    String restaurantName = getInputFromUser("Name of restaurant: ");
    String location = getInputFromUser("Location (City): ");
    String contactNumber = getInputFromUser("Contact number: (No spaces or punctuation.)");

    /* Creation of a Restaurant object using the constructor */
    Restaurant currentRestaurant = new Restaurant(restaurantName, location, contactNumber);

    return currentRestaurant;

  }

  /*
   * A method to create and return an instance of the Meal object based on input
   * from the user.
   */
  public static Meal getOrderInfo() {
    System.out.println("Please enter the order details.");

    /*
     * Reading user input of variables needed to construct a Meal object with the
     * generic getInputFromUser() method. The price and numberOfUnits are validated
     * in the calculateCostOfOrder() method, to make sure that the user enters
     * numbers that can be cast from String to double.
     */
    String mealName = getInputFromUser("Menu item name: ");
    String price = getInputFromUser("Item price: ");
    String numberOfUnits = getInputFromUser("Number of units: ");

    Meal currentMeal = new Meal(mealName, price, numberOfUnits);

    return currentMeal;

  }

  /* Method to allocate driver based on location and workload. */
  public static String allocateDriver(String restaurantLocation, String customerLocation) {

    /*
     * Declaration and assignment of string that will be returned from this method
     * to main() where it will be invoked. The string covers two scenarios. One in
     * which there are no driver locations that match the customer's location and
     * the other where a driver is allocated and the driver's name appears in the
     * returned string.
     */
    String allocatedDriverString = "";

    /*
     * Read each line of the "drivers.txt" file one by one and store each as a
     * String in an ArrayList. The elements in this ArrayList will be the respective
     * drivers with their details.
     */
    ArrayList<String> drivers = readFile();

    /**
     * Determine if there is a driver in the same location as the customer and
     * allocating an appropriate string if that is not the case.
     */
    allocatedDriverString = isDriverInRange(customerLocation, drivers);

    /**
     * Allocate driver by same location as restaurant and then lowest workload.
     */
    allocatedDriverString = allocateDriversByLocationAndWorkload(restaurantLocation, drivers);

    return allocatedDriverString;

  }

  /**
   * Grouping all the drivers in the same location as the restaurant, together in
   * an ArrayList where their workloads will be compared using an ArrayList in
   * which all workloads are stored as integers. This comparison of workloads will
   * be used to determine the driver with the lowest load, whose name will be used
   * to determine the value of the variable allocatedDriverString.
   */
  public static String allocateDriversByLocationAndWorkload(String restaurantLocation, ArrayList<String> drivers) {

    String allocatedDriverString;
    ArrayList<String> commonLocation = new ArrayList<>();
    ArrayList<Integer> workloadList = new ArrayList<>();

    for (int i = 0; i < drivers.size(); i++) {
      String[] driverDetails = drivers.get(i).split(",");
      if (driverDetails[1].trim().equalsIgnoreCase(restaurantLocation)) {
        commonLocation.add(drivers.get(i));
        workloadList.add(Integer.parseInt(driverDetails[2].trim()));
      }
    }

    /*
     * Comparing workloads and allocating that driver's name in the variable
     * allocateDriverName which will be used to construct the returned string. The
     * comparison is started assuming the first driver has the lowest workload and
     * his/her index number and details are stored in these two variables.
     */
    int indexOfMinWorkLoad = 0;

    for (int i = 0; i < commonLocation.size(); i++) {
      int minWorkload = workloadList.get(0);
      if (workloadList.get(i) < minWorkload) {
        minWorkload = workloadList.get(i);
        indexOfMinWorkLoad = i;
      }
    }

    /*
     * The commonLocation and workLoadList ArrayLists have identical indexes so the
     * index determined immediately above could be used to create an array of the
     * relevant driver's details so that his/her name can be extracted and
     * incorporated into the returned string.
     */
    String[] allocatedDriverDetails = commonLocation.get(indexOfMinWorkLoad).split(",");
    allocatedDriverString = allocatedDriverDetails[0]
        + " is nearest to the restaurant and so s/he will be delivering your order to you at:\n";

    return allocatedDriverString;
  }

  /**
   * Method to determine if there is a driver in the same location as the customer
   * and to return an appropriate message when that is not the case. This method
   * is invoked as a "helper method in allocateDriver().
   */
  public static String isDriverInRange(String customerLocation, ArrayList<String> drivers) {
    /*
     * Creating an ArrayList that contains a list of driver locations only. The
     * ArrAyList contains() method is then used on the ArrayList together with an if
     * statement to check if the customer's location is present in the list. If the
     * location does not appear the boolean variable, doesLocationsMatch is changed
     * from true to false which will trigger a specific return string that there is
     * no driver in the customer's location.
     */
    ArrayList<String> driverLocationList = new ArrayList<>();

    for (int i = 0; i < drivers.size(); i++) {
      String[] driverInfo = drivers.get(i).split(",");
      driverLocationList.add(driverInfo[1].trim());
    }

    if (driverLocationList.contains(customerLocation) != true) {
      String driverNotInRange = "Sorry! Our drivers are too far away from you to be able to deliver to your location:\n";
      return driverNotInRange;
    }

    return null;

  }

  /* Method to calculate the totalCost of each order */
  public static double calculateCostOfOrder() {

    /* Declaration and instantiation of a variable to hold the total */
    double totalCost = 0.00;

    /*
     * For loop to iterate through the ArrayList with the different Meal instances
     * ("global variable"). The numberOfUnits and price variables of each instance
     * is converted to doubles which are used to calculate a sub total for each
     * instance which is added to the totalCost variable of the order.
     */
    for (int i = 0; i < listOfMealsOrdered.size(); i++) {
      Meal currentMeal = listOfMealsOrdered.get(i);
      /*
       * Defensive programming to make sure the user entered numbers in the first two
       * variables that can be cast from String to double.
       */
      try {
        double numberOfUnitsAsDouble = Double.parseDouble(currentMeal.getNumberOfUnits());
        double priceAsDouble = Double.parseDouble(currentMeal.getPrice());
        double subTotal = numberOfUnitsAsDouble * priceAsDouble;
        totalCost += subTotal;
      } catch (NumberFormatException e) {
        e.getMessage();
        e.printStackTrace();
        System.out.println("calculateCostOfOrder() - doubles must be entered in price and number of units fields.");
      }
    }

    /* Manipulation to ensure a double digit decimal is generated and returned. */
    totalCost = Math.round(totalCost * 100.00) / 100.00;

    return totalCost;

  }

  /*
   * A generic method to convert a telephone number of format "nnnnnnnnnn" to
   * "nnn nnn nnnn". Ref:
   * https://stackoverflow.com/questions/5114762/how-do-format-a-phone-number-as-a
   * -string-in-java. Reference used to obtain the method of doing this.
   */
  public static String formatTelephoneNumber(String phoneNumber) {
    /* Specifying the desired format of the string. */
    String phoneMask = "### ### ####";
    String rawPhoneNumber = phoneNumber;

    /*
     * Declaring and instantiating the variable that will be returned from this
     * method when invoked.
     */
    String transformedPhoneNumber = "";
    try {
      /*
       * Creating an instance of the MaskFormatter class with the desired format as
       * argument.
       */
      MaskFormatter maskFormatter = new MaskFormatter(phoneMask);

      /*
       * Setting the instance to ignore the character # used in the mask if it occurs
       * in the raw string.
       */
      maskFormatter.setValueContainsLiteralCharacters(false);

      /*
       * Assigning the value of the transformed string to the return variable of the
       * method.
       */
      transformedPhoneNumber = maskFormatter.valueToString(rawPhoneNumber);
    } catch (ParseException e) {
      e.getMessage();
      e.printStackTrace();
      System.out.println("Error: Telephone number entered with spaces, punctuation or additional digits.");
    }

    return transformedPhoneNumber;

  }

  /*
   * Creation of the individual strings that is used to make up the invoice. The
   * strings are dynamically printed to file as they are create in this method
   * using the writeInvoiceToFile() method.
   */
  public static void createInvoice(Customer currentCustomer, Restaurant currentRestaurant, int orderNumber,
      String specialInstructions, double costOfOrder, String allocatedDriver) {

    /*
     * Declaration and instantiation of the variable that will be used to hold the
     * entire StringWriter content that will be printed to file in the
     * writeInvoiceToFile() method.
     */

    /*
     * Ref: https://www.baeldung.com/java-multiline-string. Reference used to select
     * a way of printing multiple strings.
     */
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);

    printWriter.println("Order Number " + orderNumber + "\n");
    printWriter.println("Customer: " + currentCustomer.getName() + " " + currentCustomer.getSurname() + "\n");
    printWriter.println("Email: " + currentCustomer.getEmail() + "\n");
    printWriter.println("Phone number: " + formatTelephoneNumber(currentCustomer.getContactNumber()) + "\n");
    printWriter.println("Location: " + currentCustomer.getLocation() + "\n");
    printWriter.println("You have ordered the following from " + currentRestaurant.getRestaurantName() + " in "
        + currentRestaurant.getLocation() + ":\n");

    /* Handling the printing of multiple meals as generated in main(). */
    for (int i = 0; i < listOfMealsOrdered.size(); i++) {
      Meal individualOrder = listOfMealsOrdered.get(i);
      printWriter.println(individualOrder.getNumberOfUnits() + " x " + individualOrder.getMealName() + " (R"
          + individualOrder.getPrice() + ")" + "\n");
    }

    printWriter.println("Special Instructions: " + specialInstructions + "\n");
    printWriter.println("Total: " + costOfOrder + "\n");
    /* The string is returned from the method when allocateDriver is invoked. */
    printWriter.println(allocateDriver(currentRestaurant.getLocation(), currentCustomer.getLocation()));
    printWriter.println(currentCustomer.getAddress());
    printWriter.println(currentCustomer.getLocation() + "\n");
    printWriter.println("If you need to contact the restaurant, their number is "
        + formatTelephoneNumber(currentRestaurant.getContactNumber()) + "." + "\n\n\n");

    writeInvoiceToFile(stringWriter.toString());

  }

  /**
   * Generic method to read a file line by line. This method is invoked in the
   * allocateDriver() method.
   */
  public static ArrayList<String> readFile() {
    ArrayList<String> drivers = new ArrayList<>();

    try {
      String driversFile = "drivers.txt";
      File driverList = new File(driversFile);
      Scanner scan = new Scanner(driverList);
      while (scan.hasNextLine()) {
        drivers.add(scan.nextLine());
      }
      scan.close();
    } catch (FileNotFoundException e) {
      e.getMessage();
      e.printStackTrace();
      System.out.println("Error: File not found - readFile().");
    }
    return drivers;
  }

  /*
   * A generic method to handle the printing of the invoice based on the String
   * generated in the createInvoice() method immediately above.
   */
  public static void writeInvoiceToFile(String lineToPrint) {
    try {
      String fileToWriteTo = "invoice.txt";
      FileWriter writer = new FileWriter(fileToWriteTo, true);
      Formatter form = new Formatter(writer);
      form.format("%s", lineToPrint + "\n");
      form.close();
    } catch (IOException e) {
      e.getMessage();
      e.printStackTrace();
      System.out.println("Error: IOException - writeInvoiceToFile()");
    }

  }
}
