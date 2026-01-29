package sanguine.strategy;

import java.util.List;
import sanguine.model.Card;
import sanguine.model.Influence;
import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;

/**
 * This class contains the common method that some strategies have.
 */
public class AbstractStrategy {
  /**
   * Simulates the result of playing a move.
   *
   * @param game the current game state
   * @param pawn indicator of whose turn it is currently
   * @param playRow the row index
   * @param playCol the column index
   * @param handIndex the hand index
   * @param hand the current player's hand
   * @return a deep copy of the new game state
   */
  protected ReadOnlySanguineModel simulateMove(ReadOnlySanguineModel game,
                                                     Pawn pawn, int playRow, int playCol,
                                                     int handIndex, List<Card> hand) {
    Card card = hand.get(handIndex);
    Influence[][] grid = card.getGrid();

    int numRows = game.getNumRows();
    int numCols = game.getNumCols();
    Pawn[][] ownership = new Pawn[numRows][numCols];
    for (int row = 0; row < numRows; row++) {
      for (int col = 0; col < numCols; col++) {
        ownership[row][col] = game.getCellOwnership(row, col);
      }
    }

    for (int gridRow = 0; gridRow < grid.length; gridRow++) {
      for (int gridCol  = 0; gridCol < grid[0].length; gridCol++) {
        if (grid[gridRow][gridCol] != Influence.INFLUENCE) {
          continue;
        }

        int boardRow = playRow + (gridRow - 2);
        int boardCol = pawn == Pawn.RED ? playCol + (gridCol - 2) : playCol - (gridCol - 2);

        if (boardRow < 0 || boardRow >= numRows || boardCol < 0 || boardCol >= numCols) {
          continue;
        }

        ownership[boardRow][boardCol] = pawn;
      }
    }

    return new SimulatedBoard(ownership, numRows, numCols, pawn);
  }
}
