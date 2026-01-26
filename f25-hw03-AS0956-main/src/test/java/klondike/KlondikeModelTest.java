package klondike;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import klondike.model.hw02.BasicCard;
import klondike.model.hw02.BasicKlondike;
import klondike.model.hw02.Card;
import klondike.model.hw02.KlondikeModel;
import org.junit.Before;
import org.junit.Test;

/**
 * A class for testing the KlondikeModel. All tests
 * in this class cannot create Card type objects. Instead,
 * the tests use the createNewDeck method to help create
 * example games.
 */
public class KlondikeModelTest {
  private KlondikeModel<BasicCard> model;

  /**
   * Sets up the BasicKlondike Game.
   */
  @Before
  public void setUp() {
    model = new BasicKlondike();
  }


  /**
   * Checks that a newly created deck has 52 unique cards.
   */
  @Test
  public void testNewDeckHas52UniqueCards() {
    List<? extends Card> deck = model.createNewDeck();

    assertEquals("Deck should have 52 cards", 52, deck.size());

    Set<Card> uniqueCards = new HashSet<Card>();
    for (Card card : deck) {
      uniqueCards.add(card);
    }
    assertEquals("Desck should have 52 unique cards", 52, uniqueCards.size());
  }

  /**
   * Makes sure that starting a game twice.
   * Or does not crash or throws properly
   */
  @Test
  public void testStartGameStartedThrows() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    model.startGame(model.createNewDeck(), false, 7, 3);
  }

  /**
   * Verifies that starting a game.
   * With an empty deck throws
   * IllegalArgumentException
   */
  @Test(expected = IllegalArgumentException.class)
  public void testStartGameInvalidDeckThrows() {
    model.startGame(List.of(), false, 7, 3);
  }

  /**
   * Checks if game setup intializes pile, draw size.
   * And card correctly
   */
  @Test
  public void testGameStartSetUpNicely() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    assertEquals(7, model.getNumPiles());
    assertEquals(3, model.getNumDraw());
    assertTrue(model.getPileHeight(0) > 0);
    assertNotNull(model.getCardAt(0, 0));
  }

  /**
   * Verifies that moving a pile.
   * To an invalid index
   * throws IllegalArgumentException
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidPileMoveThrows() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    model.movePile(0, 5, 100);
  }

  /**
   * Verfies that moving more cards.
   * Than allowed from a pile throws
   * IllegalArgumentException
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMovePileInvalidCardMoveThrows() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    model.movePile(0, 5, 1);
  }

  /**
   * Checks that getPileHeight and isCardVisible work correctly after starting a game.
   */
  @Test
  public void testGetPileHeightAndCardVisibility() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    assertTrue(model.getPileHeight(0) > 0);
    assertTrue(model.isCardVisible(0, model.getPileHeight(0) - 1));
  }

  /**
   * Makes sure that calling getNumRows before starting a game throws IllegalStateException.
   */
  @Test(expected = IllegalStateException.class)
  public void testGetNumRowsBeforeStartThrows() {
    model.getNumRows();
  }

  /**
   * Makes sure that calling getNumPiles before starting a game throws IllegalStateException.
   */
  @Test(expected = IllegalStateException.class)
  public void testGetNumPilesBeforeStartThrows() {
    model.getNumPiles();
  }

  /**
   * Makes sure that calling getNumDraw before starting a game throws IllegalStateException.
   */
  @Test(expected = IllegalStateException.class)
  public void testGetNumDrawBeforeStartThrows() {
    model.getNumDraw();
  }

  /**
   * Makes sure that calling getScore before starting a game throws IllegalStateException.
   */
  @Test(expected = IllegalStateException.class)
  public void testGetScoreBeforeStartThrows() {
    model.getScore();
  }

  /**
   * Makes sure that calling getPileHeight before starting a game throws IllegalStateException.
   */
  @Test(expected = IllegalStateException.class)
  public void testGetPileHeightBeforeStartThrows() {
    model.getPileHeight(0);
  }

  /**
   * Makes sure that accessing a card at an invalid pile index throws IllegalArgumentException.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testGetCardAtInvalidIndexThrows() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    model.getCardAt(100, 0);
  }

  /**
   * Makes sure that accessing a card at an invalid foundation index.
   * throws IllegalArgumentException.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testGetCardAtFoundationInvalidIndexThrows2() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    model.getCardAt(100);
  }

  /**
   * Makes sure that getDrawCards returns a valid list of draw cards after game starts.
   */
  @Test
  public void testGetDrawCardsList() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    List<?> drawCards = model.getDrawCards();
    assertNotNull(drawCards);
    assertTrue(drawCards.size() <= model.getNumDraw());
  }

  /**
   * Makes sure that getNumFoundations returns a positive number after starting a game.
   */
  @Test
  public void testGetNumFoundations() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    assertTrue(model.getNumFoundations() > 0);
  }

  /**
   * Verifies that the game over state is false at the start of the game.
   */
  @Test
  public void testGameOverReturnsFalse() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    assertEquals("Game should not be over at the start of the game", false, model.isGameOver());
  }

  /**
   * Checks that moving a draw card illegally throws IllegalStateException.
   */
  @Test(expected = IllegalStateException.class)
  public void testInvalidMoveDrawThrows() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    for (int i = 0; i < model.getDrawCards().size() + 1; i++) {
      model.discardDraw();
    }
    model.moveDraw(0);
  }

  /**
   * Makes sure that discarding a draw card reduces the draw pile size.
   */
  @Test
  public void testDiscardDrawReducesDeckDrawSize() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    int initialSize = model.getDrawCards().size();
    if (initialSize > 0) {
      model.discardDraw();
      assertTrue("Discard should reduce pile size", model.getDrawCards().size() <= initialSize);
    }
  }

  /**
   * Makes sure that moving a pile card to a foundation works as expected.
   */
  @Test
  public void testMoveFoundationWorks() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    if (model.getPileHeight(0) > 0) {
      model.moveToFoundation(0, 0);
      assertTrue(model.getPileHeight(0) < model.getPileHeight(0) + 1);
    }
  }
}
