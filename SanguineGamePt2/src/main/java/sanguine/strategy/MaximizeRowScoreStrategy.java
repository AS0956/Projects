package sanguine.strategy;

import sanguine.model.Card;
import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.model.SanguineModel;

/**
 * Strategy that tries to win rows by making row-score higher than opponent's.
 * Checks rows from top to bottom.
 */
public class MaximizeRowScoreStrategy implements PlayerStrategy {

  @Override
  public Move chooseMove(ReadOnlySanguineModel game, Pawn pawn) throws IllegalStateException {
    if (game.getTurn() != pawn) {
      throw new IllegalStateException("Not this player's turn!\n");
    }

    Pawn opponent = (pawn == Pawn.RED) ? Pawn.BLUE : Pawn.RED;
    int handSize = pawn.equals(Pawn.RED) ? game.getRedPlayerHand().size()
        : game.getBluePlayerHand().size();

    for (int row = 0; row < game.getNumRows(); row++) {
      int playerScore = pawn.equals(Pawn.RED) ? game.getRedRowScore(row)
          : game.getBlueRowScore(row);
      int opponentScore = opponent.equals(Pawn.RED) ? game.getRedRowScore(row)
          : game.getBlueRowScore(row);

      if (playerScore <= opponentScore) {
        for (int cardIdx = 0; cardIdx < handSize; cardIdx++) {
          for (int col = 0; col < game.getNumCols(); col++) {
            if (game.isLegal(row, col, cardIdx)) {
              int newScore = calculateScoreAfterMove(game, pawn, cardIdx, row);

              if (newScore > opponentScore) {
                return new CardMove(cardIdx, row, col);
              }
            }
          }
        }
      }
    }

    return new CardMove();
  }

  private int calculateScoreAfterMove(ReadOnlySanguineModel game, Pawn pawn,
                                      int cardIndex, int row) {
    int currentScore = pawn.equals(Pawn.RED) ? game.getRedRowScore(row)
        : game.getBlueRowScore(row);

    Card card = pawn.equals(Pawn.RED) ? game.getRedPlayerHand().get(cardIndex)
        : game.getBluePlayerHand().get(cardIndex);

    return currentScore + card.getValue();
  }
}