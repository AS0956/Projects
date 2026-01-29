package sanguine.view;

/**
 * Represents actions that can be performed in the game.
 */
public interface Features {
  /**
   * Called when a card in hand is clicked.
   *
   * @param cardIndex the index of clicked card
   */
  void onCardSelected(int cardIndex);

  /**
   * Called when a cell on the board is clicked.
   *
   * @param row the row of the clicked cell
   * @param col the column of the clicked cell
   */
  void onCellSelected(int row, int col);

  /**
   * Called when user confirms their move.
   */
  void onConfirmMove();

  /**
   * Called when user passes their turn.
   */
  void onPass();

  /**
   * The controller tells the view whether a click is legal or not first,
   * before the view updates it visually. This method helps with communicating
   * a legal move between the controller and view.
   *
   * @return true if the move is legal, false otherwise
   */
  boolean canSelect();
}