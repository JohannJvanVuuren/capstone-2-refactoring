/*
 * Method to create instances of Restaurant objects in the main method of the
 * program.
 */
public class Restaurant {

  // Attributes

  private String restaurantName;
  private String location;
  private String contactNumber;

  // Methods

  // Constructor
  public Restaurant(String restaurantName, String location, String contactNumber) {
    this.restaurantName = restaurantName;
    this.location = location;
    this.contactNumber = contactNumber;
  }

  /**
   * Getter to return the contactNumber
   */
  public String getContactNumber() {
    return contactNumber;
  }

  /**
   * Getter to return the location
   */
  public String getLocation() {
    return location;
  }

  /**
   * Getter to return the restaurantName
   */
  public String getRestaurantName() {
    return restaurantName;
  }
}
