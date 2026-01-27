package sanguine.controller;

import sanguine.model.Player;
import sanguine.model.SanguineModel;

/**
 * Represents a strategy for making player decisions in Sanguine.
 * Allows different player types (human, AI, test) without changing the game model.
 */
public interface PlayerStrategy {
  /**
   * Chooses a move for the current player's turn.
   * Can either pass or place a card on the board.
   *
   * @param game   the current game state
   * @param player the player whose turn it is
   * @return a Move representing the chosen action
   * @throws IllegalStateException if no valid move can be made
   */
  Move chooseMove(SanguineModel game, Player player) throws IllegalStateException;

  /**
   * Represents a move in the game.
   * Can be either a pass or a card placement.
   */
  interface Move {
    /**
     * Checks if this move is a pass.
     *
     * @return true if passing, false otherwise
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
}