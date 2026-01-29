package sanguine.tests;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import sanguine.model.Card;
import sanguine.model.Cell;
import sanguine.model.Influence;
import sanguine.model.Pawn;
import sanguine.model.SanguineCard;
import sanguine.model.SanguineCell;

/**
 * Tests the SanguineCell class.
 */
public class SanguineCellTest {
  private Cell cell;
  private Card card;

  /**
   * Sets up the grid influence.
   * Initializes the card.
   */
  @Before
  public void setUp() {
    Influence[][] grid = new Influence[5][5];
    for (int row = 0; row < 5; row++) {
      for (int col = 0; col < 5; col++) {
        grid[row][col] = Influence.NONE;
      }
    }
    card = new SanguineCard("Card", 2, 3, grid);
  }

  @Test
  public void testValidEmptyConstruction() {
    cell = new SanguineCell();
    assertTrue(cell.isEmpty());
    assertFalse(cell.hasCard());
    assertNull(cell.getOwningColor());
  }

  @Test
  public void testValidStartingCellConstruction() {
    cell = new SanguineCell(Pawn.RED);
    assertFalse(cell.isEmpty());
    assertEquals(Pawn.RED, cell.getPawns().getFirst());
    assertFalse(cell.hasCard());
    assertEquals(Pawn.RED, cell.getOwningColor());
  }

  @Test
  public void testInvalidStartingCellConstructionWithNullColor() {
    assertThrows(IllegalArgumentException.class, () -> new SanguineCell(null));
  }

  @Test
  public void testGetPawns() {
    cell = new SanguineCell(Pawn.RED);
    assertEquals(List.of(Pawn.RED), cell.getPawns());
    List<Pawn> pawns = cell.getPawns();
    pawns.clear();
    assertNotEquals(pawns, cell.getPawns());
  }

  @Test
  public void testInvalidGetPawnsWithEmptyPawns() {
    cell = new SanguineCell();
    assertThrows(IllegalStateException.class, () -> cell.getPawns());
  }

  @Test
  public void testGetCard() {
    Influence[][] grid = new Influence[5][5];
    for (int row = 0; row < 5; row++) {
      for (int col = 0; col < 5; col++) {
        grid[row][col] = Influence.NONE;
      }
    }
    cell = new SanguineCell(Pawn.RED);
    cell.addPawn(Pawn.RED);
    cell.placeCard(card);
    assertEquals(new SanguineCard("Card", 2, 3, grid), cell.getCard());
  }

  @Test
  public void testInvalidGetCardWithNoCardInCell() {
    cell = new SanguineCell();
    assertThrows(IllegalStateException.class, () -> cell.getCard());
  }

  @Test
  public void testGetOwningColor() {
    cell = new SanguineCell(Pawn.RED);
    assertEquals(Pawn.RED, cell.getOwningColor());
  }

  @Test
  public void testValidGetOwningColorWithNullOwningColor() {
    cell = new SanguineCell();
    assertNull(cell.getOwningColor());
  }

  @Test
  public void testAddPawn() {
    cell = new SanguineCell();
    assertThrows(IllegalStateException.class, () -> cell.getPawns());
    assertNull(cell.getOwningColor());
    cell.addPawn(Pawn.RED);
    assertEquals(List.of(Pawn.RED), cell.getPawns());
    assertEquals(Pawn.RED, cell.getOwningColor());
  }

  @Test
  public void testInvalidAddPawnWithNullPawn() {
    cell = new SanguineCell();
    assertThrows(IllegalArgumentException.class, () -> cell.addPawn(null));
  }

  @Test
  public void testInvalidAddPawnWithWrongColorPawn() {
    cell = new SanguineCell(Pawn.RED);
    assertThrows(IllegalArgumentException.class, () -> cell.addPawn(Pawn.BLUE));
  }

  @Test
  public void testInvalidAddPawnWithThreePawnsInCell() {
    cell = new SanguineCell(Pawn.RED);
    cell.addPawn(Pawn.RED);
    cell.addPawn(Pawn.RED);
    assertThrows(IllegalStateException.class, () -> cell.addPawn(Pawn.RED));
  }

  @Test
  public void testInvalidAddPawnWithCardInCell() {
    cell = new SanguineCell(Pawn.RED);
    cell.addPawn(Pawn.RED);
    cell.placeCard(card);
    assertThrows(IllegalStateException.class, () -> cell.addPawn(Pawn.RED));
  }

  @Test
  public void testPlaceCard() {
    Influence[][] grid = new Influence[5][5];
    for (int row = 0; row < 5; row++) {
      for (int col = 0; col < 5; col++) {
        grid[row][col] = Influence.NONE;
      }
    }
    cell = new SanguineCell(Pawn.RED);
    cell.addPawn(Pawn.RED);
    cell.placeCard(card);
    assertEquals(new SanguineCard("Card", 2, 3, grid), cell.getCard());
    assertThrows(IllegalStateException.class, () -> cell.getPawns());
  }

  @Test
  public void testInvalidPlaceCardWithNullCard() {
    cell = new SanguineCell();
    assertThrows(IllegalArgumentException.class, () -> cell.placeCard(null));
  }

  @Test
  public void testInvalidPlaceCardWithCardAlreadyInCell() {
    cell = new SanguineCell(Pawn.RED);
    cell.addPawn(Pawn.RED);
    cell.placeCard(card);
    assertThrows(IllegalStateException.class, () -> cell.placeCard(card));
  }

  @Test
  public void testInvalidPlaceCardWithNotEnoughPawnsInCellToCoverCardCost() {
    cell = new SanguineCell(Pawn.RED);
    assertThrows(IllegalStateException.class, () -> cell.placeCard(card));
  }

  @Test
  public void testHasCard() {
    cell = new SanguineCell(Pawn.RED);
    assertFalse(cell.hasCard());
    cell.addPawn(Pawn.RED);
    cell.placeCard(card);
    assertTrue(cell.hasCard());
  }

  @Test
  public void testIsEmpty() {
    cell = new SanguineCell();
    assertTrue(cell.isEmpty());
    cell.addPawn(Pawn.BLUE);
    assertFalse(cell.isEmpty());
  }

  @Test
  public void testGetPawnCount() {
    cell = new SanguineCell();
    assertEquals(0, cell.getPawnCount());
    cell.addPawn(Pawn.BLUE);
    assertEquals(1, cell.getPawnCount());
    ;
  }

  @Test
  public void testClearPawns() {
    cell = new SanguineCell(Pawn.BLUE);
    assertEquals(List.of(Pawn.BLUE), cell.getPawns());
    cell.clearPawns();
    assertThrows(IllegalStateException.class, () -> cell.getPawns());
  }

  @Test
  public void testConvertPawns() {
    cell = new SanguineCell(Pawn.BLUE);
    assertEquals(Pawn.BLUE, cell.getPawns().getFirst());
    cell.convertPawns();
    assertEquals(Pawn.RED, cell.getPawns().getFirst());
    assertEquals(Pawn.RED, cell.getOwningColor());
  }

  @Test
  public void testInvalidConvertPawnsWithNoPawnsInCell() {
    cell = new SanguineCell();
    assertThrows(IllegalStateException.class, () -> cell.convertPawns());
  }

  @Test
  public void testInvalidConvertPawnsWithCardInCell() {
    cell = new SanguineCell(Pawn.BLUE);
    cell.addPawn(Pawn.BLUE);
    cell.placeCard(card);
    assertThrows(IllegalStateException.class, () -> cell.convertPawns());
  }
}