package sanguine.strategy;

import java.util.List;
import sanguine.model.Card;
import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;

/**
 * This strategy chooses the move that would leave the opponent with no good moves to play.
 */
public class MinimaxStrategy extends AbstractStrategy implements PlayerStrategy {
  int bestMinValue = Integer.MAX_VALUE;
  Integer row;
  Integer col;
  Integer handIndex;

  @Override
  public Move chooseMove(ReadOnlySanguineModel game, Pawn pawn) throws IllegalStateException {
    if (game.getTurn() != pawn) {
      throw new IllegalStateException("Not this player's turn!\n");
    }
    this.row = null;
    this.col = null;
    this.handIndex = null;
    List<Card> hand = pawn.equals(Pawn.RED) ? game.getRedPlayerHand() : game.getBluePlayerHand();
    for (int handIndex = 0; handIndex < hand.size(); handIndex++) {
      for (int row = 0; row < game.getNumRows(); row++) {
        for (int col = 0; col < game.getNumCols(); col++) {
          if (game.isLegal(row, col, handIndex)) {
            ReadOnlySanguineModel newBoard = simulateMove(game, pawn,
                row, col, handIndex, hand);
            Pawn opponent = (pawn == Pawn.RED) ? Pawn.BLUE : Pawn.RED;
            int opponentBest = evaluateOpponentBestMove(newBoard, opponent);

            if (opponentBest < bestMinValue) {
              bestMinValue = opponentBest;
              this.row = row;
              this.col = col;
              this.handIndex = handIndex;
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

  /**
   * Evaluates the opponent's best move.
   * Calculates how many cells they end up owning after this move.
   *
   * @param model the game model
   * @param opponent the opponent player
   * @return the number of cells the opponent will end up owning after playing
   *         their best move
   */
  protected int evaluateOpponentBestMove(ReadOnlySanguineModel model, Pawn opponent) {
    List<Card> hand = opponent.equals(Pawn.RED) ? model.getRedPlayerHand()
        : model.getBluePlayerHand();
    int maxCells = 0;
    int numRows = model.getNumRows();
    int numCols = model.getNumCols();

    for (int handIndex = 0; handIndex < hand.size(); handIndex++) {
      for (int row = 0; row < numRows; row++) {
        for (int col = 0; col < numCols; col++) {
          if (model.isLegal(row, col, handIndex)) {
            int numCells = 0;
            for (int innerRow = 0; innerRow < numRows; innerRow++) {
              for (int innerCol = 0; innerCol < numCols; innerCol++) {
                if (simulateMove(model, opponent, row, col, handIndex, hand)
                    .getCellOwnership(innerRow, innerCol) == opponent) {
                  numCells++;
                }
              }
            }
            if (numCells > maxCells) {
              maxCells = numCells;
            }
          }
        }
      }
    }
    return maxCells;
  }
}
