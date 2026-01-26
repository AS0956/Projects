package klondike.model.hw02;

import java.util.Objects;

/**
 * Shows a basic playing card with values suit and rank.
 * Class implements the Card interface.
 */
public class BasicCard implements Card {
  /**
   * The suit of the card (spades,hearts,clubs,diamonds).
   */
  private Suit suit;

  /**
   * The rank of the card ("A","2","3", etc).
   */
  private String rank;

  /**
   * Constructs the BasicCard with the stated suit and rank.
   *
   * @param suit the suit of the card
   * @param rank the rank of the card
   */
  public BasicCard(Suit suit, String rank) {
    this.suit = suit;
    this.rank = rank;
  }

  /**
   * Returns the string represntation of this card.
   * The rank is followed by the suit symbol
   *
   * @return the string representation of card.
   */
  @Override
  public String toString() {
    return rank + suit.getSymbol();
  }

  /**
   * Compares this card to the object being identified.
   * The two cards are equal when they have same suit and rank
   *
   * @param obj the reference object with which to compare.
   * @return true if the cards are equal, false if anything else
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    BasicCard other = (BasicCard) obj;
    return Objects.equals(this.suit, other.suit) && Objects.equals(this.rank, other.rank);
  }

  /**
   * Returns hash code value for the card.
   *
   * @return hash code value based on the suit and rank.
   */
  @Override
  public int hashCode() {
    return Objects.hash(suit, rank);
  }

  /**
   * Returns suit of card.
   *
   * @return the suit.
   */
  public Suit getSuit() {
    return suit;
  }

  /**
   * Returns rank of card.
   *
   * @return the rank.
   */
  public String getRank() {
    return rank;
  }
}
