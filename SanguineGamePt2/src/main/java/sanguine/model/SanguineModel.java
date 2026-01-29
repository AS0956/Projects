package sanguine.model;

import java.util.List;

/**
 * Interface for the SanguineModel.
 * Represents operations for the two-player strategy game.
 * An implementation of this model means that the game can be modified through
 * action methods.
 */
public interface SanguineModel extends ReadOnlySanguineModel {
  /**
   * Starts the game with the given decks and initial hand size.
   *
   * @param redDeck  the deck of cards for the red player
   * @param blueDeck the deck of cards for the blue player
   * @param numRows  the number of rows for the board
   * @param numCols  the number of columns for the board
   * @param numHand  the maximum number of cards that can be in a player's hand at once
   * @throws IllegalArgumentException if there aren't enough cards in each deck to fill every
   *                                  cell on the board, if there are more than 3 of the same
   *                                  card in a deck, if the number of rows is less than 1,
   *                                  if the number of columns is less than 3 or is even,
   *                                  and if the number of rows is equal to the number of columns
   * @throws IllegalStateException    if game already started
   */
  void startGame(List<Card> redDeck, List<Card> blueDeck, int numRows, int numCols, int numHand)
      throws IllegalArgumentException, IllegalStateException;

  /**
   * Draws a card into the current player's hand, nothing happens if the deck is empty
   * and there are no more cards that can be drawn.
   *
   * @throws IllegalStateException if the game hasn't started yet or game is over
   */
  void drawCard() throws IllegalStateException;

  /**
   * Ends the current player's turn, making it the opponent's turn.
   *
   * @throws IllegalStateException if the game hasn't started yet or game is over
   *                               or if a card wasn't drawn first
   */
  void pass() throws IllegalStateException;

  /**
   * Places a card from the current player's hand onto the board.
   * Removes the card from hand, applies influence of the card to the board, draws a card,
   * and switches the turn to the other player.
   *
   * @param row       the row position on the board
   * @param col       the column position on the board
   * @param handIndex the index of the card in the player's hand
   * @throws IllegalArgumentException if row, column, or handIndex is invalid, if the placement
   *                                  on the board is invalid, or if the move is illegal
   * @throws IllegalStateException    if the game hasn't started yet or game is over
   *                                  or if a card wasn't drawn first
   */
  void placeCard(int row, int col, int handIndex)
      throws IllegalArgumentException, IllegalStateException;
}