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
 * This mock manipulates what is considered a legal move and the opponent's scores.
 */
public class MinimaxMock implements ReadOnlySanguineModel {
  private final int numRows;
  private final int numCols;
  private final Pawn turn;
  private final Set<String> legalMoves = new HashSet<>();
  private final Map<String, Integer> opponentScoresAfterMove = new HashMap<>();

  /**
   * Initializes the mock with 2 rows and 3 columns, and the current turn.
   *
   * @param turn the current turn
   */
  public MinimaxMock(Pawn turn) {
    if (turn == null) {
      throw new IllegalArgumentException();
    }
    this.numRows = 2;
    this.numCols = 3;
    this.turn = turn;
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
    return turn;
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
    return legalMoves.contains(row + ", " + col + ", " + handIndex);
  }

  /**
   * Sets a legal move.
   *
   * @param row the row index
   * @param col the column index
   * @param handIndex the hand index
   */
  public void setLegal(int row, int col, int handIndex) {
    legalMoves.add(row + ", " + col + ", " + handIndex);
  }

  /**
   * Sets the legal move to correlate to a number of cells owned by players.
   * This is more manipulation for testing.
   *
   * @param row the row index
   * @param col the column index
   * @param handIndex the hand index
   * @param score the opponent's number of owned cells
   */
  public void setOpponentScoreAfterMove(int row, int col, int handIndex, int score) {
    opponentScoresAfterMove.put(row + ", " + col + ", " + handIndex, score);
  }

  /**
   * Gets the opponent's number of cells after a certain move.
   *
   * @param row the row index
   * @param col the column index
   * @param handIndex the hand index
   * @return the number of cells owned by the opponent after a certain move
   */
  public int getOpponentScore(int row, int col, int handIndex) {
    return opponentScoresAfterMove.getOrDefault(row + ", " + col + ", " + handIndex,
        0);
  }
}
