package sanguine.model;

import java.util.List;

/**
 * A read-only version of the SanguineModel, which only contains observer methods.
 * Through an implementation of this, the model can't be modified and the user
 * can only see the contents of it.
 */
public interface ReadOnlySanguineModel {
  /**
   * Checks if the game is over.
   * The game ends when both players pass consecutively.
   *
   * @return true if game is over, false otherwise
   * @throws IllegalStateException if the game hasn't started yet
   */
  boolean isGameOver() throws IllegalStateException;

  /**
   * Gets the winner of the game.
   *
   * @return the winning player color, or null if the game ended in a tie
   * @throws IllegalStateException if game hasn't started yet or is not over yet
   */
  Pawn getWinner() throws IllegalStateException;

  /**
   * Gets the current player that is playing.
   *
   * @return the current player color
   * @throws IllegalStateException if the game hasn't started yet or game is over
   */
  Pawn getTurn() throws IllegalStateException;

  /**
   * Gets the game board.
   *
   * @return the game board (should return a copy or read-only view to prevent modification)
   * @throws IllegalStateException if the game hasn't started yet
   */
  SanguineCell[][] getBoard() throws IllegalStateException;

  /**
   * Gets the row score for the red player at the specified index.
   *
   * @return the red player's score at the given index
   * @throws IllegalArgumentException if the index is invalid
   * @throws IllegalStateException    if the game hasn't started yet
   */
  int getRedRowScore(int index) throws IllegalArgumentException, IllegalStateException;

  /**
   * Gets the row score for the blue player at the specified index.
   *
   * @return the blue player's score at the given index
   * @throws IllegalArgumentException if the index is invalid
   * @throws IllegalStateException    if the game hasn't started yet
   */
  int getBlueRowScore(int index) throws IllegalArgumentException, IllegalStateException;

  /**
   * Gets the red player's current total score.
   *
   * @return the red player's score as an int
   * @throws IllegalStateException if the game hasn't started yet
   */
  int getRedScore() throws IllegalStateException;

  /**
   * Gets the blue player's current total score.
   *
   * @return the blue player's score as an int
   * @throws IllegalStateException if the game hasn't started yet
   */
  int getBlueScore() throws IllegalStateException;

  /**
   * Gets the red player's current hand.
   *
   * @return a list of cards
   * @throws IllegalStateException if the game hasn't started yet
   */
  List<Card> getRedPlayerHand() throws IllegalStateException;

  /**
   * Gets the blue player's current hand.
   *
   * @return a list of cards
   * @throws IllegalStateException if the game hasn't started yet
   */
  List<Card> getBluePlayerHand() throws IllegalStateException;

  /**
   * Gets the number of rows that the current game board has.
   *
   * @return the number of rows as an int
   * @throws IllegalStateException if the game hasn't started yet
   */
  int getNumRows() throws IllegalStateException;

  /**
   * Gets the number of columns that the current game board has.
   *
   * @return the number of columns as an int
   * @throws IllegalStateException if the game hasn't started yet
   */
  int getNumCols() throws IllegalStateException;

  /**
   * Gets the contents of the cell at the specified row and column.
   *
   * @param row the row of the cell to be observed
   * @param col the column of the cell to be observed
   * @return a list of CellContent containing what is inside the cell, or an empty list
   * if the cell is empty
   * @throws IllegalArgumentException if the row or column is invalid
   * @throws IllegalStateException    if the game hasn't started yet
   */
  List<CellContent> getCellContent(int row, int col) throws IllegalArgumentException,
      IllegalStateException;

  /**
   * Gets the color of the player that owns the cell at the specified coordinates,
   * and null if no player currently owns that cell.
   *
   * @param row the row of the cell to be observed
   * @param col the column of the cell to be observed
   * @return a color of enum Pawn, which is either RED or BLUE, or null
   * @throws IllegalArgumentException if the row or column is invalid
   * @throws IllegalStateException    if the game hasn't started yet
   */
  Pawn getCellOwnership(int row, int col) throws IllegalArgumentException,
      IllegalStateException;

  /**
   * Determines whether the card can be placed at the specified coordinates.
   *
   * @param row       the row for the card to be placed
   * @param col       the column for the card to be placed
   * @param handIndex the index in the player's hand pointing a card
   * @return true if the move is legal, false otherwise
   * @throws IllegalArgumentException if the row, column, or handIndex is invalid
   * @throws IllegalStateException    if the game hasn't started yet
   */
  boolean isLegal(int row, int col, int handIndex) throws IllegalArgumentException,
      IllegalStateException;
}
