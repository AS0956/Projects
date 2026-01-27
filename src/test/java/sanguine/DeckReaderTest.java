package sanguine;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.List;
import org.junit.Test;
import sanguine.controller.DeckReader;
import sanguine.model.Card;
import sanguine.model.Influence;

/**
 * Tests the DeckReader class.
 */
public class DeckReaderTest {
  @Test
  public void testDeckSizeIsCorrect() {
    assertEquals(15, DeckReader.readDeck("docs/deck1.deck").size());
  }

  @Test
  public void testCardNameCostValueAreReadProperly() {
    List<Card> deck = DeckReader.readDeck("docs/deck1.deck");
    assertEquals("Security", deck.getFirst().getName());
    assertEquals(1, deck.getFirst().getCost());
    assertEquals(1, deck.getFirst().getValue());
    assertEquals("Flame", deck.get(6).getName());
    assertEquals(1, deck.get(6).getCost());
    assertEquals(3, deck.get(6).getValue());
  }

  @Test
  public void testCardGridIsReadProperly() {
    List<Card> deck = DeckReader.readDeck("docs/deck1.deck");
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
    assertArrayEquals(grid, deck.getFirst().getGrid());
  }

  @Test
  public void testInvalidPath() {
    assertThrows(IllegalArgumentException.class,
        () -> DeckReader.readDeck("docs/deck.deck"));
  }

  @Test
  public void testInvalidCardFormat() {
    assertThrows(IllegalArgumentException.class,
        () -> DeckReader.readDeck("docs/invalidDeckValue.deck"));
  }

  @Test
  public void testInvalidNumRows() {
    assertThrows(IllegalArgumentException.class,
        () -> DeckReader.readDeck("docs/invalidDeckRow.deck"));
  }

  @Test
  public void testInvalidNumColumns() {
    assertThrows(IllegalArgumentException.class,
        () -> DeckReader.readDeck("docs/invalidDeckColumn.deck"));
  }

  @Test
  public void testInvalidCharacter() {
    assertThrows(IllegalArgumentException.class,
        () -> DeckReader.readDeck("docs/invalidDeckCharacter.deck"));
  }
}
