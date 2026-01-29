package sanguine.strategy;

/**
 * Represents a move in the game.
 * Can be either a pass or a card placement.
 */
public interface Move {
  /**
   * Checks if this move is a pass.
   *
   * @return true if is a pass move, false otherwise
   */
  boolean isPass();

  /**
   * Gets the row to place the card.
   *
   * @return the row index
   * @throws IllegalStateException if this is a pass move
   */
  int getRow() throws IllegalStateException;

  /**
   * Gets the column to place the card.
   *
   * @return the column index
   * @throws IllegalStateException if this is a pass move
   */
  int getCol() throws IllegalStateException;

  /**
   * Gets the hand index of the card to play.
   *
   * @return the hand index
   * @throws IllegalStateException if this is a pass move
   */
  int getHandIndex() throws IllegalStateException;
}