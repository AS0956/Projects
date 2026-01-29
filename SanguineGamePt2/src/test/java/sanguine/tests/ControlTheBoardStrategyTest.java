package sanguine.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import sanguine.controller.SanguinePlayer;
import sanguine.mocks.ControlBoardMock;
import sanguine.model.Card;
import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.strategy.CardMove;
import sanguine.strategy.ControlTheBoardStrategy;
import sanguine.strategy.Move;

/**
 * Tests the ControlTheBoardStrategy class.
 */
public class ControlTheBoardStrategyTest {
  SanguinePlayer redPlayer = new SanguinePlayer(Pawn.RED, new ControlTheBoardStrategy() {
    @Override
    public Move chooseMove(ReadOnlySanguineModel game, Pawn pawn) {
      int count = -1;
      Integer bestRow = null;
      Integer bestCol = null;
      Integer bestHandIndex = null;
      List<Card> hand = pawn.equals(Pawn.RED) ? game.getRedPlayerHand() : game.getBluePlayerHand();
      int numRows = game.getNumRows();
      int numCols = game.getNumCols();
      for (int index = 0; index < hand.size(); index++) {
        for (int row = 0; row < numRows; row++) {
          for (int col = 0; col < numCols; col++) {
            if (game.isLegal(row, col, index)) {
              int myCells = ((ControlBoardMock) game).getScore(row, col, index);
              if (myCells > count) {
                count = myCells;
                bestRow = row;
                bestCol = col;
                bestHandIndex = index;
              }
            }
          }
        }
      }
      if (bestRow == null) {
        return new CardMove();
      }
      return new CardMove(bestHandIndex, bestRow, bestCol);
    }
  });
  SanguinePlayer bluePlayer = new SanguinePlayer(Pawn.BLUE, new ControlTheBoardStrategy());
  ControlBoardMock model;

  /**
   * Sets up the mock model.
   */
  @Before
  public void setUp() {
    model = new ControlBoardMock(2, 3);
  }

  @Test
  public void testStrategyWorks() {
    model.setScore(0, 0, 0, 2);
    model.setScore(1, 1, 0, 3);
    CardMove expected = new CardMove(0, 1, 1);
    assertEquals(expected, redPlayer.chooseMove(model));
  }

  @Test
  public void testTwoMovesWithSameNumberOfCellOwnershipChoosesFirstRow() {
    model.setScore(0, 0, 0, 2);
    model.setScore(1, 0, 0, 2);
    CardMove expected = new CardMove(0, 0, 0);
    assertEquals(expected, redPlayer.chooseMove(model));
  }

  @Test
  public void testTwoMovesWithSameNumberOfCellOwnershipChoosesFirstColumn() {
    model.setScore(1, 0, 0, 2);
    model.setScore(1, 1, 0, 2);
    CardMove expected = new CardMove(0, 1, 0);
    assertEquals(expected, redPlayer.chooseMove(model));
  }

  @Test
  public void testTwoMovesWithSameNumberOfCellOwnershipChoosesFirstCard() {
    model.setScore(1, 1, 0, 2);
    model.setScore(1, 1, 1, 2);
    CardMove expected = new CardMove(0, 1, 1);
    assertEquals(expected, redPlayer.chooseMove(model));
  }

  @Test
  public void testPassMove() {
    CardMove expected = new CardMove();
    assertEquals(expected, redPlayer.chooseMove(model));
  }

  @Test
  public void testWrongTurn() {
    assertThrows(IllegalStateException.class, () -> bluePlayer.chooseMove(model));
  }
}