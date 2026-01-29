package sanguine.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import sanguine.controller.SanguinePlayer;
import sanguine.mocks.MinimaxMock;
import sanguine.model.Card;
import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.strategy.CardMove;
import sanguine.strategy.MinimaxStrategy;

/**
 * Tests the MinimaxStrategy class.
 */
public class MinimaxStrategyTest {
  SanguinePlayer redPlayer = new SanguinePlayer(Pawn.RED, new MinimaxStrategy() {
    int row;
    int col;
    int handIndex;

    @Override
    protected ReadOnlySanguineModel simulateMove(ReadOnlySanguineModel model, Pawn pawn,
                                                 int row, int col, int handIndex, List<Card> hand) {
      this.row = row;
      this.col = col;
      this.handIndex = handIndex;
      return model;
    }

    @Override
    protected int evaluateOpponentBestMove(ReadOnlySanguineModel m, Pawn opponent) {
      return ((MinimaxMock) m).getOpponentScore(row, col, handIndex);
    }
  });
  SanguinePlayer bluePlayer = new SanguinePlayer(Pawn.BLUE, new MinimaxStrategy());
  MinimaxMock model;

  /**
   * Sets up the mock with the current turn being red player.
   */
  @Before
  public void setUp() {
    model = new MinimaxMock(Pawn.RED);
  }

  @Test
  public void testMinimizesOpponent() {
    model.setLegal(0, 0, 0);
    model.setLegal(1, 1, 0);
    model.setOpponentScoreAfterMove(0, 0, 0, 5);
    model.setOpponentScoreAfterMove(1, 1, 0, 3);

    CardMove expected = new CardMove(0, 1, 1);
    assertEquals(expected, redPlayer.chooseMove(model));
  }

  @Test
  public void testPrioritizesFirstRow() {
    model.setLegal(0, 0, 0);
    model.setLegal(1, 0, 0);
    model.setOpponentScoreAfterMove(0, 0, 0, 3);
    model.setOpponentScoreAfterMove(1, 0, 0, 3);

    CardMove expected = new CardMove(0, 0, 0);
    assertEquals(expected, redPlayer.chooseMove(model));
  }

  @Test
  public void testPrioritizesFirstColumn() {
    model.setLegal(1, 0, 0);
    model.setLegal(1, 1, 0);
    model.setOpponentScoreAfterMove(1, 0, 0, 3);
    model.setOpponentScoreAfterMove(1, 1, 0, 3);

    CardMove expected = new CardMove(0, 1, 0);
    assertEquals(expected, redPlayer.chooseMove(model));
  }

  @Test
  public void testPrioritizesFirstCard() {
    model.setLegal(0, 0, 0);
    model.setLegal(0, 0, 1);
    model.setOpponentScoreAfterMove(0, 0, 0, 3);
    model.setOpponentScoreAfterMove(0, 0, 1, 3);

    CardMove expected = new CardMove(0, 0, 0);
    assertEquals(expected, redPlayer.chooseMove(model));
  }

  @Test
  public void testPassMove() {
    assertEquals(new CardMove(), redPlayer.chooseMove(model));
  }

  @Test
  public void testWrongTurn() {
    assertThrows(IllegalStateException.class, () -> bluePlayer.chooseMove(model));
  }
}
