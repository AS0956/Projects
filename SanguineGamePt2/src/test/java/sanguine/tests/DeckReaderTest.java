package sanguine.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import sanguine.controller.DeckReader;
import sanguine.model.Card;
import sanguine.model.Influence;
import sanguine.model.SanguineCard;

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

  /**
   * Tests the SanguineCard class.
   */
  public static class SanguineCardTest {
    Card c1;
    Card c2;
    Influence[][] grid;

    /**
     * Sets up the influence grid.
     */
    @Before
    public void setUp() {
      grid = new Influence[5][5];
      for (int row = 0; row < 5; row++) {
        for (int col = 0; col < 5; col++) {
          grid[row][col] = Influence.INFLUENCE;
        }
      }
    }

    @Test
    public void testValidConstruction() {
      c1 = new SanguineCard("Card 1", 1, 2, grid);
      assertEquals("Card 1", c1.getName());
      assertEquals(1, c1.getCost());
      assertEquals(2, c1.getValue());
      Influence[][] mockGrid = new Influence[5][5];
      for (int row = 0; row < 5; row++) {
        for (int col = 0; col < 5; col++) {
          mockGrid[row][col] = Influence.INFLUENCE;
        }
      }
      assertArrayEquals(mockGrid, c1.getGrid());
    }

    @Test
    public void testInvalidConstructionWithName() {
      assertThrows(IllegalArgumentException.class,
          () -> new SanguineCard(null, 1, 2, grid));
    }

    @Test
    public void testInvalidConstructionWithCost() {
      assertThrows(IllegalArgumentException.class,
          () -> new SanguineCard("Card 1", -1, 2, grid));
      assertThrows(IllegalArgumentException.class,
          () -> new SanguineCard("Card 1", 0, 2, grid));
    }

    @Test
    public void testInvalidConstructionWithValue() {
      assertThrows(IllegalArgumentException.class,
          () -> new SanguineCard("Card 1", 1, -1, grid));
      assertThrows(IllegalArgumentException.class,
          () -> new SanguineCard("Card 1", 1, 0, grid));
    }

    @Test
    public void testInvalidConstructionWithGrid() {
      assertThrows(IllegalArgumentException.class,
          () -> new SanguineCard("Card 1", 1, 2, null));
      Influence[][] mockGridInvalidNumRows = new Influence[3][5];
      for (int row = 0; row < 3; row++) {
        for (int col = 0; col < 5; col++) {
          mockGridInvalidNumRows[row][col] = Influence.INFLUENCE;
        }
      }
      assertThrows(IllegalArgumentException.class,
          () -> new SanguineCard("Card 1", 1, 2, mockGridInvalidNumRows));
      Influence[][] mockGridInvalidNumCols = new Influence[5][3];
      for (int row = 0; row < 5; row++) {
        for (int col = 0; col < 3; col++) {
          mockGridInvalidNumCols[row][col] = Influence.INFLUENCE;
        }
      }
      assertThrows(IllegalArgumentException.class,
          () -> new SanguineCard("Card 1", 1, 2, mockGridInvalidNumCols));
      Influence[][] mockGridNullTiles = new Influence[5][5];
      assertThrows(IllegalArgumentException.class,
          () -> new SanguineCard("Card 1", 1, 2, mockGridNullTiles));
    }

    @Test
    public void testEquals() {
      c1 = new SanguineCard("Card 1", 1, 2, grid);
      c2 = new SanguineCard("Card 1", 1, 2, grid);
      assertEquals(c1, c2);
    }
  }
}
