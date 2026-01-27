package klondike.controller;

import java.util.ArrayList;
import java.util.List;
import klondike.model.hw02.Card;
import klondike.model.hw02.KlondikeModel;

/**
 * Mock implementation of KlondikeModel.
 * Tracks method calls for testing purposes
 * and logs every action which
 * modifies the game state in a log list
 *
 * <p>This mock can be used to check that the controller
 * sends the right commands to the model</p>
 *
 * @param <C> the type of Card used in game.
 */

public class MockTrackInputsBasicKlondikeModel<C extends Card> implements KlondikeModel<C> {

  public final List<String> log = new ArrayList<>();

  /**
   * Returns an empty new deck for testing.
   *
   * @return an empty list of cards
   */
  @Override
  public List<C> createNewDeck() {
    return List.of();
  }

  /**
   * Logs starting the game.
   *
   * @param deck     the deck to use (ignored in this mock)
   * @param shuffle  whether to shuffle the deck (ignored)
   * @param numPiles number of cascade piles (ignored)
   * @param numDraw  number of draw cards (ignored)
   */
  @Override
  public void startGame(List<C> deck, boolean shuffle, int numPiles, int numDraw) {
    log.add("Starting game...");
  }

  /**
   * Logs moving cards from one pile to another.
   *
   * @param srcPile  source pile index (0-based)
   * @param numCards number of cards to move
   * @param destPile destination pile index (0-based)
   */
  @Override
  public void movePile(int srcPile, int numCards, int destPile) {
    log.add("Moving pile from " + srcPile + " to " + destPile);
  }

  /**
   * Logs moving a card from the draw pile to a cascade pile.
   *
   * @param destPile destination pile index (0-based)
   */
  @Override
  public void moveDraw(int destPile) {
    log.add("Moving pile to " + destPile);
  }

  /**
   * Empty implementation for moving a card from a cascade pile to a foundation.
   *
   * @param srcPile        source pile index (0-based)
   * @param foundationPile destination foundation index (0-based)
   * @throws IllegalArgumentException never thrown in this mock
   * @throws IllegalStateException    never thrown in this mock
   */
  @Override
  public void moveToFoundation(int srcPile, int foundationPile)
      throws IllegalArgumentException, IllegalStateException {
  }

  /**
   * Logs moving a card from the draw pile to a foundation.
   *
   * @param foundationPile destination foundation index (0-based)
   */
  @Override
  public void moveDrawToFoundation(int foundationPile) {
    log.add("Moving to foundation pile " + foundationPile);
  }

  /**
   * Logs trashing a card from the draw pile.
   *
   * @throws IllegalStateException never thrown in this mock
   */
  @Override
  public void discardDraw() throws IllegalStateException {
    log.add("discardDraw");
  }

  /**
   * Returns 0 rows for testing purposes.
   *
   * @return 0
   * @throws IllegalStateException never thrown in this mock
   */
  @Override
  public int getNumRows() throws IllegalStateException {
    return 0;
  }

  /**
   * Returns 0 cascade piles for testing purposes.
   *
   * @return 0
   * @throws IllegalStateException never thrown in this mock
   */
  @Override
  public int getNumPiles() throws IllegalStateException {
    return 0;
  }

  /**
   * Returns 0 draw cards for testing purposes.
   *
   * @return 0
   * @throws IllegalStateException never thrown in this mock
   */
  @Override
  public int getNumDraw() throws IllegalStateException {
    return 0;
  }

  /**
   * Always returns false for game over status.
   *
   * @return false
   * @throws IllegalStateException never thrown in this mock
   */
  @Override
  public boolean isGameOver() throws IllegalStateException {
    return false;
  }

  /**
   * Always returns a score of 0.
   *
   * @return 0
   * @throws IllegalStateException never thrown in this mock
   */
  @Override
  public int getScore() throws IllegalStateException {
    return 0;
  }

  /**
   * Returns 0 for the height of any pile.
   *
   * @param pileNum pile index (0-based)
   * @return 0
   * @throws IllegalArgumentException never thrown in this mock
   * @throws IllegalStateException    never thrown in this mock
   */
  @Override
  public int getPileHeight(int pileNum) throws IllegalArgumentException, IllegalStateException {
    return 0;
  }

  /**
   * Returns null for any card in a pile.
   *
   * @param pileNum pile index (0-based)
   * @param card    card index (0-based)
   * @return null
   * @throws IllegalArgumentException never thrown in this mock
   * @throws IllegalStateException    never thrown in this mock
   */
  @Override
  public C getCardAt(int pileNum, int card) throws IllegalArgumentException, IllegalStateException {
    return null;
  }

  /**
   * Returns null for any card in a foundation.
   *
   * @param foundationPile foundation index (0-based)
   * @return null
   * @throws IllegalArgumentException never thrown in this mock
   * @throws IllegalStateException    never thrown in this mock
   */
  @Override
  public C getCardAt(int foundationPile) throws IllegalArgumentException, IllegalStateException {
    return null;
  }

  /**
   * Returns false for visibility of any card.
   *
   * @param pileNum pile index (0-based)
   * @param card    card index (0-based)
   * @return false
   * @throws IllegalArgumentException never thrown in this mock
   * @throws IllegalStateException    never thrown in this mock
   */
  @Override
  public boolean isCardVisible(int pileNum, int card)
      throws IllegalArgumentException, IllegalStateException {
    return false;
  }

  /**
   * Returns an empty list of draw cards.
   *
   * @return empty list
   * @throws IllegalStateException never thrown in this mock
   */
  @Override
  public List<C> getDrawCards() throws IllegalStateException {
    return new ArrayList<>();
  }

  /**
   * Returns 0 foundations for testing only.
   *
   * @return 0
   * @throws IllegalStateException never thrown in this mock
   */
  @Override
  public int getNumFoundations() throws IllegalStateException {
    return 0;
  }
}
