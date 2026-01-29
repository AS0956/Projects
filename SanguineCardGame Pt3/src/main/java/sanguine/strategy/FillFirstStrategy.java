package sanguine.strategy;

import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.model.SanguineModel;

/**
 * A strategy that tries to place cards in the first available legal position.
 * Goes through each card in the hand and tries to place it on the board starting
 * from the top-left corner, moving left to right and top to bottom.
 */
public class FillFirstStrategy implements PlayerStrategy {

  /**
   * Chooses a move by finding the first legal position for any card in the hand.
   * Checks cards in order from the hand, and for each card checks board positions
   * row by row, column by column. Returns the first legal move found, or passes
   * if no legal moves exist.
   *
   * @param game the current game state
   * @param pawn the player making the move (RED or BLUE)
   * @return a CardMove representing the chosen move, or a pass move if no legal moves
   * @throws IllegalStateException if it's not the specified pawn's turn
   */
  @Override
  public Move chooseMove(ReadOnlySanguineModel game, Pawn pawn) throws IllegalStateException {
    if (game.getTurn() != pawn) {
      throw new IllegalStateException("Not this player's turn!\n");
    }

    int handSize = pawn.equals(Pawn.RED) ? game.getRedPlayerHand().size()
        : game.getBluePlayerHand().size();

    for (int cardIndex = 0; cardIndex < handSize; cardIndex++) {
      for (int row = 0; row < game.getNumRows(); row++) {
        for (int col = 0; col < game.getNumCols(); col++) {
          try {
            if (game.isLegal(row, col, cardIndex)) {
              return new CardMove(cardIndex, row, col);
            }
          } catch (IllegalArgumentException ignored) {
            // ignore and keep looking
          }
        }
      }
    }
    return new CardMove();
  }
}