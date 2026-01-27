package klondike.model.hw02;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a pile of cards used in the game klondike.
 * A pile keeps track of its acrsd and which ones are face up in the pile.
 */
public class Pile {
  private List<Card> cards;
  private int firstFaceUpCard;

  /**
   * Makes an empty pile with no cards in it.
   */
  public Pile() {
    this.cards = new ArrayList<>();
    this.firstFaceUpCard = -1;
  }

  /**
   * Checks if the pile has no cards.
   *
   * @return true if the pile empty, but false otherwise
   */
  public boolean isEmpty() {
    return this.cards.isEmpty();
  }

  /**
   * Modifies the index of the first face up card if the pile changed.
   */

  public void flipTopIfNeeded() {
    if (this.cards.isEmpty()) {
      firstFaceUpCard = -1;
    } else if (firstFaceUpCard >= this.cards.size()) {
      firstFaceUpCard = cards.size() - 1;
    }
  }

  /**
   * Returns the top card of the pile without removing it.
   *
   * @return the top card or null if the pile is empty.
   */

  public Card peekTop() {
    if (isEmpty()) {
      return null;
    } else {
      return this.cards.get(cards.size() - 1);
    }
  }

  /**
   * Gets the card at a specified index in the pile.
   *
   * @param index the position of the card
   * @return the card at that index
   * @throws IllegalArgumentException if the index is invalid
   */

  public Card getCardAt(int index) {
    if (index < 0 || index >= cards.size()) {
      throw new IllegalArgumentException("Invalid index for card");
    }
    return this.cards.get(index);
  }

  /**
   * Checks if the card at the specified indedx is face up.
   *
   * @param index the card's position in the pile.
   * @return true if the card is face up only, else false.
   * @throws IllegalArgumentException if the index is invalid.
   */

  public boolean isCardFaceUp(int index) {
    if (index < 0 || index >= cards.size()) {
      throw new IllegalArgumentException("Not valid card index");
    }
    return index >= firstFaceUpCard;
  }

  /**
   * Removes and returns the top card from the pile.
   *
   * @return the card removed
   * @throws IllegalStateException if the pile is empty
   */

  public Card removeTopCard() {
    if (isEmpty()) {
      throw new IllegalStateException("Pile is empty");
    }
    Card removedCard = this.cards.remove(cards.size() - 1);
    flipTopIfNeeded();
    return removedCard;
  }

  /**
   * Removes all cards from a specified index to the top of the pile.
   *
   * @param index the starting index
   * @return the list of removed cards
   * @throws IllegalArgumentException if the index is invalid
   * @throws IllegalStateException    if the card at the index is face down
   */
  public List<Card> removeFrom(int index) {
    if (index < 0 || index >= cards.size()) {
      throw new IllegalArgumentException("Not valid card index");
    }
    if (index < firstFaceUpCard) {
      throw new IllegalStateException("Invalid card index");
    }
    List<Card> removedCards = new ArrayList<>();
    removedCards = new ArrayList<>(cards.subList(index, cards.size()));
    cards.subList(index, cards.size()).clear();
    flipTopIfNeeded();
    return removedCards;
  }

  /**
   * Adds a sequence of cards to the current pile.
   *
   * @param build the cards to add
   */
  public void addBuild(List<Card> build) {
    this.cards.addAll(build);
    if (firstFaceUpCard == -1) {
      firstFaceUpCard = cards.size() - build.size();
    }
  }

  /**
   * Returns copy of face-up cards in the pile.
   *
   * @return a list of face-up cards
   */
  public List<Card> getFaceUpCardsCopy() {
    if (firstFaceUpCard == -1) {
      return new ArrayList<>();
    } else {
      return new ArrayList<>(cards.subList(firstFaceUpCard, cards.size()));
    }
  }

  /**
   * Returns copy of all the cards in the pile.
   *
   * @return a list of all cards
   */
  public List<Card> getAllCardsCopy() {
    return new ArrayList<>(cards);

  }

  /**
   * Adds a face-down card to the pile.
   *
   * @param card the card to add
   */
  public void addCardFaceDown(BasicCard card) {
    this.cards.add(card);
  }

  /**
   * Adds a face up card to the current pile.
   *
   * @param card the card to add
   */
  public void addCardFaceUp(BasicCard card) {
    int newIndex = this.cards.size();
    this.cards.add(card);
    if (firstFaceUpCard == -1 || newIndex < firstFaceUpCard) {
      firstFaceUpCard = newIndex;
    }
  }

  /**
   * returns the number of cards in the pile.
   *
   * @return the size of the pile
   */
  public int size() {
    return this.cards.size();
  }
}