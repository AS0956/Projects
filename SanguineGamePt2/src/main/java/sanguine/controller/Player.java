package sanguine.controller;

import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.strategy.Move;

/**
 * Represents what a player can do.
 * Each player is either red or blue as indicated by the Pawn enum.
 */
public interface Player {
  /**
   * Chooses an appropriate move based of the strategy the player is using.
   * If there is no valid move according to the strategy, then turn a pass move.
   *
   * @param model the current state of the game
   * @return a move of class Move
   */
  Move chooseMove(ReadOnlySanguineModel model);

  /**
   * Gets the color of the player, which is either red or blue.
   *
   * @return a type of Pawn enum
   */
  Pawn getColor();
}
