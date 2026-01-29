package sanguine.controller;

import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.strategy.Move;
import sanguine.strategy.PlayerStrategy;

/**
 * Each player is either the red or blue player, and has a strategy to play the game.
 */
public class SanguinePlayer implements Player {
  private final Pawn color;
  private final PlayerStrategy strategy;

  /**
   * Initializes the color of strategy of the player.
   *
   * @param color the color of the player (red or blue)
   * @param strategy the strategy this player plans to use
   */
  public SanguinePlayer(Pawn color, PlayerStrategy strategy) {
    this.color = color;
    this.strategy = strategy;
  }

  @Override
  public Move chooseMove(ReadOnlySanguineModel model) {
    return strategy.chooseMove(model, this.color);
  }

  @Override
  public Pawn getColor() {
    return null;
  }
}
