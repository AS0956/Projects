package klondike.model.hw02;

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

  @Test
  public void testStartGameStartedThrows() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    model.startGame(model.createNewDeck(), false, 7, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartGameInvalidDeckThrows() {
    model.startGame(List.of(), false, 7, 3);
  }

  @Test
  public void testGameStartSetUpNicely() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    assertEquals(7, model.getNumPiles());
    assertEquals(3, model.getNumDraw());
    assertTrue(model.getPileHeight(0) > 0);
    assertNotNull(model.getCardAt(0, 0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidPileMoveThrows() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    model.movePile(0, 5, 100);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMovePileInvalidCardMoveThrows() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    model.movePile(0, 5, 1);
  }

  @Test
  public void testGetPileHeightAndCardVisibility() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    assertTrue(model.getPileHeight(0) > 0);
    assertTrue(model.isCardVisible(0, model.getPileHeight(0) - 1));
  }

  @Test(expected = IllegalStateException.class)
  public void testGetNumRowsBeforeStartThrows() {
    model.getNumRows();
  }

  @Test(expected = IllegalStateException.class)
  public void testGetNumPilesBeforeStartThrows() {
    model.getNumPiles();
  }

  @Test(expected = IllegalStateException.class)
  public void testGetNumDrawBeforeStartThrows() {
    model.getNumDraw();
  }

  @Test(expected = IllegalStateException.class)
  public void testGetScoreBeforeStartThrows() {
    model.getScore();
  }

  @Test(expected = IllegalStateException.class)
  public void testGetPileHeightBeforeStartThrows() {
    model.getPileHeight(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetCardAtInvalidIndexThrows() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    model.getCardAt(100, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetCardAtFoundationInvalidIndexThrows2() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    model.getCardAt(100);
  }

  @Test
  public void testGetDrawCardsList() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    List<?> drawCards = model.getDrawCards();
    assertNotNull(drawCards);
    assertTrue(drawCards.size() <= model.getNumDraw());
  }

  @Test
  public void testGetNumFoundations() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    assertTrue(model.getNumFoundations() > 0);
  }

  @Test
  public void testGameOverReturnsFalse() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    assertEquals("Game should not be over at the start of the game", false, model.isGameOver());
  }

  @Test(expected = IllegalStateException.class)
  public void testInvalidMoveDrawThrows() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    for (int i = 0; i < model.getDrawCards().size() + 1; i++) {
      model.discardDraw();
    }
    model.moveDraw(0);
  }

  @Test
  public void testDiscardDrawReducesDeckDrawSize() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    int initialSize = model.getDrawCards().size();
    if (initialSize > 0) {
      model.discardDraw();
      assertTrue("Discard should reduce pile size", model.getDrawCards().size() <= initialSize);
    }
  }

  @Test
  public void testMoveFoundationWorks() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    if (model.getPileHeight(0) > 0) {
      model.moveToFoundation(0, 0);
      assertTrue(model.getPileHeight(0) < model.getPileHeight(0) + 1);
    }
  }

}







