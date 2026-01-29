package sanguine.strategy;

import java.util.List;
import sanguine.model.Card;
import sanguine.model.CellContent;
import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.model.SanguineCell;

/**
 * This class is only used to simulate what the board would look like.
 */
public class SimulatedBoard implements ReadOnlySanguineModel {
  private final Pawn[][] ownershipOfCells;
  private final int numRows;
  private final int numCols;
  private final Pawn turn;

  /**
   * Initializes the board, number of rows and columns, and whose turn it is.
   *
   * @param ownershipOfCells a 2D array indicating who owns what cell
   * @param numRows the number of rows
   * @param numCols the number of columns
   * @param previousPlayer the previous player
   */
  public SimulatedBoard(Pawn[][] ownershipOfCells, int numRows, int numCols, Pawn previousPlayer) {
    this.ownershipOfCells = ownershipOfCells;
    this.numRows = numRows;
    this.numCols = numCols;
    this.turn = previousPlayer.equals(Pawn.RED) ? Pawn.BLUE : Pawn.RED;
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
    return List.of();
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
    return ownershipOfCells[row][col];
  }

  @Override
  public boolean isLegal(int row, int col, int handIndex)
      throws IllegalArgumentException, IllegalStateException {
    return false;
  }
}
