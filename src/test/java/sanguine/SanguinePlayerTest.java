package sanguine;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;
import sanguine.model.Card;
import sanguine.model.Influence;
import sanguine.model.Player;
import sanguine.model.SanguineCard;
import sanguine.model.SanguinePlayer;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the SanguinePlayer class.
 */
public class SanguinePlayerTest {
  SanguinePlayer p1;
  List<Card> deck;
  Card c1;
  Card c2;
  Card c3;
  Influence[][] grid;

  @Before
  public void setUp() {
    grid = new Influence[5][5];
    for (int row = 0; row < 5; row++) {
      for (int col = 0; col < 5; col++) {
        grid[row][col] = Influence.INFLUENCE;
      }
    }
    c1 = new SanguineCard("Card 1", 1, 1, grid);
    c2 = new SanguineCard("Card 2", 1, 1, grid);
    c3 = new SanguineCard("Card 3", 1, 1, grid);
    deck = new ArrayList<>();
    deck.add(c1);
    deck.add(c2);
    deck.add(c3);
  }

  @Test
  public void testValidPlayerConstruction() {
    p1 = new SanguinePlayer(deck, 1, 1);
    p1.dealHand(1);
    assertEquals(List.of(c2, c3), p1.getDeck());
    assertEquals(List.of(c1), p1.getHand());
    assertArrayEquals(new int[1], p1.getScores());
  }

  @Test
  public void testInvalidPlayerConstructionWithNullAndEmptyDeck() {
    List<Card> mockDeck = new ArrayList<>();
    assertThrows(IllegalArgumentException.class,
        () -> new SanguinePlayer(null, 1, 1));
    assertThrows(IllegalArgumentException.class,
        () -> new SanguinePlayer(mockDeck, 1, 1));
  }

  @Test
  public void testInvalidPlayerConstructionWithInvalidNumHand() {
    assertThrows(IllegalArgumentException.class,
        () -> new SanguinePlayer(deck, -1, 1));
    assertThrows(IllegalArgumentException.class,
        () -> new SanguinePlayer(deck, 0, 1));
    assertThrows(IllegalArgumentException.class,
        () -> new SanguinePlayer(deck, 2, 1));
  }

  @Test
  public void testInvalidPlayerConstructionWithInvalidNumRows() {
    assertThrows(IllegalArgumentException.class,
        () -> new SanguinePlayer(deck, 1, -1));
    assertThrows(IllegalArgumentException.class,
        () -> new SanguinePlayer(deck, 1, 0));
  }

  @Test
  public void testValidSecondConstructor() {
    p1 = new SanguinePlayer(deck, 1, 1);
    Player player = new SanguinePlayer(p1);
    assertEquals(new ArrayList<>(deck), player.getDeck());
    assertEquals(List.of(), player.getHand());
    assertArrayEquals(new int[] {0}, player.getScores());
  }

  @Test
  public void testInvalidSecondConstructorWithNullSanguinePlayer() {
    assertThrows(IllegalArgumentException.class, () -> new SanguinePlayer(null));
  }

  @Test
  public void testDrawCard() {
    p1 = new SanguinePlayer(deck, 1, 1);
    p1.dealHand(1);
    p1.useCard(0, 0);
    p1.drawCard();
    assertEquals(List.of(c2), p1.getHand());
  }

  @Test
  public void testDrawCardWithFullHand() {
    p1 = new SanguinePlayer(deck, 1, 1);
    p1.dealHand(1);
    assertThrows(IllegalStateException.class, () -> p1.drawCard());
  }

  @Test
  public void testDrawCardWithEmptyDeck() {
    p1 = new SanguinePlayer(deck, 1, 1);
    p1.dealHand(1);
    p1.useCard(0, 0);
    p1.drawCard();
    p1.useCard(0, 0);
    p1.drawCard();
    p1.useCard(0, 0);
    p1.drawCard();
    assertEquals(List.of(), p1.getHand());
  }

  @Test
  public void testUseCard() {
    p1 = new SanguinePlayer(deck, 1, 1);
    p1.dealHand(1);
    p1.useCard(0, 0);
    assertEquals(List.of(), p1.getHand());
    assertArrayEquals(new int[] {1}, p1.getScores());
  }

  @Test
  public void testUseCardWithInvalidHandIndex() {
    p1 = new SanguinePlayer(deck, 1, 1);
    assertThrows(IllegalArgumentException.class, () -> p1.useCard(0, -1));
    assertThrows(IllegalArgumentException.class, () -> p1.useCard(0, 1));
  }
}