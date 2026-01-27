package creditcard;

/**
 * A public interface which uses methods in order to be used for implementation from a java class.
 */

public interface CreditCardAccount {

  /**
   * Returns status of credit card account.
   *
   * @return the status of credit card account as string
   */
  String status();

  /**
   * Returns current creditLimit on credit card account.
   *
   * @return the creditLimit on credit card account as a double
   */
  double creditLimit();

  /**
   * Returns APR of credit card account.
   *
   * @return the APR of credit card account as a double between 0 and 100
   */
  double apr();

  /**
   * Returns minimum payment for current billing cycle.
   *
   * @return the minimum payment for the current billing cycle as a double
   */
  double minimumPayment();

  /**
   * Returns current balance of the credit card account.
   *
   * @return current balance of the credit card account as a double
   */
  double balance();

  /**
   * Charges credit card account if price is non-negative as a double.
   *
   * @param amount to charge
   * @return updated credit card account balance
   */
  CreditCardAccount expense(double amount);

  /**
   * Takes any non-negative price as a double from the current credit card account balance.
   *
   * @param amount to pay
   * @return resulting account balance
   */
  CreditCardAccount payoff(double amount);

  /**
   * Increases current limit on credit card.
   *
   * @return resulting account after limit change
   */
  CreditCardAccount increaseLimit();

  /**
   * Processes the current credit card cycle.
   *
   * @return the resulting credit card account after processing the current credit cycle
   */
  CreditCardAccount processCycle();

}