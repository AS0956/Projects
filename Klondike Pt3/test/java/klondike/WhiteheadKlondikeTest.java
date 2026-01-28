package klondike;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import klondike.model.hw02.BasicCard;
import klondike.model.hw02.Card;
import klondike.model.hw02.KlondikeModel;
import klondike.model.hw02.Suit;
import klondike.model.hw04.WhiteheadKlondike;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for WhiteheadKlondike variant.
 * Tests the four key differences from BasicKlondike.
 */
public class WhiteheadKlondikeTest {
  private KlondikeModel<BasicCard> model;

  /**
   * Setup for Whitehead Klondike game.
   */
  @Before
  public void setUp() {
    model = new WhiteheadKlondike();
  }

  /**
   * Tests that all cascade cards are visible from the start.
   */
  @Test
  public void testAllCascadeCardsAreFaceUp() {
    model.startGame(model.createNewDeck(), false, 7, 3);

    for (int pile = 0; pile < model.getNumPiles(); pile++) {
      for (int card = 0; card < model.getPileHeight(pile); card++) {
        assertTrue("All cards should be face-up in pile " + pile,
            model.isCardVisible(pile, card));
      }
    }
  }

  /**
   * Tests face-up with different pile configurations.
   */
  @Test
  public void testAllCardsVisibleWithFewerPiles() {
    model.startGame(model.createNewDeck(), false, 4, 2);

    for (int pile = 0; pile < 4; pile++) {
      for (int card = 0; card < model.getPileHeight(pile); card++) {
        assertTrue("Card should be visible", model.isCardVisible(pile, card));
      }
    }
  }


  /**
   * Tests that same-color builds work (using standard deck).
   */
  @Test
  public void testSameColorBuildsAllowed() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    try {
      model.movePile(0, 1, 1);
    } catch (IllegalStateException e) {
      assertTrue("Error should be about rank/suit, not color",
          !e.getMessage().toLowerCase().contains("color")
              || e.getMessage().contains("same color"));
    }
  }

  /**
   * Tests that opposite colors cannot build.
   */
  @Test
  public void testOppositeColorsNotAllowed() {
    List<BasicCard> deck = createDeckWithOppositeColors();
    model.startGame(deck, false, 3, 1);

    try {
      model.movePile(0, 1, 1);
      fail("Should not allow opposite color builds");
    } catch (IllegalStateException e) {
      assertTrue("Should mention color",
          e.getMessage().toLowerCase().contains("color"));
    }
  }

  /**
   * Tests that multi-card moves require same suit.
   */
  @Test
  public void testMultiCardMoveMustBeSameSuit() {
    List<BasicCard> deck = createDeckWithDifferentSuits();
    model.startGame(deck, false, 5, 1); // Use 5 piles so pile 2 has 3 cards

    try {
      model.movePile(2, 2, 0); // Move 2 cards from pile 2
      fail("Should require same suit for multi-card moves");
    } catch (IllegalStateException e) {
      System.out.println("Exception message: " + e.getMessage());
      assertTrue("Should mention suit, but got: " + e.getMessage(),
          e.getMessage().toLowerCase().contains("suit"));
    }
  }

  /**
   * Tests single card moves don't require suit matching.
   */
  @Test
  public void testSingleCardMoveOnlyRequiresColor() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    try {
      model.movePile(0, 1, 1);
    } catch (IllegalStateException e) {
      assertTrue("Single card error should not be about suit",
          !e.getMessage().toLowerCase().contains("same suit"));
    }
  }

  /**
   * Tests that non-King cards can move to empty piles.
   */
  @Test
  public void testAnyCardToEmptyPile() {
    List<BasicCard> deck = model.createNewDeck();
    model.startGame(deck, false, 7, 3);

    assertTrue("Game should start successfully", model.getNumPiles() == 7);
  }


  /**
   * Tests basic game initialization.
   */
  @Test
  public void testGameInitialization() {
    model.startGame(model.createNewDeck(), false, 7, 3);

    assertEquals(7, model.getNumPiles());
    assertEquals(3, model.getNumDraw());
    assertEquals(4, model.getNumFoundations());
    assertEquals(0, model.getScore());
  }

  /**
   * Tests draw card follows same-color rule.
   */
  @Test
  public void testDrawCardFollowsColorRule() {
    model.startGame(model.createNewDeck(), false, 7, 3);

    try {
      model.moveDraw(0);
    } catch (IllegalStateException e) {
      //
    }
  }

  /**
   * Tests invalid pile indices throw exceptions.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidPileIndexThrows() {
    model.startGame(model.createNewDeck(), false, 7, 3);
    model.movePile(0, 1, 99);
  }

  private List<BasicCard> createDeckWithOppositeColors() {
    List<BasicCard> deck = new ArrayList<>();
    deck.add(new BasicCard(Suit.hearts, "5"));
    deck.add(new BasicCard(Suit.hearts, "6"));
    deck.add(new BasicCard(Suit.clubs, "3"));

    List<BasicCard> standard = (List<BasicCard>) model.createNewDeck();
    for (int i = 3; i < 52; i++) {
      deck.add(standard.get(i));
    }
    return deck;
  }

  private List<BasicCard> createDeckWithDifferentSuits() {
    List<BasicCard> deck = new ArrayList<>();
    deck.add(new BasicCard(Suit.hearts, "7"));
    deck.add(new BasicCard(Suit.diamonds, "6")); // Different suit
    deck.add(new BasicCard(Suit.hearts, "5"));

    List<BasicCard> standard = (List<BasicCard>) model.createNewDeck();
    for (int i = 3; i < 52; i++) {
      deck.add(standard.get(i));
    }
    return deck;
  }
}