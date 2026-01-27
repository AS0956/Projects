package klondike.model.hw02;

/**
 * This enum represents the four suits in a normal deck of cards.
 * Every suit has a symbol and color (red/black)
 */

public enum Suit {
  clubs("♣", false),
  diamonds("♢", true),
  hearts("♡", true),
  spades("♠", false);

  private final String symbol;
  private final boolean isRed;

  /**
   * Creates a suit with the symbol and color.
   *
   * @param symbol the symbol for ths uit
   * @param isRed  true if the suit is red, if black is false
   */
  Suit(String symbol, boolean isRed) {
    this.symbol = symbol;
    this.isRed = isRed;
  }

  /**
   * gets symbol of the suit.
   *
   * @return the symbol as a string
   */

  public String getSymbol() {
    return this.symbol;
  }

  /**
   * Checks if the suit is red.
   *
   * @return true if the suit is red, false in other situations.
   */
  public boolean isRed() {
    return this.isRed;
  }
}
