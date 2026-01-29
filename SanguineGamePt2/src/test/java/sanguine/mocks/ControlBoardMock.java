package sanguine.mocks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import sanguine.model.Card;
import sanguine.model.CellContent;
import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.model.SanguineCell;

/**
 * Mocks the read only model to track the ownership of each cell.
 */
public class ControlBoardMock implements ReadOnlySanguineModel {
  private final int numRows;
  private final int numCols;
  private final Map<String, Integer> resultScores = new HashMap<>();
  private final Set<String> legals = new HashSet<>();

  /**
   * Initializes number of rows and columns.
   *
   * @param numRows the number of rows
   * @param numCols the number of columns
   */
  public ControlBoardMock(int numRows, int numCols) {
    if (numRows < 0 || numRows > 2 || numCols < 0 || numCols > 3) {
      throw new IllegalArgumentException();
    }
    this.numRows = numRows;
    this.numCols = numCols;
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
    return List.of(new MockCard(), new MockCard());
  }

  @Override
  public List<Card> getBluePlayerHand() throws IllegalStateException {
    return List.of(new MockCard(), new MockCard());
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
    return legals.contains(row + ", " + col + ", " + handIndex);
  }

  /**
   * Adds a new legal move and the score is the key correlating to the number of
   * owned cells for the current player after the move.
   *
   * @param row the row index
   * @param col the column index
   * @param hand the hand index
   * @param score the score
   */
  public void setScore(int row, int col, int hand, int score) {
    resultScores.put(row + ", " + col + ", " + hand, score);
    legals.add(row + ", " + col + ", " + hand);
  }

  /**
   * Gets the score at the specified row and column.
   *
   * @param row the row index
   * @param col the column index
   * @param handIndex the hand index
   * @return the score
   */
  public int getScore(int row, int col, int handIndex) {
    return resultScores.getOrDefault(row + ", " + col + ", " + handIndex, 0);
  }
}