package sanguine.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import sanguine.model.Card;
import sanguine.model.Influence;
import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.model.SanguineCard;
import sanguine.strategy.ControlTheBoardStrategy;
import sanguine.strategy.FillFirstStrategy;
import sanguine.strategy.MaximizeRowScoreStrategy;

/**
 * Tests for MachinePlayerActions.
 */
public class MachinePlayerActionTest {

  private MachinePlayerActions redMachine;
  private MachinePlayerActions blueMachine;
  private TestModel testModel;

  /**
   * Sets up the mock model and two players as machines with different strategies.
   */
  @Before
  public void setUp() {
    testModel = new TestModel();
    redMachine = new MachinePlayerActions(Pawn.RED, new FillFirstStrategy());
    blueMachine = new MachinePlayerActions(Pawn.BLUE, new MaximizeRowScoreStrategy());
  }

  @Test
  public void testMachinePlayerHasCorrectColor() {
    assertEquals("Red machine should have RED color",
        Pawn.RED, redMachine.getColor());
    assertEquals("Blue machine should have BLUE color",
        Pawn.BLUE, blueMachine.getColor());
  }

  @Test
  public void testMachinePlayerChoosesMove() {
    // Machine should choose row 0, col 0, handIndex 0
    int row = redMachine.chooseRow(testModel);
    int col = redMachine.chooseCol(testModel);
    int handIndex = redMachine.chooseHandIndex(testModel);

    assertEquals(0, row);
    assertEquals(0, col);
    assertEquals(0, handIndex);
  }

  @Test
  public void testMachinePlayerWithDifferentStrategies() {
    MachinePlayerActions fillFirst = new MachinePlayerActions(
        Pawn.RED, new FillFirstStrategy());
    MachinePlayerActions controlBoard = new MachinePlayerActions(
        Pawn.RED, new ControlTheBoardStrategy());

    // Both should return valid moves
    int row1 = fillFirst.chooseRow(testModel);
    int row2 = controlBoard.chooseRow(testModel);

    assertTrue(row1 >= 0);
    assertTrue(row2 >= 0);
  }

  @Test
  public void testClearMove() {
    int row = redMachine.chooseRow(testModel);
    int col = redMachine.chooseCol(testModel);
    int handIndex = redMachine.chooseHandIndex(testModel);

    // Clear the move
    redMachine.clearMove();
    assertEquals(handIndex, redMachine.chooseHandIndex(testModel));
  }

  // Simple test model mock
  private static class TestModel implements ReadOnlySanguineModel {

    @Override
    public Pawn getTurn() {
      return Pawn.RED;
    }

    @Override
    public int getNumRows() {
      return 5;
    }

    @Override
    public int getNumCols() {
      return 7;
    }

    @Override
    public List<Card> getRedPlayerHand() {
      // Return a hand with one dummy card
      List<Card> hand = new java.util.ArrayList<>();
      Influence[][] grid = new Influence[5][5];
      for (int row = 0; row < 5; row++) {
        for (int col = 0; col < 5; col++) {
          grid[row][col] = Influence.NONE;
        }
      }
      grid[2][2] = Influence.CARD;
      hand.add(new SanguineCard("Test", 1, 1, grid));
      return hand;
    }

    @Override
    public List<Card> getBluePlayerHand() {
      return getRedPlayerHand();
    }

    @Override
    public boolean isLegal(int row, int col, int handIndex) {
      return row == 0 && col == 0 && handIndex == 0;
    }

    @Override
    public Pawn getCellOwnership(int row, int col) {
      if (col == 0) {
        return Pawn.RED;
      }
      if (col == 6) {
        return Pawn.BLUE;
      }
      return null;
    }

    @Override
    public boolean isGameOver() {
      return false;
    }

    @Override
    public Pawn getWinner() {
      return null;
    }

    @Override
    public sanguine.model.SanguineCell[][] getBoard() {
      return new sanguine.model.SanguineCell[5][7];
    }

    @Override
    public int getRedRowScore(int index) {
      return 0;
    }

    @Override
    public int getBlueRowScore(int index) {
      return 0;
    }

    @Override
    public int getRedScore() {
      return 0;
    }

    @Override
    public int getBlueScore() {
      return 0;
    }

    @Override
    public java.util.List<sanguine.model.CellContent> getCellContent(int row, int col) {
      return new java.util.ArrayList<>();
    }
  }
}