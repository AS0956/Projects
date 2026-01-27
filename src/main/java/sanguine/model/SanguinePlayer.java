package sanguine.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a player in the Sanguine game.
 * Each player has a deck, a hand dealt from the deck,
 * and scores for each row on the board.
 */
public class SanguinePlayer implements Player {
  private final List<Card> deck;
  private final List<Card> hand;
  private final int[] scores;
  private final int numHand;

  /**
   * Assigns the player a playing deck and hands out the cards
   * in the deck, initializing a starting hand.
   * The maximum number of cards in a player's hand at any given time
   * can't be greater than a third of the given deck size.
   * All scores are initially initialized to zero.
   *
   * @param deck    the player's deck
   * @param numHand the starting number of cards in the player's hand
   * @param numRows the number of rows the game board has
   * @throws IllegalArgumentException if any of the arguments are invalid
   */
  public SanguinePlayer(List<Card> deck, int numHand, int numRows) throws IllegalArgumentException {
    if (deck == null || deck.isEmpty()) {
      throw new IllegalArgumentException("Null or empty deck!\n");
    } else if (numHand < 1 || numHand > (deck.size() / 3)) {
      throw new IllegalArgumentException("The hand has to have at least one card and "
          + "the number of cards in a player's hand can't "
          + "be greater than a third of the given deck!\n");
    } else if (numRows < 1) {
      throw new IllegalArgumentException("There has to be at least one row!\n");
    }
    this.deck = new ArrayList<>(deck);
    this.hand = new ArrayList<>();
    this.scores = new int[numRows];
    this.numHand = numHand;
  }

  /**
   * Constructs a SanguinePlayer with a given player.
   * This constructor is used to create a copy of a player without holding an alias to it.
   *
   * @param player the player to be copied
   * @throws IllegalArgumentException if the given SanguinePlayer is null
   */
  public SanguinePlayer(SanguinePlayer player) throws IllegalArgumentException {
    if (player == null) {
      throw new IllegalArgumentException("Can't have a null player!\n");
    }
    this.deck = new ArrayList<>(player.deck);
    this.hand = new ArrayList<>(player.hand);
    this.scores = Arrays.copyOf(player.scores, player.scores.length);
    this.numHand = player.numHand;
  }

  @Override
  public void dealHand(int numHand) {
    for (int index = 0; index < numHand; index++) {
      hand.add(this.deck.removeFirst());
    }
  }

  @Override
  public void drawCard() {
    if (!deck.isEmpty()) {
      hand.add(deck.removeFirst());
    }
  }

  @Override
  public void useCard(int row, int handIndex) throws IllegalArgumentException {
    if (handIndex < 0 || handIndex > hand.size() - 1) {
      throw new IllegalArgumentException("Invalid index!\n");
    }
    if (row < 0 || row > scores.length) {
      throw new IllegalArgumentException("Invalid row index!\n");
    }
    updateScore(row, hand.get(handIndex).getValue());
    hand.remove(handIndex);
  }

  private void updateScore(int row, int addValue) {
    if (row < 0 || row > scores.length - 1) {
      throw new IllegalArgumentException("Invalid row index!\n");
    } else if (addValue < 1) {
      throw new IllegalArgumentException("Value to add must be at least one!\n");
    }
    scores[row] += addValue;
  }

  @Override
  public List<Card> getDeck() {
    return List.copyOf(deck);
  }

  @Override
  public List<Card> getHand() {
    return List.copyOf(hand);
  }

  @Override
  public int[] getScores() {
    return Arrays.copyOf(scores, scores.length);
  }
}
