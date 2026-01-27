package creditcard;

/**
  *Implements CreditCardAccount interface.
  *Mimics a real-life credit card account
  *Provides methods to process billing cycles,
  *make payments, and charges expenses.
  *This class keeps track of balance, creditLimit, apr, minimumPayment, lateFee, currentCycle
  *increases credit limit according to the set account rules
 */

public class BasicCreditCardAccount implements CreditCardAccount {
  private double balance;
  private double creditLimit;
  private double apr;
  private double minimumPayment;
  private int lateFee;
  private int currentCycle;
  private String status;
  private boolean processCycle;

  /**
  *Constructs new BasicCreditCardAccount with specified lateFee, creditLimit, and apr.
  *Intialized balance to 0.0, minimumPayment to 0.0
 */

  public BasicCreditCardAccount(double creditLimit, double apr, int lateFee) {

    this.creditLimit = creditLimit;
    this.apr = apr;
    this.balance = 0.0;
    minimumPayment = 0.0;
    this.lateFee = lateFee;
    status = "OK: balance within limit";
    processCycle = false;

    if (!(creditLimit > 0 && apr > 0 && apr < 100 && lateFee > 0 && lateFee <= creditLimit)) {
      throw new IllegalArgumentException("Invalid credit limit");
    }
  }

  private BasicCreditCardAccount(double balance, double creditLimit, double apr,
                                 double minimumPayment, int lateFee, int currentCycle,
                                 String status) {
    this.balance = balance;
    this.creditLimit = creditLimit;
    this.apr = apr;
    this.minimumPayment = minimumPayment;
    this.lateFee = lateFee;
    this.currentCycle = currentCycle;
    this.status = status;

    if (creditLimit <= 0 || apr <= 0 || apr > 100 || lateFee < 0) {
      throw new IllegalArgumentException("Invalid credit limit");
    }
  }

  @Override
  public String status() {
    return status;
  }

  @Override
  public double creditLimit() {
    return creditLimit;
  }

  @Override
  public double apr() {
    return apr;
  }

  @Override
  public double balance() {
    return balance;
  }

  @Override
  public double minimumPayment() {
    double interest = this.balance * (apr / 100.0);
    double newBalance = this.balance + interest;
    if (this.balance == 0) {
      return 0;
    } else if (!processCycle) {
      return minimumPayment;
    } else if (this.balance > 50 && minimumPayment == 0) {
      minimumPayment = this.balance * 0.02;
    } else {
      minimumPayment = (newBalance * 0.02) + interest + lateFee;
    }
    return minimumPayment;
  }

  @Override
  public CreditCardAccount expense(double amount) {
    double newBalance = this.balance + amount;
    if (amount < 0) {
      status = "ERR: cannot input negative money";
      return new BasicCreditCardAccount(this.balance, creditLimit,
          apr, minimumPayment, lateFee, currentCycle, status);
    } else if (newBalance > creditLimit) {
      status = "ERR: cannot expense beyond limit";
      return new BasicCreditCardAccount(this.balance, creditLimit,
          apr, minimumPayment, lateFee, currentCycle, status);
    }
    status = "OK: balance within limit";
    return new BasicCreditCardAccount(newBalance, creditLimit,
        apr, minimumPayment, lateFee, currentCycle, status);
  }

  @Override
  public CreditCardAccount payoff(double amount) {
    if (amount > this.balance) {
      status = "ERR: cannot decrease below 0";
    } else if (amount < 0) {
      status = "ERR: cannot input negative money";
    } else {
      this.balance -= amount;
      status = "OK: balance within limit";

    }
    return new BasicCreditCardAccount(this.balance, creditLimit,
        apr, minimumPayment, lateFee, currentCycle, status);
  }

  @Override
  public CreditCardAccount increaseLimit() {
    if (currentCycle >= 3 && this.balance <= 0.5 * creditLimit) {
      creditLimit += 500;
      currentCycle = 0;
      status = "OK: limit increased";
    } else {
      status = "ERR: limit cannot increase";
    }
    return new BasicCreditCardAccount(this.balance, creditLimit,
        apr, minimumPayment, lateFee, currentCycle, status);

  }


  @Override
  public CreditCardAccount processCycle() {
    this.balance += this.balance * (apr / 100.0);
    processCycle = true;
    minimumPayment = minimumPayment();

    if (this.balance <= (creditLimit * 0.5)) {
      currentCycle++;
    } else if (this.balance > creditLimit) {
      status = "OK: balance exceeds limit";
    }
    return new BasicCreditCardAccount(this.balance, creditLimit,
        apr, minimumPayment, lateFee, currentCycle, status);
  }
}