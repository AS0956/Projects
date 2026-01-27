package sanguine.model;

/**
 * Represents a card.
 */
public interface Card extends CellContent {
  /**
   * Gets the card's name.
   *
   * @return the card's name as a String
   */
  public String getName();

  /**
   * Gets the cost of the card.
   *
   * @return the cost of the card as an int
   */
  public int getCost();

  /**
   * Gets the value of the card.
   *
   * @return the value of the card as an int
   */
  public int getValue();

  /**
   * Gets the card's influence grid.
   *
   * @return the card's influence grid as a 2D array of Influence
   */
  public Influence[][] getGrid();
}
