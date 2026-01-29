package sanguine.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import sanguine.model.BasicSanguine;
import sanguine.model.Card;
import sanguine.model.Influence;
import sanguine.model.SanguineCard;
import sanguine.model.SanguineModel;
import sanguine.view.TextualView;
import sanguine.view.View;

/**
 * Tests the TextualView class.
 */
public class TextualViewTest {
  SanguineModel model = new BasicSanguine();
  View view = new TextualView();
  List<Card> redDeck;
  List<Card> blueDeck;
  Card card;

  /**
   * Sets up the influence grid.
   * Initializes the card and decks to start the game.
   */
  @Before
  public void setUp() {
    Influence[][] grid = new Influence[5][5];
    for (int row = 0; row < 5; row++) {
      for (int col = 0; col < 5; col++) {
        if ((row == 1 && col == 2) || (row == 2 && col == 1)
            || (row == 2 && col == 3) || (row == 3 && col == 2)) {
          grid[row][col] = Influence.INFLUENCE;
        } else if (row == 2 && col == 2) {
          grid[row][col] = Influence.CARD;
        } else {
          grid[row][col] = Influence.NONE;
        }
      }
    }
    card = new SanguineCard("Card", 1, 2, grid);
    redDeck = new ArrayList<>();
    blueDeck = new ArrayList<>();
    for (int index = 0; index < 3; index++) {
      redDeck.add(card);
      blueDeck.add(card);
    }
    model.startGame(redDeck, blueDeck, 1, 3, 1);
  }

  @Test
  public void testStartingGameState() {
    assertEquals("0 1_1 0", view.render(model));
  }

  @Test
  public void testNullGame() {
    assertThrows(IllegalArgumentException.class, () -> view.render(null));
  }

  @Test
  public void testRedWinning() {
    model.drawCard();
    model.placeCard(0, 0, 0);
    assertEquals("2 R11 0", view.render(model));
    model.drawCard();
    model.pass();
    model.drawCard();
    model.placeCard(0, 1, 0);
    model.drawCard();
    model.pass();
    assertEquals("4 RR1 0", view.render(model));
    model.drawCard();
    model.placeCard(0, 2, 0);
    assertEquals("6 RRR 0", view.render(model));
  }

  @Test
  public void testBlueWinning() {
    model.drawCard();
    model.pass();
    model.drawCard();
    model.placeCard(0, 2, 0);
    model.drawCard();
    model.pass();
    assertEquals("0 11B 2", view.render(model));
    model.drawCard();
    model.placeCard(0, 1, 0);
    model.drawCard();
    model.pass();
    assertEquals("0 1BB 4", view.render(model));
    model.drawCard();
    model.placeCard(0, 0, 0);
    assertEquals("0 BBB 6", view.render(model));
  }

  @Test
  public void testTie() {
    model.drawCard();
    model.placeCard(0, 0, 0);
    model.drawCard();
    model.placeCard(0, 2, 0);
    assertEquals("2 R1B 2", view.render(model));
    model.drawCard();
    model.pass();
    model.drawCard();
    model.pass();
    assertEquals("2 R1B 2", view.render(model));
  }
}
