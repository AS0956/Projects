package sanguine.model;

import java.util.List;

/**
 * Represents a player.
 * A player can draw a card into their hand
 * and their score at each row can be updated throughout the game.
 * The observer methods should return a collection not aliased to
 * the actual player's fields.
 */
public interface Player {
  /**
   * Deals out the starting hand using the player's deck.
   *
   * @param numHand the starting number of cards in the player's hand
   */
  public void dealHand(int numHand);

  /**
   * Draws a card from the player's deck into their hand.
   * If the deck is empty,no cards are drawn.
   */
  public void drawCard();

  /**
   * Discards the card from the player's hand and updates
   * the player's score at that row.
   *
   * @param row       the row for the card to be placed in
   * @param handIndex the index indicating which card to play from the hand
   * @throws IllegalArgumentException if the row or hand index is invalid
   */
  public void useCard(int row, int handIndex) throws IllegalArgumentException;

  /**
   * Gets the player's current deck.
   *
   * @return the player's playing deck as a list of cards
   */
  public List<Card> getDeck();

  /**
   * Gets the player's current hand.
   *
   * @return the player's hand as a list of cards
   */
  public List<Card> getHand();

  /**
   * Gets the player's current row scores.
   *
   * @return the player's scores for each row as an array of ints
   */
  public int[] getScores();
}
