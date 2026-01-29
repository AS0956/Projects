package sanguine.mocks;

import sanguine.model.Card;
import sanguine.model.Influence;

/**
 * A mock card with only a value field.
 * This class is simply made for mocking and testing purposes.
 */
public class MockCard implements Card {
  int value;

  /**
   * An empty constructor initializes the value of the card to 0.
   */
  public MockCard() {
    value = 0;
  }

  /**
   * Initialize the value of the card with the given argument.
   *
   * @param value the value of the card
   */
  public MockCard(int value) {
    this.value = value;
  }

  @Override
  public String getName() {
    return "";
  }

  @Override
  public int getCost() {
    return 0;
  }

  @Override
  public int getValue() {
    return value;
  }

  @Override
  public Influence[][] getGrid() {
    return new Influence[5][5];
  }
}
