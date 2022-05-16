/*
 * Class to create instances of Customer objects for use in the main method of
 * the program
 */
public class Customer {

  // Attributes

  private String name;
  private String surname;
  private String contactNumber;
  private String address;
  private String location;
  private String email;

  // Methods

  // Constructor
  public Customer(String name, String surname, String contactNumber, String address, String location, String email) {
    this.name = name;
    this.surname = surname;
    this.contactNumber = contactNumber;
    this.address = address;
    this.location = location;
    this.email = email;
  }

  /**
   * Getter to return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Getter to return the surname
   */
  public String getSurname() {
    return surname;
  }

  /**
   * Getter to return the contactNumber
   */
  public String getContactNumber() {
    return contactNumber;
  }

  /**
   * Getter to return the address
   */
  public String getAddress() {
    return address;
  }

  /**
   * Getter to return the location
   */
  public String getLocation() {
    return location;
  }

  /**
   * Getter to return the email
   */
  public String getEmail() {
    return email;
  }
}
