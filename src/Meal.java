/*
 * Method to create instances of Meal objects to use in the main method of the
 * program for the invoice and total amount due calculation
 */
public class Meal {

  // Attributes

  private String mealName;
  private String price;
  private String numberOfUnits;

  // Methods

  // Constructor
  public Meal(String mealName, String price, String numberOfUnits) {
    this.mealName = mealName;
    this.price = price;
    this.numberOfUnits = numberOfUnits;
  }

  /**
   * Getter to return the numberOfUnits
   */
  public String getNumberOfUnits() {
    return numberOfUnits;
  }

  /**
   * Getter to return the price
   */
  public String getPrice() {
    return price;
  }

  /**
   * Getter to return the mealName
   */
  public String getMealName() {
    return mealName;
  }
}
