package sanguine.mocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import sanguine.model.Card;
import sanguine.model.CellContent;
import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.model.SanguineCell;

/**
 * This mock class tracks the coordinates of what is checked.
 */
public class RowColumnTrackerMock implements ReadOnlySanguineModel {
  private final Appendable log;
  private final int numRows;
  private final int numCols;
  private final int handSize;
  private int legalRow = -1;
  private int legalCol = -1;
  private int legalHand = -1;

  /**
   * Initializes the log.
   *
   * @param log the appendable log
   */
  public RowColumnTrackerMock(Appendable log, int numRows, int numCols, int handSize) {
    if (log == null) {
      throw new IllegalArgumentException("Can't have a null log!\n");
    }
    this.log = log;
    this.numRows = numRows;
    this.numCols = numCols;
    this.handSize = handSize;
  }

  @Override
  public boolean isGameOver() throws IllegalStateException {
    return false;
  }

  @Override
  public Pawn getWinner() throws IllegalStateException {
    return null;
  }

  @Override
  public Pawn getTurn() throws IllegalStateException {
    return Pawn.RED;
  }

  @Override
  public SanguineCell[][] getBoard() throws IllegalStateException {
    return new SanguineCell[0][];
  }

  @Override
  public int getRedRowScore(int index) throws IllegalArgumentException, IllegalStateException {
    return 0;
  }

  @Override
  public int getBlueRowScore(int index) throws IllegalArgumentException, IllegalStateException {
    return 0;
  }

  @Override
  public int getRedScore() throws IllegalStateException {
    return 0;
  }

  @Override
  public int getBlueScore() throws IllegalStateException {
    return 0;
  }

  @Override
  public List<Card> getRedPlayerHand() throws IllegalStateException {
    List<Card> hand = new ArrayList<>();
    for (int i = 0; i < handSize; i++) {
      hand.add(new MockCard());
    }
    return hand;
  }

  @Override
  public List<Card> getBluePlayerHand() throws IllegalStateException {
    return List.of();
  }

  @Override
  public int getNumRows() throws IllegalStateException {
    return numRows;
  }

  @Override
  public int getNumCols() throws IllegalStateException {
    return numCols;
  }

  @Override
  public List<CellContent> getCellContent(int row, int col)
      throws IllegalArgumentException, IllegalStateException {
    return List.of();
  }

  @Override
  public Pawn getCellOwnership(int row, int col)
      throws IllegalArgumentException, IllegalStateException {
    return null;
  }

  @Override
  public boolean isLegal(int row, int col, int handIndex)
      throws IllegalArgumentException, IllegalStateException {
    try {
      log.append(String.format("%d %d %d\n", row, col, handIndex));
    } catch (IOException ignored) {
      // ignore
    }
    return row == legalRow && col == legalCol && handIndex == legalHand;
  }

  /**
   * Sets the legal move accordingly to what we want to test.
   *
   * @param row the row index
   * @param col the column index
   * @param hand the hand size
   */
  public void setLegalMove(int row, int col, int hand) {
    this.legalRow = row;
    this.legalCol = col;
    this.legalHand = hand;
  }
}
