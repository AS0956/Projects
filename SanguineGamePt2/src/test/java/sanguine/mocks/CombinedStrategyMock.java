package sanguine.mocks;

import java.util.List;
import sanguine.model.Card;
import sanguine.model.CellContent;
import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.model.SanguineCell;

/**
 * This mocks the read only with only 2 rows and 3 columns.
 */
public class CombinedStrategyMock implements ReadOnlySanguineModel {
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
    return 2;
  }

  @Override
  public int getNumCols() throws IllegalStateException {
    return 3;
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
    return false;
  }
}
