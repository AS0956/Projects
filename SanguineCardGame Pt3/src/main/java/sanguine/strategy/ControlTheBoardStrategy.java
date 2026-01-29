package sanguine.strategy;

import java.util.List;
import sanguine.model.Card;
import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;

/**
 * This strategy chooses the move that gives the player the most ownership of cells.
 */
public class ControlTheBoardStrategy extends AbstractStrategy implements PlayerStrategy {
  int count;
  Integer row;
  Integer col;
  Integer handIndex;

  @Override
  public Move chooseMove(ReadOnlySanguineModel game, Pawn pawn) throws IllegalStateException {
    count = 0;
    row = null;
    col = null;
    handIndex = null;

    if (game.getTurn() != pawn) {
      throw new IllegalStateException("Not this player's turn!\n");
    }

    List<Card> hand = pawn.equals(Pawn.RED) ? game.getRedPlayerHand()
        : game.getBluePlayerHand();
    int numRows = game.getNumRows();
    int numCols = game.getNumCols();

    for (int index = 0; index < hand.size(); index++) {
      for (int row = 0; row < numRows; row++) {
        for (int col = 0; col < game.getNumCols(); col++) {
          if (game.isLegal(row, col, index)) {
            int numCells = 0;
            for (int innerRow = 0; innerRow < numRows; innerRow++) {
              for (int innerCol = 0; innerCol < numCols; innerCol++) {
                if (simulateMove(game, pawn, row, col, index, hand)
                    .getCellOwnership(innerRow, innerCol) == pawn) {
                  numCells++;
                }
              }
            }
            if (numCells > count) {
              count = numCells;
              this.row = row;
              this.col = col;
              this.handIndex = index;
            }
          }
        }
      }
    }

    if (row == null) {
      return new CardMove();
    }
    return new CardMove(handIndex, row, col);
  }
}