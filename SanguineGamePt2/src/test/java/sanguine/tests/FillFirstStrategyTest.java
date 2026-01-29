package sanguine.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Before;
import org.junit.Test;
import sanguine.controller.SanguinePlayer;
import sanguine.mocks.RowColumnTrackerMock;
import sanguine.model.Pawn;
import sanguine.strategy.CardMove;
import sanguine.strategy.FillFirstStrategy;

/**
 * Tests the FillFirstStrategy class.
 */
public class FillFirstStrategyTest {
  SanguinePlayer redPlayer = new SanguinePlayer(Pawn.RED, new FillFirstStrategy());
  SanguinePlayer bluePlayer = new SanguinePlayer(Pawn.BLUE, new FillFirstStrategy());
  Appendable log;
  RowColumnTrackerMock model;

  /**
   * Sets up the log and mock model.
   */
  @Before
  public void setUp() {
    log = new StringBuilder();
    model = new RowColumnTrackerMock(log, 3, 5, 2);
  }

  @Test
  public void testFirstMoveRed() {
    model.setLegalMove(0, 0, 0);
    String expected = "0 0 0\n";
    CardMove move = new CardMove(0, 0, 0);
    assertEquals(move, redPlayer.chooseMove(model));
    assertEquals(expected, log.toString());
  }

  @Test
  public void testMiddleOfGameRedMove() {
    model.setLegalMove(1, 2, 0);
    String expected = "0 0 0\n0 1 0\n0 2 0\n0 3 0\n0 4 0\n1 0 0\n1 1 0\n1 2 0\n";
    CardMove move = new CardMove(0, 1, 2);
    assertEquals(move, redPlayer.chooseMove(model));
    assertEquals(expected, log.toString());
  }

  @Test
  public void testPassMoveRed() {
    String expected = ("0 0 0\n0 1 0\n0 2 0\n0 3 0\n0 4 0\n1 0 0\n1 1 0\n1 2 0\n1 3 0\n1 4 0\n"
        + "2 0 0\n2 1 0\n2 2 0\n2 3 0\n2 4 0\n0 0 1\n0 1 1\n0 2 1\n0 3 1\n0 4 1\n"
        + "1 0 1\n1 1 1\n1 2 1\n1 3 1\n1 4 1\n2 0 1\n2 1 1\n2 2 1\n2 3 1\n2 4 1\n");
    CardMove move = new CardMove();
    assertEquals(move, redPlayer.chooseMove(model));
    assertEquals(expected, log.toString());
    System.out.println(log);
  }

  @Test
  public void testWrongTurn() {
    assertThrows(IllegalStateException.class, () -> bluePlayer.chooseMove(model));
  }
}
