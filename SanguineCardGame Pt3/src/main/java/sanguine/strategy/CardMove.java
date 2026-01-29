package sanguine.strategy;

/**
 * Represents a move in the game, which can either be a pass or placing a card
 * from the player's hand onto the board.
 */
public class CardMove implements Move {
  private final boolean pass;
  private final int row;
  private final int col;
  private final int handIndex;

  /**
   * Creates a pass move where no card is played.
   */
  public CardMove() {
    this.pass = true;
    this.row = -1;
    this.col = -1;
    this.handIndex = -1;
  }

  /**
   * Creates a move that places a card from the hand onto the board.
   *
   * @param handIndex the index of the card in the player's hand to play
   * @param row the row position on the board to place the card
   * @param col the column position on the board to place the card
   */
  public CardMove(int handIndex, int row, int col) {
    this.pass = false;
    if (row < 0 || col < 0 || handIndex < 0) {
      throw new IllegalArgumentException("Invalid argument(s)!\n");
    }
    this.row = row;
    this.col = col;
    this.handIndex = handIndex;
  }

  /**
   * Checks if this move is a pass.
   *
   * @return true if the move is a pass, false otherwise
   */
  @Override
  public boolean isPass() {
    return pass;
  }

  /**
   * Gets the row where the card should be placed.
   *
   * @return the row position
   * @throws IllegalStateException if this is a pass move
   */
  @Override
  public int getRow() throws IllegalStateException {
    if (pass) {
      throw new IllegalStateException("Can't get row from pass move!\n");
    }
    return row;
  }

  /**
   * Gets the column where the card should be placed.
   *
   * @return the column position
   * @throws IllegalStateException if this is a pass move
   */
  @Override
  public int getCol() throws IllegalStateException {
    if (pass) {
      throw new IllegalStateException("Can't get column from pass move!\n");
    }
    return col;
  }

  /**
   * Gets the index of the card in the player's hand to be played.
   *
   * @return the hand index
   * @throws IllegalStateException if this is a pass move
   */
  @Override
  public int getHandIndex() throws IllegalStateException {
    if (pass) {
      throw new IllegalStateException("Can't get hand index from pass move!\n");
    }
    return handIndex;
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof CardMove move) {
      if (this.isPass()) {
        return move.isPass();
      } else {
        return this.getRow() == move.getRow()
            && this.getCol() == move.getCol()
            && this.getHandIndex() == move.getHandIndex();
      }
    }
    return false;
  }
}