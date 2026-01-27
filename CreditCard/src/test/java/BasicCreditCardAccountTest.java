import static org.junit.Assert.assertEquals;

import creditcard.BasicCreditCardAccount;
import creditcard.CreditCardAccount;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests BasicCreditCardAccount.
 */
public class BasicCreditCardAccountTest {
  private BasicCreditCardAccount account;

  /**
   * Tests different parts.
   */

  @Before
  public void setup() {
    account = new BasicCreditCardAccount(1000, 15, 90);
  }

  @Test
  public void testStatus() {
    assertEquals("OK: balance within limit", account.status());
  }

  @Test
  public void testcreditLimit() {
    assertEquals(1000, account.creditLimit(), 0.001);
  }

  @Test
  public void testapr() {
    assertEquals(15, account.apr(), 0.001);
  }

  @Test
  public void testbalance() {
    assertEquals(0.0, account.balance(), 0.001);
  }

  @Test
  public void testminimumPayment() {
    assertEquals(0.0, account.minimumPayment(), 0.001);
  }

  @Test
  public void testExpense() {
    account.expense(2000);
    assertEquals(0, account.balance(), 0.001);
  }

  @Test
  public void testIncreaseLimitAfterMultiplePayment() {
    account.expense(1000);
    account.processCycle();
    account.payoff(23); // higher payment to reduce balance
    account.processCycle();
    account.payoff(Math.ceil(22.471));
    account.processCycle();
    account.increaseLimit();
    assertEquals("OK: limit increased", account.status());
    double expectedBalance = account.balance();
    assertEquals(expectedBalance, account.balance(), 0.001);
  }

  @Test
  public void testCannotIncreaseLimitWithLargeBalance() {
    account.expense(500);
    account.increaseLimit();
    assertEquals("ERR: limit cannot increase", account.status());
  }

  @Test
  public void testCannotIncreaseLimitRightAfterCreation() {
    account.increaseLimit();
    assertEquals("ERR: limit cannot increase", account.status());
  }

  @Test
  public void testValidPaymentAfterExpense() {
    account.expense(400);
    account.payoff(300);
    assertEquals(0, account.balance(), 0.001);
  }

}