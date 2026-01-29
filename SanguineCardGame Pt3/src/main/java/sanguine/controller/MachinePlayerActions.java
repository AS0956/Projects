package sanguine.controller;

import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.strategy.Move;
import sanguine.strategy.PlayerStrategy;

/**
 * A machine player requires a strategy to be constructed.
 * From that strategy, a move is chosen.
 */
public class MachinePlayerActions implements PlayerActions {
  private final Pawn color;
  private final PlayerStrategy strategy;
  private Move move;

  /**
   * Initializes the color of strategy of the player.
   *
   * @param color the color of the player (red or blue)
   * @param strategy the strategy this player plans to use
   */
  public MachinePlayerActions(Pawn color, PlayerStrategy strategy) {
    if (color == null || strategy == null) {
      throw new IllegalArgumentException("Can't have null color or strategy!\n");
    }
    this.color = color;
    this.strategy = strategy;
  }

  private Move getMove(ReadOnlySanguineModel model) {
    if (move == null) {
      move = strategy.chooseMove(model, color);
    }
    return move;
  }

  @Override
  public int chooseRow(ReadOnlySanguineModel model) {
    Move move = getMove(model);
    return move.isPass() ? -1 : move.getRow();
  }

  @Override
  public int chooseCol(ReadOnlySanguineModel model) {
    Move move = getMove(model);
    return move.isPass() ? -1 : move.getCol();
  }

  @Override
  public int chooseHandIndex(ReadOnlySanguineModel model) {
    Move move = getMove(model);
    return move.isPass() ? -1 : move.getHandIndex();
  }

  @Override
  public Pawn getColor() {
    return color;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof MachinePlayerActions;
  }

  @Override
  public String toString() {
    return "Machine";
  }

  /**
   * Clears the move to null so it doesn't use the same move in the next turn.
   */
  public void clearMove() {
    this.move = null;
  }
}
