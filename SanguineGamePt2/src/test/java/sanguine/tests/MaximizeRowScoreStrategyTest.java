package sanguine.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Before;
import org.junit.Test;
import sanguine.controller.SanguinePlayer;
import sanguine.mocks.MaximizeRowScoreMock;
import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.strategy.CardMove;
import sanguine.strategy.MaximizeRowScoreStrategy;

/**
 * Tests the MaximizeRowScoreStrategy class.
 */
public class MaximizeRowScoreStrategyTest {
  SanguinePlayer redPlayer = new SanguinePlayer(Pawn.RED, new MaximizeRowScoreStrategy());
  SanguinePlayer bluePlayer = new SanguinePlayer(Pawn.BLUE, new MaximizeRowScoreStrategy());
  Appendable log;
  MaximizeRowScoreMock model;

  /**
   * Sets up the log and mock model.
   */
  @Before
  public void setUp() {
    log = new StringBuilder();
    model = new MaximizeRowScoreMock(log);
  }

  @Test
  public void testFirstMove() {
    ReadOnlySanguineModel model = new MaximizeRowScoreMock(log);
    CardMove expected = new CardMove(0, 0, 0);
    String result = "0 0 0\n";
    assertEquals(expected, redPlayer.chooseMove(model));
    assertEquals(result, log.toString());
  }

  @Test
  public void testAllRows() {
    ReadOnlySanguineModel model = new MaximizeRowScoreMock(log, new int[] {1, 1, 1},
        new int[] {3, 3, 2});
    String result = "0 0 0\n0 1 0\n0 2 0\n0 3 0\n0 4 0\n0 0 1\n0 1 1\n0 2 1\n0 3 1\n0 4 1\n"
                  + "1 0 0\n1 1 0\n1 2 0\n1 3 0\n1 4 0\n1 0 1\n1 1 1\n1 2 1\n1 3 1\n1 4 1\n2 0 0\n";
    CardMove expected = new CardMove(0, 2, 0);
    assertEquals(expected, redPlayer.chooseMove(model));
    assertEquals(result, log.toString());
    System.out.println(log.toString());
  }

  @Test
  public void testOnlyOneValidRow() {
    ReadOnlySanguineModel model = new MaximizeRowScoreMock(log, new int[] {3, 2, 3},
        new int[] {2, 2, 1});
    String result = "1 0 0\n";
    CardMove expected = new CardMove(0, 1, 0);
    assertEquals(expected, redPlayer.chooseMove(model));
    assertEquals(result, log.toString());
  }

  @Test
  public void testPassMove() {
    ReadOnlySanguineModel model = new MaximizeRowScoreMock(log, new int[] {3, 3, 3},
        new int[] {1, 1, 1});
    String result = "";
    CardMove expected = new CardMove();
    assertEquals(expected, redPlayer.chooseMove(model));
    assertEquals(result, log.toString());
    System.out.println(log.toString());
  }

  @Test
  public void testWrongTurn() {
    ReadOnlySanguineModel model = new MaximizeRowScoreMock(log);
    assertThrows(IllegalStateException.class, () -> bluePlayer.chooseMove(model));
  }
}
