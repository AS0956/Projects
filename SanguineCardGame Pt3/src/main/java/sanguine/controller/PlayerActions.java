package sanguine.controller;

import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.strategy.Move;

/**
 * Represents what a player can do.
 * Each player is either red or blue as indicated by the Pawn enum.
 */
public interface PlayerActions {
  /**
   * Chooses the appropriate row based of the best move the strategy finds.
   * If there is no valid move according to the strategy, then returns -1.
   *
   * @param model the current state of the game
   * @return the row for the move to be made
   */
  int chooseRow(ReadOnlySanguineModel model);

  /**
   * Chooses the appropriate column based of the best move the strategy finds.
   * If there is no valid move according to the strategy, then returns -1.
   *
   * @param model the current state of the game
   * @return the column for the move to be made
   */
  int chooseCol(ReadOnlySanguineModel model);

  /**
   * Chooses the appropriate hand index based of the best move the strategy finds.
   * If there is no valid move according to the strategy, then returns -1.
   *
   * @param model the current state of the game
   * @return the hand index for the move to be made
   */
  int chooseHandIndex(ReadOnlySanguineModel model);

  /**
   * Gets the color of the player, which is either red or blue.
   *
   * @return a type of Pawn enum
   */
  Pawn getColor();

  /**
   * Compares this PlayerAction object to another object based on their
   * specific class type.
   *
   * @param other   the reference object with which to compare
   * @return true if this object is equal to other, false otherwise
   */
  boolean equals(Object other);

  /**
   * Returns a string indicating if the player is a human or machine.
   *
   * @return a string either as "Human" or "Machine"
   */
  String toString();
}
