package sanguine.tests;

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.junit.Test;
import sanguine.controller.SanguinePlayer;
import sanguine.mocks.CombinedStrategyMock;
import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.strategy.CardMove;
import sanguine.strategy.CombinedStrategies;
import sanguine.strategy.Move;
import sanguine.strategy.PlayerStrategy;

/**
 * Tests the CombinedStrategies class.
 */
public class CombinedStrategiesTest {
  Move move;
  CombinedStrategies strategy;
  SanguinePlayer redPlayer;
  SanguinePlayer bluePlayer;
  ReadOnlySanguineModel model = new CombinedStrategyMock();

  static class PassStrategy implements PlayerStrategy {
    @Override
    public Move chooseMove(ReadOnlySanguineModel game, Pawn pawn) {
      return new CardMove(); // pass
    }
  }

  static class FixedMoveStrategy implements PlayerStrategy {
    private final Move move;

    /**
     * Initializes the move field with the given move.
     *
     * @param move the fixed move
     */
    public FixedMoveStrategy(Move move) {
      this.move = move;
    }

    @Override
    public Move chooseMove(ReadOnlySanguineModel game, Pawn pawn) {
      return move;
    }
  }

  @Test
  public void testFirstStrategyIsUsed() {
    move = new CardMove(0, 0, 0);
    strategy = new CombinedStrategies(
        List.of(new FixedMoveStrategy(move), new PassStrategy(), new PassStrategy()));
    redPlayer = new SanguinePlayer(Pawn.RED, strategy);
    assertEquals(move, strategy.chooseMove(model, Pawn.RED));
  }

  @Test
  public void testSecondStrategyIsUsed() {
    move = new CardMove(0, 1, 0);
    strategy = new CombinedStrategies(
        List.of(new PassStrategy(), new FixedMoveStrategy(move), new PassStrategy()));
    redPlayer = new SanguinePlayer(Pawn.RED, strategy);
    assertEquals(move, strategy.chooseMove(model, Pawn.RED));
  }

  @Test
  public void testLastStrategyIsUsed() {
    move = new CardMove(0, 1, 1);
    strategy = new CombinedStrategies(
        List.of(new PassStrategy(), new PassStrategy(), new FixedMoveStrategy(move)));
    redPlayer = new SanguinePlayer(Pawn.RED, strategy);
    assertEquals(move, strategy.chooseMove(model, Pawn.RED));
  }

  @Test
  public void testPassMove() {
    strategy = new CombinedStrategies(
        List.of(new PassStrategy(), new PassStrategy(), new PassStrategy()));
    redPlayer = new SanguinePlayer(Pawn.RED, strategy);
    assertEquals(new CardMove(), strategy.chooseMove(model, Pawn.RED));
  }
}
