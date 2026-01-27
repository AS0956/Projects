package klondike.model.hw04;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import klondike.model.hw02.BasicCard;
import klondike.model.hw02.BasicKlondike;
import klondike.model.hw02.Card;
import klondike.model.hw02.Pile;
import klondike.model.hw02.Suit;

/**
 * Whitehead variation of the Klondike Game.
 */
public class WhiteheadKlondike extends BasicKlondike {

  /**
   * Creates a new Whitehead Klondike game.
   */
  public WhiteheadKlondike() {
    super();
  }

  /**
   * Creates a new Whitehead Klondike game with custom draw cards.
   *
   * @param numDraw number of cards visible at one time
   */
  public WhiteheadKlondike(int numDraw) {
    super();
    this.numDrawCards = numDraw;
  }

  /**
   * Starts a new game of Whitehead Klondike with given deck.
   * Deals the cards face-up to cascade piles
   * and sets up the state of the game.
   *
   * @param deck     the deck of cards used in the game
   * @param shuffle  decides whether to shuffle deck before dealing it
   * @param numPiles number of cascade piles
   * @param numDraw  number of draw cards
   */
  @Override
  public void startGame(List<BasicCard> deck, boolean shuffle, int numPiles, int numDraw) {
    if (deck == null) {
      throw new IllegalArgumentException("Deck cannot be null");
    }
    for (BasicCard card : deck) {
      if (card == null) {
        throw new IllegalArgumentException("Card cannot be null");
      }
    }
    if (numPiles <= 0 || numDraw <= 0) {
      throw new IllegalArgumentException("Invalid parameters");
    }
    int requiredCards = (numPiles * (numPiles + 1)) / 2;
    if (deck.size() < requiredCards) {
      throw new IllegalArgumentException("Invalid parameters");
    }

    List<BasicCard> workingDeck = new ArrayList<>(deck);
    if (shuffle) {
      Collections.shuffle(workingDeck);
    }

    cascadePiles = new ArrayList<>();
    drawPiles = new ArrayList<>();
    foundationPiles = new ArrayList<>();

    this.numPiles = numPiles;
    this.numDrawCards = numDraw;

    for (int i = 0; i < numPiles; i++) {
      cascadePiles.add(new Pile());
    }

    for (int row = 0; row < numPiles; row++) {
      for (int pile = row; pile < numPiles; pile++) {
        BasicCard card = workingDeck.remove(0);
        cascadePiles.get(pile).addCardFaceUp(card);
      }
    }

    for (int i = 0; i < 4; i++) {
      foundationPiles.add(new Pile());
    }

    while (!workingDeck.isEmpty()) {
      drawPiles.add(workingDeck.remove(0));
    }
    gameStarted = true;
  }

  @Override
  public void movePile(int srcPile, int numCards, int destPile) {
    if (!gameStarted) {
      throw new IllegalStateException("Game not started");
    }
    if (srcPile < 0 || srcPile >= cascadePiles.size()
        || destPile < 0 || destPile >= cascadePiles.size()) {
      throw new IllegalArgumentException("Invalid pile index");
    }

    if (numCards <= 0 || srcPile == destPile) {
      throw new IllegalArgumentException("Invalid number of cards or pile index");
    }

    Pile source = cascadePiles.get(srcPile);
    final Pile dest = cascadePiles.get(destPile);

    if (numCards > source.size()) {
      throw new IllegalArgumentException("Invalid number of cards or pile index");
    }

    int startIndex = source.size() - numCards;

    List<Card> cardsToMove = new ArrayList<>();
    for (int i = startIndex; i < source.size(); i++) {
      cardsToMove.add(source.getCardAt(i));
    }

    if (!isSingleSuit(cardsToMove)) {
      throw new IllegalStateException("Must be same suit to move");
    }

    BasicCard bottom = (BasicCard) cardsToMove.get(0);

    if (!dest.isEmpty()) {
      BasicCard topDest = (BasicCard) dest.peekTop();

      if (!isSameColor(bottom, topDest)) {
        throw new IllegalStateException("Must be same color");
      }

      if (!isOneRankLower(bottom, topDest)) {
        throw new IllegalStateException("Must place on card one rank higher");
      }
    }

    dest.addBuild(cardsToMove);
    source.removeFrom(startIndex);
  }

  /**
   * Moves the top draw card to the cascade pile specified.
   * The card needs to match
   * the color and be a rank lower
   * than the dest card.
   *
   * @param destPile the index of the destination cascade pile
   * @throws IllegalArgumentException if destPile is out of bounds
   * @throws IllegalStateException if game did not start, drawPile is empty,
   *                               or violates color/rank rules.
   */

  @Override
  public void moveDraw(int destPile) throws IllegalArgumentException, IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }
    if (drawPiles.isEmpty()) {
      throw new IllegalStateException("Draw piles cannot be empty");
    }
    if (destPile < 0 || destPile >= cascadePiles.size()) {
      throw new IllegalArgumentException("Pile out of bounds");
    }

    BasicCard drawCard = drawPiles.get(0);
    Pile destinationPile = cascadePiles.get(destPile);

    if (destinationPile.size() != 0) {
      BasicCard topDestCard = (BasicCard) destinationPile.peekTop();

      if (!isSameColor(drawCard, topDestCard)) {
        throw new IllegalStateException("Must be same color");
      }

      if (!isOneRankLower(drawCard, topDestCard)) {
        throw new IllegalStateException("Invalid move");
      }
    }

    destinationPile.addCardFaceUp(drawCard);
    drawPiles.remove(0);
  }

  private boolean isSingleSuit(List<Card> cards) {
    if (cards.isEmpty()) {
      return true;
    }
    Suit suit = ((BasicCard) cards.get(0)).getSuit();
    for (Card c : cards) {
      if (!((BasicCard) c).getSuit().equals(suit)) {
        return false;
      }
    }
    return true;
  }

  private boolean isSameColor(BasicCard c1, BasicCard c2) {
    return c1.getSuit().isRed() == c2.getSuit().isRed();
  }

  private boolean isOneRankLower(BasicCard c1, BasicCard c2) {
    return getRankValue(c1.getRank()) + 1 == getRankValue(c2.getRank());
  }

  /**
   * Main method to show WhiteheadKlondike game.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    WhiteheadKlondike game = new WhiteheadKlondike();
    List<BasicCard> deck = game.createNewDeck();
    game.startGame(deck, false, 7, 3);
    System.out.println(game.toString());
  }
}
