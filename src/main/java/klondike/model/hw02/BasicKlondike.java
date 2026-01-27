package klondike.model.hw02;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Implementation of the KlondikeModel interface.
 *
 * <p>Class models the original Klondike game with cascade pile, draw pile, and
 * foundation pile. Supports the operations to start game, move cards between given piles,
 * and check the state of the game.
 *
 */

public class BasicKlondike implements KlondikeModel<BasicCard> {

  protected List<Pile> cascadePiles;
  protected List<Pile> foundations;
  protected List<BasicCard> drawPiles;
  protected List<Pile> foundationPiles;
  protected int numPiles;
  protected int numDrawCards;
  protected boolean gameStarted = false;

  /**
   * Constructs new BasicKlondike model for the game with empty piles
   * standard settings.
   */
  public BasicKlondike() {
    cascadePiles = new ArrayList<>();
    foundations = new ArrayList<>();
    drawPiles = new ArrayList<>();
    foundationPiles = new ArrayList<>();
    numPiles = 0;
    numDrawCards = 0;
    gameStarted = false;
  }

  private void checkGameStarted() {
    if (!gameStarted) {
      throw new IllegalStateException("Game has not been started");
    }

  }

  /**
   * Creates and returns the standard 52-deck of BasicCard.
   * The deck has four suits (spades, hearts, diamonds, clubs)
   * with ranks "A" all the way to "K"
   *
   * @return list representing a new deck of cards
   */

  @Override
  public List<BasicCard> createNewDeck() {
    List<BasicCard> deck = new ArrayList<>();
    String[] ranks = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    Suit[] suits = {Suit.spades, Suit.hearts, Suit.diamonds, Suit.clubs};
    for (Suit suit : suits) {
      for (String rank : ranks) {
        deck.add(new BasicCard(suit, rank));
      }
    }
    return deck;
  }

  /**
   * Starts new Klondike game.
   *
   * <p>Deals the cards into cascade piles and
   * sets up draw and foundation piles based on the rules.
   *
   * @param deck     the deck of cards used in the game
   * @param shuffle  decides whether to shuffle deck before dealing it
   * @param numPiles number of cascade piles
   * @param numDraw  number of draw cards
   * @throws IllegalArgumentException if deck is invalid or game parameters are incorrect
   */

  @Override
  public void startGame(List<BasicCard> deck, boolean shuffle, int numPiles, int numDraw) {
    if (deck == null) {
      throw new IllegalArgumentException("Deck can't be null");
    }
    for (BasicCard card : deck) {
      if (card == null) {
        throw new IllegalArgumentException("Card can't be null");
      }
    }
    if (numPiles <= 0) {
      throw new IllegalArgumentException("Number of piles must be greater than 0");
    }
    if (numDraw <= 0) {
      throw new IllegalArgumentException("Number of draw cards must be greater than 0");
    }
    int requiredCards = (numPiles * (numPiles + 1)) / 2;
    if (deck.size() < requiredCards) {
      throw new IllegalArgumentException("Not enough cards to start the game");
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

    int cardIndex = 0;
    for (int i = 0; i < numPiles; i++) {
      cascadePiles.add(new Pile());
    }


    for (int row = 0; row < numPiles; row++) {
      for (int pile = row; pile < numPiles; pile++) {
        BasicCard card = workingDeck.remove(0);
        if (row == pile) {
          cascadePiles.get(pile).addCardFaceUp(card);
        } else {
          cascadePiles.get(pile).addCardFaceDown(card);
        }
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

  /**
   * Moves the cards from a cascade pile to another one
   * based on the klondike rules.
   *
   * @param srcPile  the 0-based index (from the left) of the pile to be moved
   * @param numCards how many cards to be moved from that pile
   * @param destPile the 0-based index (from the left) of the destination pile for the moved cards
   * @throws IllegalStateException    if the game has not been started or if
   *                                  any move rules have been violated
   * @throws IllegalArgumentException if card counts are invalid or the pile
   *                                  indices are not valid.
   */

  @Override
  public void movePile(int srcPile, int numCards, int destPile) {
    checkGameStarted();
    if (srcPile < 0 || srcPile >= cascadePiles.size()
        || destPile < 0 || destPile >= cascadePiles.size()) {
      throw new IllegalArgumentException("Pile out of bounds");
    }
    if (numCards <= 0) {
      throw new IllegalArgumentException("Number of Cards must be greater than 0");
    }
    if (srcPile == destPile) {
      throw new IllegalArgumentException("Pile out of bounds");
    }

    Pile source = cascadePiles.get(srcPile);
    final Pile destination = cascadePiles.get(destPile);

    if (numCards > source.size()) {
      throw new IllegalArgumentException("Number of Cards must be less than srcPile");
    }

    int startIndex = source.size() - numCards;

    for (int i = startIndex; i < source.size(); i++) {
      if (!source.isCardFaceUp(i)) {
        throw new IllegalArgumentException("All the cards in this move are not faceup");
      }
    }

    List<Card> movingStack = new ArrayList<>();
    for (int i = startIndex; i < source.size(); i++) {
      movingStack.add(source.getCardAt(i));
    }

    BasicCard bottomMovingCard = (BasicCard) movingStack.get(0);

    if (destination.size() == 0) {
      if (!bottomMovingCard.getRank().equals("K")) {
        throw new IllegalStateException("Only can move king to empty pile");
      }
    } else {
      BasicCard topDestCard = (BasicCard) destination.peekTop();
      if (!isOneRankLower(bottomMovingCard, topDestCard)
          || !isOppositeColor(bottomMovingCard, topDestCard)) {
        throw new IllegalStateException("All the cards in this move are not opposite");
      }
    }
    destination.addBuild(movingStack);
    source.removeFrom(startIndex);
    source.flipTopIfNeeded();
  }

  /**
   * Moves the topmost draw card to a specific cascade pile.
   *
   * @param destPile the index of the destination cascade pile
   * @throws IllegalArgumentException if the game has not started or the move
   *                                  made is invalid
   * @throws IllegalStateException    if the index of destination pile is valid
   *                                  or not
   */
  @Override
  public void moveDraw(int destPile) throws IllegalArgumentException, IllegalStateException {
    checkGameStarted();
    if (drawPiles.isEmpty()) {
      throw new IllegalStateException("Draw piles cannot be empty");
    }

    if (destPile < 0 || destPile >= cascadePiles.size()) {
      throw new IllegalArgumentException("Pile out of bounds");
    }

    BasicCard drawCard = drawPiles.get(0);

    Pile destinationPile = cascadePiles.get(destPile);


    if (destinationPile.size() == 0) {
      if (!drawCard.getRank().equals("K")) {
        throw new IllegalStateException("Only king can move to empty pile");
      }
    } else {
      BasicCard topDestCard = (BasicCard) destinationPile.peekTop();
      if (!isOneRankLower(drawCard, topDestCard)  // Fixed order
          || !isOppositeColor(drawCard, topDestCard)) {
        throw new IllegalStateException("Invalid move");  // Changed
      }
    }

    destinationPile.addCardFaceUp(drawCard);  // Changed to FaceUp
    drawPiles.remove(0);

  }

  /**
   * Moves the topmost card from the cascade pile to
   * a foundation pile based on the Klondike rules.
   *
   * @param srcPile        the source cascade pile index
   * @param foundationPile destination foundation pile index
   * @throws IllegalStateException    if the game didn't start or if the move
   *                                  if not valid.
   * @throws IllegalArgumentException checks validity of pile indices
   */

  @Override
  public void moveToFoundation(int srcPile, int foundationPile) {
    checkGameStarted();
    if (foundationPile < 0 || foundationPile >= foundationPiles.size()) {
      throw new IllegalArgumentException("Pile out of bounds");
    }
    if (srcPile < 0 || srcPile >= cascadePiles.size()) {
      throw new IllegalArgumentException("Pile out of bounds");
    }

    Pile foundation = foundationPiles.get(foundationPile);
    Pile sourcePile = cascadePiles.get(srcPile);

    if (sourcePile.size() == 0) {
      throw new IllegalStateException("Source pile is empty");
    }

    BasicCard cardToMove = (BasicCard) sourcePile.peekTop();

    if (foundation.size() == 0) {
      if (!cardToMove.getRank().equals("A")) {
        throw new IllegalStateException("Invalid foundation move");
      } else {
        foundation.addCardFaceUp(cardToMove);
        sourcePile.removeFrom(sourcePile.size() - 1);
        sourcePile.flipTopIfNeeded();
        return;
      }
    }

    BasicCard topFoundationCard = (BasicCard) foundation.peekTop();
    if (!cardToMove.getSuit().equals(topFoundationCard.getSuit())) {
      throw new IllegalStateException("Foundation move not valid");
    }
    if (getRankValue(cardToMove.getRank()) != getRankValue(topFoundationCard.getRank()) + 1) {
      throw new IllegalStateException("Foundation move not valid");
    }
    foundation.addCardFaceUp(cardToMove);
    sourcePile.removeFrom(sourcePile.size() - 1);
    sourcePile.flipTopIfNeeded();
  }

  /**
   * Checks if 2 cards have opposite colors.
   *
   * @param card1 first card to compare
   * @param card2 second card to compare
   * @return true if cards are opposite colors
   *         false otherwise
   */
  protected boolean isOppositeColor(Card card1, Card card2) {
    BasicCard c1 = (BasicCard) card1;
    BasicCard c2 = (BasicCard) card2;
    return ((c1.getSuit().isRed() && !c2.getSuit().isRed())
        || !c1.getSuit().isRed() && c2.getSuit().isRed());
  }

  /**
   *Checks if first card is one rank lower.
   * than the second card
   *
   * @param lower the card that should be one rank lower
   * @param higher the card that should be one rank higher
   * @return true if lower is one rank below higher
   *              false if not
   */
  protected boolean isOneRankLower(Card lower, Card higher) {
    BasicCard c1 = (BasicCard) lower;
    BasicCard c2 = (BasicCard) higher;
    return getRankValue(c1.getRank()) + 1 == getRankValue(c2.getRank());
  }

  /**
   *Returns the number value of card rank.
   *
   * @param rank the rank string ("A", "2", etc.)
   * @return the number value of rank
   * @throws IllegalArgumentException if rank is invalid
   */
  protected int getRankValue(String rank) {
    switch (rank) {
      case "A":
        return 1;
      case "2":
        return 2;
      case "3":
        return 3;
      case "4":
        return 4;
      case "5":
        return 5;
      case "6":
        return 6;
      case "7":
        return 7;
      case "8":
        return 8;
      case "9":
        return 9;
      case "10":
        return 10;
      case "J":
        return 11;
      case "Q":
        return 12;
      case "K":
        return 13;
      default:
        throw new IllegalArgumentException("Unknown rank");
    }
  }


  /**
   * Moves the topmost draw-pile card to a foundation pile.
   *
   * @param foundationPile the 0-based index (from the left) of the foundation pile to place card
   * @throws IllegalStateException if the game has not started draw pile is empty
   *                      or does not comply with foundation rules
   * @throws IllegalArgumentException if foundationPile index is not valid
   */
  @Override
  public void moveDrawToFoundation(int foundationPile) {
    if (!gameStarted) {
      throw new IllegalStateException("Game not started");
    }
    if (foundationPile < 0 || foundationPile >= foundationPiles.size()) {
      throw new IllegalArgumentException("Pile out of bounds");
    }
    if (drawPiles.isEmpty()) {
      throw new IllegalStateException("Draw pile has no cards");
    }
    BasicCard cardToMove = drawPiles.get(0);
    Pile foundation = foundationPiles.get(foundationPile);

    if (foundation.size() == 0) {
      if (!cardToMove.getRank().equals("A")) {
        throw new IllegalStateException("Must place Ace first");
      }
      foundation.addCardFaceUp(cardToMove);
      drawPiles.remove(0);
      return;
    }
    BasicCard topFoundationCard = (BasicCard) foundation.peekTop();

    if (!cardToMove.getSuit().equals(topFoundationCard.getSuit())) {
      throw new IllegalStateException("Foundation move not valid");
    }
    if (getRankValue(cardToMove.getRank()) != getRankValue(topFoundationCard.getRank()) + 1) {
      throw new IllegalStateException("Foundation move not valid");
    }

    foundation.addCardFaceUp(cardToMove);
    drawPiles.remove(0);
  }

  /**
   * Removes the topmost card form the draw pile to the bottom
   * of the draw pile.
   *
   * @throws IllegalStateException if the game has not started
   *                               or if the draw pile is empty
   */

  @Override
  public void discardDraw() throws IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game not started");
    }
    if (drawPiles.isEmpty()) {
      throw new IllegalStateException("Draw pile has no cards");
    }
    BasicCard firstCard = drawPiles.remove(0);
    drawPiles.add(firstCard);
  }

  /**
   * Returns the # of rws in the cascade piles (max pile height).
   *
   * @return the max number of cards in any cascade pile
   * @throws IllegalStateException if the game has not started
   */
  @Override
  public int getNumRows() throws IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game not started");
    }
    int maxHeight = 0;
    for (Pile pile : cascadePiles) {
      if (pile.size() > maxHeight) {
        maxHeight = pile.size();
      }
    }
    return maxHeight;
  }

  /**
   * Returns the # of cascade piles in the game.
   *
   * @return # of cascade piles
   * @throws IllegalStateException if the game has not started
   */

  @Override
  public int getNumPiles() throws IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game not started");
    }
    return numPiles;
  }

  /**
   * Returns the # of visible draw cards.
   *
   * @return # of draw cards
   * @throws IllegalStateException if the game has not started
   */

  @Override
  public int getNumDraw() throws IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game not started");
    }
    return Math.min(numDrawCards, drawPiles.size());
  }

  /**
   * Returns the game is over meaning all foundation piles are complete.
   *
   * @return true if the game is over and false if any other situation.
   * @throws IllegalStateException if the game has not started
   */
  @Override
  public boolean isGameOver() throws IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game not started");
    }
    for (Pile pile : foundationPiles) {
      if (pile.size() != 13) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns the current score.
   * which is the total rank values of the top cards in foundation piles.
   *
   * @return the current score
   * @throws IllegalStateException if game has not started
   */
  @Override
  public int getScore() throws IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game not started");
    }
    int score = 0;

    for (Pile pile : foundationPiles) {
      if (!pile.isEmpty()) {
        BasicCard topCard = (BasicCard) pile.peekTop();
        score += getRankValue(topCard.getRank());

      }
    }
    return score;

  }

  /**
   * Returns the number of cards in the cascade pile which is given.
   *
   * @param pileNum the 0-based index (from the left) of the pile
   * @return # of cards in the pile
   * @throws IllegalArgumentException if pileNum is invalid
   * @throws IllegalStateException    if the game has not started
   */
  @Override
  public int getPileHeight(int pileNum) throws IllegalArgumentException, IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game not started");
    }
    if (pileNum < 0 || pileNum >= cascadePiles.size()) {
      throw new IllegalArgumentException("Pile number is not valid");
    }
    return cascadePiles.get(pileNum).size();
  }

  /**
   * Returns the card at the given position in the cascade pile which is specified.
   *
   * @param pileNum column of the desired card (0-indexed from the left)
   * @param card    row of the desired card (0-indexed from the top)
   * @return the requested card
   * @throws IllegalArgumentException if the card index or pileNum is invalid
   * @throws IllegalStateException    if the game did not start
   */
  @Override
  public BasicCard getCardAt(int pileNum, int card)
      throws IllegalArgumentException, IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game not started");
    }
    if (pileNum < 0 || pileNum >= cascadePiles.size()) {
      throw new IllegalArgumentException("Pile number is not valid");
    }
    Pile pile = cascadePiles.get(pileNum);

    if (card < 0 || card >= cascadePiles.get(pileNum).size()) {
      throw new IllegalArgumentException("Card number is not valid");
    }
    if (!pile.isCardFaceUp(card)) {
      throw new IllegalArgumentException("Card number cant be seen");
    }
    return (BasicCard) pile.getCardAt(card);
  }

  /**
   * Returns the card at the current position in the specific foundation pile.
   *
   * @param foundationPile foundaiton pile index
   * @return the requested card
   * @throws IllegalArgumentException if pileNum or the card index is invalid
   * @throws IllegalStateException    if the game has not yet started
   */

  @Override
  public BasicCard getCardAt(int foundationPile)
      throws IllegalArgumentException, IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game not started");
    }
    if (foundationPile < 0 || foundationPile >= foundationPiles.size()) {
      throw new IllegalArgumentException("Pile number is not valid");
    }
    Pile pile = foundationPiles.get(foundationPile);
    if (pile.size() == 0) {
      return null;
    }
    return (BasicCard) pile.peekTop();
  }

  /**
   * Returns if a card in the cascade pile is face up.
   *
   * @param pileNum the index of cascade pile
   * @param card    index of card within the pile
   * @return true if the card is visible (faceUp), return true if another situation
   * @throws IllegalArgumentException if pileNum or index of card is not valid
   * @throws IllegalStateException    if the game has not started
   */

  @Override
  public boolean isCardVisible(int pileNum, int card)
      throws IllegalArgumentException, IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game not started");
    }
    if (pileNum < 0 || pileNum >= cascadePiles.size()) {
      throw new IllegalArgumentException("Pile number is not valid");
    }
    Pile pile = cascadePiles.get(pileNum);
    if (card < 0 || card >= cascadePiles.get(pileNum).size()) {
      throw new IllegalArgumentException("Card number is not valid");
    }
    return pile.isCardFaceUp(card);
  }

  /**
   * Returns the list of visible cards in the draw pile.
   *
   * @return a list of visible cards that were drawn
   * @throws IllegalStateException if the game has did not start
   */

  @Override
  public List<BasicCard> getDrawCards() throws IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game not started");
    }
    int numVisible = Math.min(numDrawCards, drawPiles.size());
    return new ArrayList<>(drawPiles.subList(0, numVisible));
  }

  /**
   * Returns the # of foundation piles in the game.
   *
   * @return # of foundaiton piles
   * @throws IllegalStateException if the game did not start
   */

  @Override
  public int getNumFoundations() throws IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("Game not started");
    }
    return foundationPiles.size();
  }

  /**
   *Returns a text representation of the game state at current moment.
   *
   * @return string representation of the game board, or is
   *              an empty string if the game did not start
   */

  @Override
  public String toString() {
    if (!gameStarted) {
      return "";
    }
    StringBuilder sb = new StringBuilder();

    sb.append("Draw:");
    if (drawPiles.isEmpty()) {
      sb.append(" <empty>");
    } else {
      List<BasicCard> visibleDrawCards = drawPiles.subList(
          0, Math.min(numDrawCards, drawPiles.size()));
      for (int i = 0; i < visibleDrawCards.size(); i++) {
        if (i > 0) {
          sb.append(",");
        }
        sb.append(" ").append(visibleDrawCards.get(i));
      }
    }
    sb.append("\n");

    sb.append("Foundation:");
    for (int i = 0; i < foundationPiles.size(); i++) {
      if (i > 0) {
        sb.append(",");
      }
      Pile f = foundationPiles.get(i);
      sb.append(" ").append(f.isEmpty() ? "<empty>" : f.peekTop());
    }
    sb.append("\n");

    int maxHeight = cascadePiles.stream().mapToInt(Pile::size).max().orElse(0);
    for (int row = 0; row < maxHeight; row++) {
      for (int i = 0; i < cascadePiles.size(); i++) {
        Pile p = cascadePiles.get(i);
        String cardStr;
        if (row < p.size()) {
          cardStr = p.isCardFaceUp(row) ? p.getCardAt(row).toString() : "?";
        } else {
          cardStr = "";
        }
        sb.append(String.format("%-3s", cardStr));
        if (i < cascadePiles.size() - 1) {
          sb.append(" ");
        }
      }
      sb.append("\n");
    }

    return sb.toString();
  }

  /**
   * Main method to show BasicKlondike game.
   *
   * @param args command line arguments.
   */

  public static void main(String[] args) {
    BasicKlondike game = new BasicKlondike();
    List<BasicCard> deck = game.createNewDeck();
    game.startGame(deck, false, 7, 3); // 7 cascade piles, 3 draw cards
    System.out.println(game.toString()); // visually check if board looks correct
  }

}



