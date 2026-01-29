package sanguine.strategy;

import java.util.List;
import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;

/**
 * Combines various strategies, having each following one as a fallback.
 */
public class CombinedStrategies implements PlayerStrategy {
  private final List<PlayerStrategy> strategies;

  /**
   * Initializes the strategies to be combined and used together.
   *
   * @param strategies the list of strategies to be used together
   */
  public CombinedStrategies(List<PlayerStrategy> strategies) {
    if (strategies == null) {
      throw new IllegalArgumentException();
    }
    this.strategies = strategies;
  }

  @Override
  public Move chooseMove(ReadOnlySanguineModel game, Pawn pawn) {
    if (game.getTurn() != pawn) {
      throw new IllegalStateException("Not this player's turn!\n");
    }

    for (PlayerStrategy strategy : strategies) {
      Move move = strategy.chooseMove(game, pawn);
      if (!move.isPass()) {
        return move;
      }
    }
    return new CardMove();
  }
}