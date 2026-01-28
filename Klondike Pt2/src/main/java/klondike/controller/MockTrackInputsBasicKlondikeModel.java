package klondike.controller;

import java.util.ArrayList;
import java.util.List;
import klondike.model.hw02.Card;
import klondike.model.hw02.KlondikeModel;

/**
 * jh.
 *
 * @param <C> hghg
 */

public class MockTrackInputsBasicKlondikeModel<C extends Card> implements KlondikeModel<C> {

  public final List<String> log = new ArrayList<>();

  @Override
  public List<C> createNewDeck() {
    return List.of();
  }

  @Override
  public void startGame(List<C> deck, boolean shuffle, int numPiles, int numDraw) {
    log.add("Starting game...");
  }

  @Override
  public void movePile(int srcPile, int numCards, int destPile) {
    log.add("Moving pile from " + srcPile + " to " + destPile);
  }

  @Override
  public void moveDraw(int destPile) {
    log.add("Moving pile to " + destPile);
  }

  @Override
  public void moveToFoundation(int srcPile, int foundationPile)
      throws IllegalArgumentException, IllegalStateException {

  }

  @Override
  public void moveDrawToFoundation(int foundationPile) {
    log.add("Moving to foundation pile " + foundationPile);
  }

  @Override
  public void discardDraw() throws IllegalStateException {
    log.add("discardDraw");

  }

  @Override
  public int getNumRows() throws IllegalStateException {
    return 0;
  }

  @Override
  public int getNumPiles() throws IllegalStateException {
    return 0;
  }

  @Override
  public int getNumDraw() throws IllegalStateException {
    return 0;
  }

  @Override
  public boolean isGameOver() throws IllegalStateException {
    return false;
  }

  @Override
  public int getScore() throws IllegalStateException {
    return 0;
  }

  @Override
  public int getPileHeight(int pileNum) throws IllegalArgumentException, IllegalStateException {
    return 0;
  }

  @Override
  public C getCardAt(int pileNum, int card) throws IllegalArgumentException, IllegalStateException {
    return null;
  }

  @Override
  public C getCardAt(int foundationPile) throws IllegalArgumentException, IllegalStateException {
    return null;
  }

  @Override
  public boolean isCardVisible(int pileNum, int card)
      throws IllegalArgumentException, IllegalStateException {
    return false;
  }

  @Override
  public List<C> getDrawCards() throws IllegalStateException {
    return new ArrayList<>();
  }

  @Override
  public int getNumFoundations() throws IllegalStateException {
    return 0;
  }
}
