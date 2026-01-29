package sanguine.strategy;

import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.model.SanguineModel;

/**
 * Represents a strategy for making player decisions in Sanguine.
 * Allows different player types (human or AI) to play without changing the game model.
 */
public interface PlayerStrategy {
  /**
   * Chooses a move for the current player's turn.
   * Can either pass or place a card on the board.
   *
   * @param game the current game state
   * @param pawn indicator of whose turn it is
   * @return a Move representing the chosen action
   * @throws IllegalStateException if no valid move can be made
   */
  Move chooseMove(ReadOnlySanguineModel game, Pawn pawn) throws IllegalStateException;
}