package sanguine.model;

import java.util.List;

/**
 * Represents a single cell on the game board.
 */
public interface Cell {
  /**
   * Get the list of pawns in the cell.
   *
   * @return a list of pawns in the cell
   * @throws IllegalStateException if there are no pawns in the cell
   */
  List<Pawn> getPawns() throws IllegalStateException;

  /**
   * Get the card that was placed in the cell, only if there is one.
   *
   * @return the card in the cell
   * @throws IllegalStateException if there is no card in the cell
   */
  Card getCard() throws IllegalStateException;

  /**
   * Gets the color of the player who currently owns the cell.
   *
   * @return the color of the player who owns the cell
   */
  Pawn getOwningColor();

  /**
   * Adds a pawn to cell.
   *
   * @param pawn the pawn to be added
   * @throws IllegalArgumentException if the pawn is null or if the pawn to be added isn't the
   *                                  same color as the other pawns in the cell
   * @throws IllegalStateException    if the cell is full and no more pawns can be added,
   *                                  or if a card is occupying the cell
   */
  void addPawn(Pawn pawn) throws IllegalArgumentException, IllegalStateException;

  /**
   * Places a card in cell and clears all pawns, if any, are in the cell..
   *
   * @param card the card to place
   * @throws IllegalArgumentException if the card is null
   * @throws IllegalStateException    if the cell is already occupied by a card
   *                                  or if the cell doesn't have enough pawns to cover
   *                                  the cost of the card
   */
  void placeCard(Card card) throws IllegalArgumentException, IllegalStateException;

  /**
   * Checks to see if the cell has a card.
   *
   * @return true if the cell is occupied by a card, false otherwise
   */
  boolean hasCard();

  /**
   * Checks if the cell is empty.
   * A cell is empty if there are no pawns in the cell
   * and no card occupies the cell.
   *
   * @return true if empty, false otherwise
   */
  boolean isEmpty();

  /**
   * Gets the number of pawns in the cell.
   * The number of pawns range from 0 to 3.
   *
   * @return the number of pawns as an int
   */
  int getPawnCount();

  /**
   * Clears all pawns from the cell.
   * There will be zero pawns in the cell after it is cleared.
   */
  void clearPawns();

  /**
   * Converts all pawns in the cell to the opposing color.
   * If the pawns are red, they all become blue, and vice versa.
   *
   * @throws IllegalStateException if the cell is empty or if the cell is occupied by a card
   */
  void convertPawns() throws IllegalStateException;
}