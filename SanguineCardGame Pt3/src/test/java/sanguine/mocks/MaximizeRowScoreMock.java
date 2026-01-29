package sanguine.mocks;

import java.io.IOException;
import java.util.List;
import sanguine.model.Card;
import sanguine.model.CellContent;
import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.model.SanguineCell;

/**
 * This mock class makes so you can set one row to be a valid row through customization.
 * The game starts with zero scores throughout all rows.
 */
public class MaximizeRowScoreMock implements ReadOnlySanguineModel {
  private final Appendable log;
  private final int numRows;
  private final int numCols;
  private final int handSize;
  private final int[] redRowScores;
  private final int[] blueRowScores;

  /**
   * All scores start at zero.
   */
  public MaximizeRowScoreMock(Appendable log) {
    if (log == null) {
      throw new IllegalArgumentException("Can't have a null log!\n");
    }
    this.log = log;
    numRows = 3;
    numCols = 5;
    handSize = 2;
    redRowScores = new int[] {0, 0, 0};
    blueRowScores = new int[] {0, 0, 0};
  }

  /**
   * Manipulate the row scores to the desired arrays for each player.
   *
   * @param redRowScores the red row scores as an array
   * @param blueRowScores the blue row scores as an array
   */
  public MaximizeRowScoreMock(Appendable log, int[] redRowScores, int [] blueRowScores) {
    if (log == null) {
      throw new IllegalArgumentException("Can't have a null log!\n");
    }
    this.log = log;
    numRows = 3;
    numCols = 5;
    handSize = 2;
    this.redRowScores = redRowScores;
    this.blueRowScores = blueRowScores;
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
    return redRowScores[index];
  }

  @Override
  public int getBlueRowScore(int index) throws IllegalArgumentException, IllegalStateException {
    return blueRowScores[index];
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
    return List.of(new MockCard(2), new MockCard(2));
  }

  @Override
  public List<Card> getBluePlayerHand() throws IllegalStateException {
    return List.of(new MockCard(2), new MockCard(2));
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
    return getRedRowScore(row) <= getBlueRowScore(row);
  }
}
