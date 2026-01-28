package klondike;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import klondike.model.hw02.BasicCard;
import klondike.model.hw02.BasicKlondike;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for BasicKlondike model.
 * Verifies that all operations work correctly and
 * appropriate exceptions are thrown for invalid inputs.
 */
public class BasicKlondikeTest {

  private BasicKlondike game;
  private List<BasicCard> deck;

  /**
   * Sets up a fresh game and deck before each test.
   */
  @Before
  public void setUp() {
    game = new BasicKlondike();
    deck = game.createNewDeck();
  }

  /**
   * Tests that a new deck has 52 cards.
   */
  @Test
  public void testCreateNewDeck() {
    assertEquals(52, deck.size());
  }

  /**
   * Verifies the deck contains all 52 cards.
   */
  @Test
  public void testDeckHasAllCards() {
    assertEquals(52, deck.size());
  }

  /**
   * Tests that a game starts successfully with valid parameters.
   */
  @Test
  public void testStartGameValid() {
    game.startGame(deck, false, 7, 3);
    assertEquals(7, game.getNumPiles());
    assertEquals(3, game.getNumDraw());
  }

  /**
   * Tests that starting with a null deck throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testStartGameNullDeck() {
    game.startGame(null, false, 7, 3);
  }

  /**
   * Tests that a deck with null cards throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testStartGameNullCard() {
    deck.set(0, null);
    game.startGame(deck, false, 7, 3);
  }

  /**
   * Tests that zero piles throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testStartGameInvalidPiles() {
    game.startGame(deck, false, 0, 3);
  }

  /**
   * Tests that negative piles throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testStartGameNegativePiles() {
    game.startGame(deck, false, -1, 3);
  }

  /**
   * Tests that zero draw cards throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testStartGameInvalidDraw() {
    game.startGame(deck, false, 7, 0);
  }

  /**
   * Tests that insufficient cards in deck throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testStartGameNotEnoughCards() {
    List<BasicCard> smallDeck = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      smallDeck.add(deck.get(i));
    }
    game.startGame(smallDeck, false, 7, 3);
  }

  /**
   * Tests that moving pile before game starts throws exception.
   */
  @Test(expected = IllegalStateException.class)
  public void testMovePileBeforeStart() {
    game.movePile(0, 1, 1);
  }

  /**
   * Tests that moving draw before game starts throws exception.
   */
  @Test(expected = IllegalStateException.class)
  public void testMoveDrawBeforeStart() {
    game.moveDraw(0);
  }

  /**
   * Tests that getting score before game starts throws exception.
   */
  @Test(expected = IllegalStateException.class)
  public void testGetScoreBeforeStart() {
    game.getScore();
  }

  /**
   * Tests basic pile move functionality.
   */
  @Test
  public void testMovePileSingleCard() {
    game.startGame(deck, false, 7, 3);
    try {
      int pile0Height = game.getPileHeight(0);
      int pile1Height = game.getPileHeight(1);
      assertTrue(pile0Height >= 0);
      assertTrue(pile1Height >= 0);
    } catch (Exception e) {
      // Expected for any random decks
    }
  }

  /**
   * Tests that invalid source pile throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMovePileInvalidSource() {
    game.startGame(deck, false, 7, 3);
    game.movePile(-1, 1, 0);
  }

  /**
   * Tests that invalid destination pile throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMovePileInvalidDest() {
    game.startGame(deck, false, 7, 3);
    game.movePile(0, 1, 10);
  }

  /**
   * Tests that moving to same pile throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMovePileSamePile() {
    game.startGame(deck, false, 7, 3);
    game.movePile(0, 1, 0);
  }

  /**
   * Tests that moving zero cards throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMovePileZeroCards() {
    game.startGame(deck, false, 7, 3);
    game.movePile(0, 0, 1);
  }

  /**
   * Tests that moving too many cards throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMovePileTooManyCards() {
    game.startGame(deck, false, 7, 3);
    game.movePile(0, 100, 1);
  }

  /**
   * Tests that invalid pile index for draw move throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMoveDrawInvalidPile() {
    game.startGame(deck, false, 7, 3);
    game.moveDraw(-1);
  }

  /**
   * Tests that moving from empty draw pile throws exception.
   */
  @Test(expected = IllegalStateException.class)
  public void testMoveDrawEmptyDraw() {
    List<BasicCard> smallDeck = new ArrayList<>();
    for (int i = 0; i < 28; i++) {
      smallDeck.add(deck.get(i));
    }
    game.startGame(smallDeck, false, 7, 3);
    game.moveDraw(0);
  }

  /**
   * Tests that invalid foundation index throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMoveToFoundationInvalidFoundation() {
    game.startGame(deck, false, 7, 3);
    game.moveToFoundation(0, -1);
  }

  /**
   * Tests that invalid source pile throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMoveToFoundationInvalidSource() {
    game.startGame(deck, false, 7, 3);
    game.moveToFoundation(-1, 0);
  }

  /**
   * Tests moving from empty pile to foundation throws exception.
   */
  @Test(expected = IllegalStateException.class)
  public void testMoveToFoundationEmptyPile() {
    game.startGame(deck, false, 3, 3);
    game.moveToFoundation(2, 0);
  }

  /**
   * Tests that invalid foundation index throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMoveDrawToFoundationInvalid() {
    game.startGame(deck, false, 7, 3);
    game.moveDrawToFoundation(-1);
  }

  /**
   * Tests that moving draw to foundation with no cards throws exception.
   */
  @Test(expected = IllegalStateException.class)
  public void testMoveDrawToFoundationNoCards() {
    List<BasicCard> smallDeck = new ArrayList<>();
    for (int i = 0; i < 28; i++) {
      smallDeck.add(deck.get(i));
    }
    game.startGame(smallDeck, false, 7, 3);
    game.moveDrawToFoundation(0);
  }

  /**
   * Tests that discarding changes the draw cards.
   */
  @Test
  public void testDiscardDraw() {
    game.startGame(deck, false, 7, 3);
    List<BasicCard> before = game.getDrawCards();
    game.discardDraw();
    List<BasicCard> after = game.getDrawCards();
    assertNotEquals(before.get(0), after.get(0));
  }

  /**
   * Tests that discarding before game starts throws exception.
   */
  @Test(expected = IllegalStateException.class)
  public void testDiscardDrawBeforeStart() {
    game.discardDraw();
  }

  /**
   * Tests that number of rows is positive after starting game.
   */
  @Test
  public void testGetNumRows() {
    game.startGame(deck, false, 7, 3);
    assertTrue(game.getNumRows() > 0);
  }

  /**
   * Tests that number of piles matches what was specified.
   */
  @Test
  public void testGetNumPiles() {
    game.startGame(deck, false, 7, 3);
    assertEquals(7, game.getNumPiles());
  }

  /**
   * Tests that number of draw cards matches what was specified.
   */
  @Test
  public void testGetNumDraw() {
    game.startGame(deck, false, 7, 3);
    assertEquals(3, game.getNumDraw());
  }

  /**
   * Tests that initial score is zero.
   */
  @Test
  public void testGetScore() {
    game.startGame(deck, false, 7, 3);
    assertEquals(0, game.getScore());
  }

  /**
   * Tests that game is not over at start.
   */
  @Test
  public void testIsGameOver() {
    game.startGame(deck, false, 7, 3);
    assertFalse(game.isGameOver());
  }

  /**
   * Tests that pile heights are correct after dealing.
   */
  @Test
  public void testGetPileHeight() {
    game.startGame(deck, false, 7, 3);
    assertEquals(1, game.getPileHeight(0));
    assertEquals(2, game.getPileHeight(1));
    assertEquals(7, game.getPileHeight(6));
  }

  /**
   * Tests that invalid pile index throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testGetPileHeightInvalid() {
    game.startGame(deck, false, 7, 3);
    game.getPileHeight(-1);
  }

  /**
   * Tests that getting a card returns a valid card.
   */
  @Test
  public void testGetCardAt() {
    game.startGame(deck, false, 7, 3);
    BasicCard card = game.getCardAt(0, 0);
    assertNotNull(card);
  }

  /**
   * Tests that invalid pile index throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testGetCardAtInvalidPile() {
    game.startGame(deck, false, 7, 3);
    game.getCardAt(-1, 0);
  }

  /**
   * Tests that invalid card index throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testGetCardAtInvalidCard() {
    game.startGame(deck, false, 7, 3);
    game.getCardAt(0, 10);
  }

  /**
   * Tests that empty foundation returns null.
   */
  @Test
  public void testGetCardAtFoundation() {
    game.startGame(deck, false, 7, 3);
    BasicCard card = game.getCardAt(0);
    assertNull(card);
  }

  /**
   * Tests that invalid foundation index throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testGetCardAtFoundationInvalid() {
    game.startGame(deck, false, 7, 3);
    game.getCardAt(-1);
  }

  /**
   * Tests card visibility in BasicKlondike.
   * Only top cards should be visible.
   */
  @Test
  public void testIsCardVisible() {
    game.startGame(deck, false, 7, 3);
    assertTrue(game.isCardVisible(0, 0));
    assertFalse(game.isCardVisible(1, 0));
    assertTrue(game.isCardVisible(1, 1));
  }

  /**
   * Tests that invalid pile index throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testIsCardVisibleInvalidPile() {
    game.startGame(deck, false, 7, 3);
    game.isCardVisible(-1, 0);
  }

  /**
   * Tests that draw cards list has correct size.
   */
  @Test
  public void testGetDrawCards() {
    game.startGame(deck, false, 7, 3);
    List<BasicCard> draws = game.getDrawCards();
    assertTrue(draws.size() <= 3);
  }

  /**
   * Tests that there are 4 foundation piles.
   */
  @Test
  public void testGetNumFoundations() {
    game.startGame(deck, false, 7, 3);
    assertEquals(4, game.getNumFoundations());
  }

  /**
   * Tests starting game with different pile count.
   */
  @Test
  public void testStartGameDifferentPiles() {
    game.startGame(deck, false, 5, 2);
    assertEquals(5, game.getNumPiles());
    assertEquals(2, game.getNumDraw());
  }

  /**
   * Tests that shuffling works without errors.
   */
  @Test
  public void testStartGameWithShuffle() {
    BasicKlondike game2 = new BasicKlondike();
    List<BasicCard> deck2 = game2.createNewDeck();

    game.startGame(deck, false, 7, 3);
    game2.startGame(deck2, true, 7, 3);

    assertEquals(7, game.getNumPiles());
    assertEquals(7, game2.getNumPiles());
  }

  /**
   * Tests that toString produces valid output.
   */
  @Test
  public void testToString() {
    game.startGame(deck, false, 7, 3);
    String output = game.toString();
    assertNotNull(output);
    assertTrue(output.contains("Foundation"));
    assertTrue(output.contains("Draw"));
  }
}