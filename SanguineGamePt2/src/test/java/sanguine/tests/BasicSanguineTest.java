package sanguine.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import sanguine.model.BasicSanguine;
import sanguine.model.Card;
import sanguine.model.Influence;
import sanguine.model.Pawn;
import sanguine.model.SanguineCard;
import sanguine.model.SanguineCell;
import sanguine.model.SanguineModel;

/**
 * Test for the BasicSanguine class.
 */
public class BasicSanguineTest {
  SanguineModel model = new BasicSanguine();
  Card card;
  List<Card> redDeck;
  List<Card> blueDeck;

  /**
   * Sets up the influence grid and decks.
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
    for (int index = 0; index < 6; index++) {
      redDeck.add(card);
      blueDeck.add(card);
    }
  }

  @Test
  public void testValidStartGame() {
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertEquals(List.of(card, card), model.getRedPlayerHand());
    assertEquals(List.of(card, card), model.getBluePlayerHand());
    assertEquals(2, model.getBoard().length);
    assertEquals(3, model.getBoard()[0].length);
    for (int row = 0; row < 2; row++) {
      for (int col = 0; col < 3; col++) {
        if (col == 0) {
          assertEquals(List.of(Pawn.RED), model.getCellContent(row, col));
        } else if (col == 2) {
          assertEquals(List.of(Pawn.BLUE), model.getCellContent(row, col));
        } else {
          assertTrue(model.getCellContent(row, col).isEmpty());
        }
      }
    }
    assertEquals(Pawn.RED, model.getTurn());
    assertFalse(model.isGameOver());
  }

  @Test
  public void testInvalidStartGameWhenGameAlreadyStarted() {
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertThrows(IllegalStateException.class,
        () -> model.startGame(redDeck, blueDeck, 2, 3, 2));
  }

  @Test
  public void testInvalidStartGameWithInvalidNumberOfRows() {
    assertThrows(IllegalArgumentException.class,
        () -> model.startGame(redDeck, blueDeck, -1, 3, 2));
    assertThrows(IllegalArgumentException.class,
        () -> model.startGame(redDeck, blueDeck, 0, 3, 2));
  }

  @Test
  public void testInvalidStartGameWithInvalidNumberOfColumns() {
    assertThrows(IllegalArgumentException.class,
        () -> model.startGame(redDeck, blueDeck, 2, -1, 2));
    assertThrows(IllegalArgumentException.class,
        () -> model.startGame(redDeck, blueDeck, 2, 0, 2));
    assertThrows(IllegalArgumentException.class,
        () -> model.startGame(redDeck, blueDeck, 2, 4, 2));
  }

  @Test
  public void testInvalidStartGameWithSquareBoard() {
    assertThrows(IllegalArgumentException.class,
        () -> model.startGame(redDeck, blueDeck, 2, 2, 2));
  }

  @Test
  public void testInvalidStartGameWithInvalidDeckSize() {
    redDeck.removeLast();
    blueDeck.removeLast();
    assertThrows(IllegalArgumentException.class,
        () -> model.startGame(redDeck, blueDeck, 2, 3, 2));
  }

  @Test // not finished testing
  public void testDrawCard() {
    assertThrows(IllegalStateException.class, () -> model.drawCard());
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    model.drawCard();
    assertEquals(List.of(card, card, card), model.getRedPlayerHand());
  }

  @Test
  public void testInvalidDrawCardWhenGameIsOver() {
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    model.drawCard();
    model.pass();
    model.drawCard();
    model.pass();
    assertThrows(IllegalStateException.class, () -> model.drawCard());
  }

  @Test
  public void testPass() {
    assertThrows(IllegalStateException.class, () -> model.pass());
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    model.drawCard();
    model.pass();
    assertEquals(Pawn.BLUE, model.getTurn());
    model.drawCard();
    model.pass();
    assertTrue(model.isGameOver());
    assertThrows(IllegalStateException.class, () -> model.pass());
  }

  @Test
  public void testPlaceCard() {
    assertThrows(IllegalStateException.class, () -> model.placeCard(1, 0, 0));
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    model.drawCard();
    model.placeCard(1, 0, 0);
    assertEquals(List.of(Pawn.RED, Pawn.RED), model.getCellContent(0, 0));
    assertEquals(List.of(Pawn.RED), model.getCellContent(1, 1));
    assertEquals(2, model.getRedPlayerHand().size());
    model.drawCard();
    model.pass();
    model.drawCard();
    model.placeCard(0, 0, 0);
    model.drawCard();
    model.pass();
    model.drawCard();
    model.placeCard(1, 1, 0);
    model.drawCard();
    model.pass();
    model.drawCard();
    model.placeCard(0, 1, 0);
    model.drawCard();
    model.pass();
    model.drawCard();
    model.placeCard(1, 2, 0);
    assertEquals(1, model.getRedPlayerHand().size());
    assertEquals(Pawn.BLUE, model.getTurn());
  }

  @Test
  public void testIsLegalWithInvalidRowsAndColumnsIndex() {
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertThrows(IllegalArgumentException.class,
        () -> model.isLegal(-1, 0, 0));
    assertThrows(IllegalArgumentException.class,
        () -> model.isLegal(2, 0, 0));
    assertThrows(IllegalArgumentException.class,
        () -> model.isLegal(1, -1, 0));
    assertThrows(IllegalArgumentException.class,
        () -> model.isLegal(-1, 3, 0));
  }

  @Test
  public void testIsLegalWithInvalidHandIndex() {
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertThrows(IllegalArgumentException.class,
        () -> model.isLegal(1, 0, -1));
    assertThrows(IllegalArgumentException.class,
        () -> model.isLegal(1, 0, 2));
  }

  @Test
  public void testIsLegalWhenCellIsNotOwnedByPlayer() {
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertThrows(IllegalArgumentException.class,
        () -> model.isLegal(1, 2, 0));
    assertThrows(IllegalArgumentException.class,
        () -> model.isLegal(0, 2, 0));
  }

  @Test
  public void testIsLegalWhenNotEnoughPawnsToCoverCostOfCard() {
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertThrows(IllegalArgumentException.class,
        () -> model.isLegal(1, 1, 0));
    assertThrows(IllegalArgumentException.class,
        () -> model.isLegal(0, 1, 0));
  }

  @Test
  public void testIsGameOver() {
    assertThrows(IllegalStateException.class, () -> model.isGameOver());
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertFalse(model.isGameOver());
    model.drawCard();
    model.pass();
    model.drawCard();
    model.pass();
    assertTrue(model.isGameOver());
  }

  @Test
  public void testGetWinnerReturnsRedPlayer() {
    assertThrows(IllegalStateException.class, () -> model.getWinner());
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertThrows(IllegalStateException.class, () -> model.getWinner());
    model.drawCard();
    model.placeCard(1, 0, 0);
    model.drawCard();
    model.pass();
    model.drawCard();
    model.placeCard(0, 0, 0);
    model.drawCard();
    model.pass();
    model.drawCard();
    model.placeCard(1, 1, 0);
    model.drawCard();
    model.pass();
    model.drawCard();
    model.pass();
    assertEquals(Pawn.RED, model.getWinner());
  }

  @Test
  public void testGetWinnerReturnsBluePlayer() {
    assertThrows(IllegalStateException.class, () -> model.getWinner());
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertThrows(IllegalStateException.class, () -> model.getWinner());
    model.drawCard();
    model.pass();
    model.drawCard();
    model.placeCard(1, 2, 0);
    model.drawCard();
    model.pass();
    model.drawCard();
    model.placeCard(0, 2, 0);
    model.drawCard();
    model.pass();
    model.drawCard();
    model.placeCard(1, 1, 0);
    model.drawCard();
    model.pass();
    model.drawCard();
    model.pass();
    assertEquals(Pawn.BLUE, model.getWinner());
  }

  @Test
  public void testGetWinnerReturnsNull() {
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    model.drawCard();
    model.pass();
    model.drawCard();
    model.pass();
    assertNull(model.getWinner());
  }

  @Test
  public void testGetTurn() {
    assertThrows(IllegalStateException.class, () -> model.getTurn());
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertEquals(Pawn.RED, model.getTurn());
    model.drawCard();
    model.pass();
    assertEquals(Pawn.BLUE, model.getTurn());
  }

  @Test
  public void testGetBoard() {
    assertThrows(IllegalStateException.class, () -> model.getBoard());
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    model.drawCard();
    SanguineCell[][] board = model.getBoard();
    model.placeCard(1, 0, 0);
    assertEquals(List.of(Pawn.RED), model.getBoard()[1][1].getPawns());
    assertTrue(board[1][1].isEmpty());
    assertTrue(model.getBoard()[1][0].hasCard());
  }

  @Test
  public void testGetRedRowScore() {
    assertThrows(IllegalStateException.class, () -> model.getRedRowScore(0));
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertEquals(0, model.getRedRowScore(0));
    model.drawCard();
    model.placeCard(1, 0, 0);
    assertEquals(2, model.getRedRowScore(1));
  }

  @Test
  public void testGetBlueRowScore() {
    assertThrows(IllegalStateException.class, () -> model.getBlueRowScore(0));
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertEquals(0, model.getBlueRowScore(0));
    model.drawCard();
    model.pass();
    model.drawCard();
    model.placeCard(1, 2, 0);
    assertEquals(2, model.getBlueRowScore(1));
  }

  @Test
  public void testGetRedScore() {
    assertThrows(IllegalStateException.class, () -> model.getRedScore());
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertEquals(0, model.getRedScore());
    model.drawCard();
    model.placeCard(1, 0, 0);
    assertEquals(2, model.getRedScore());
    model.drawCard();
    model.placeCard(1, 2, 0);
    assertEquals(0, model.getRedScore());
  }

  @Test
  public void testGetBlueScore() {
    assertThrows(IllegalStateException.class, () -> model.getBlueScore());
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertEquals(0, model.getBlueScore());
    model.drawCard();
    model.pass();
    model.drawCard();
    model.placeCard(0, 2, 0);
    assertEquals(2, model.getBlueScore());
    model.drawCard();
    model.placeCard(0, 0, 0);
    assertEquals(0, model.getBlueScore());
  }

  @Test
  public void testGetRedPlayerHand() {
    assertThrows(IllegalStateException.class, () -> model.getRedPlayerHand());
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertEquals(List.of(card, card), model.getRedPlayerHand());
    List<Card> hand = model.getRedPlayerHand();
    hand.removeLast();
    model.drawCard();
    model.placeCard(1, 0, 0);
    assertEquals(List.of(card, card), model.getRedPlayerHand());
    model.drawCard();
    model.pass();
    model.drawCard();
    model.pass();
    assertEquals(List.of(card, card, card), model.getRedPlayerHand());
  }

  @Test
  public void testGetBluePlayerHand() {
    assertThrows(IllegalStateException.class, () -> model.getBluePlayerHand());
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertEquals(List.of(card, card), model.getBluePlayerHand());
    List<Card> hand = model.getBluePlayerHand();
    hand.removeLast();
    model.drawCard();
    model.pass();
    model.drawCard();
    model.placeCard(1, 2, 0);
    assertEquals(List.of(card, card), model.getBluePlayerHand());
    model.drawCard();
    model.pass();
    model.drawCard();
    model.pass();
    assertEquals(List.of(card, card, card), model.getBluePlayerHand());
  }

  @Test
  public void testGetNumRows() {
    assertThrows(IllegalStateException.class, () -> model.getNumRows());
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertEquals(2, model.getNumRows());
  }

  @Test
  public void testGetNumCols() {
    assertThrows(IllegalStateException.class, () -> model.getNumCols());
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertEquals(3, model.getNumCols());
  }

  @Test
  public void testGetCellContentRed() {
    assertThrows(IllegalStateException.class, () -> model.getCellContent(0, 0));
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertEquals(List.of(Pawn.RED), model.getCellContent(1, 0));
    assertEquals(List.of(), model.getCellContent(0, 1));
    model.drawCard();
    model.placeCard(0, 0, 0);
    assertEquals(List.of(card), model.getCellContent(0, 0));
    assertEquals(List.of(Pawn.RED, Pawn.RED), model.getCellContent(1, 0));
    assertEquals(List.of(Pawn.RED), model.getCellContent(0, 1));
    assertEquals(List.of(), model.getCellContent(1, 1));
  }

  @Test
  public void testGetCellContentBlue() {
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    model.drawCard();
    model.pass();
    Card card = model.getBluePlayerHand().getFirst();
    model.drawCard();
    model.placeCard(1, 2, 0);
    assertEquals(List.of(card), model.getCellContent(1, 2));
    assertEquals(List.of(Pawn.BLUE, Pawn.BLUE), model.getCellContent(0, 2));
    assertEquals(List.of(Pawn.BLUE), model.getCellContent(1, 1));
    assertEquals(List.of(), model.getCellContent(0, 1));
  }

  @Test
  public void testInvalidGetCellContentWithInvalidRowIndex() {
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertThrows(IllegalArgumentException.class, () -> model.getCellContent(-1, 0));
  }

  @Test
  public void testInvalidGetCellContentWithInvalidColumnIndex() {
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertThrows(IllegalArgumentException.class, () -> model.getCellContent(0, -1));
  }

  @Test
  public void testGetCellOwnership() {
    assertThrows(IllegalStateException.class, () -> model.getCellOwnership(0, 0));
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertEquals(Pawn.RED, model.getCellOwnership(0, 0));
    assertEquals(Pawn.BLUE, model.getCellOwnership(0, 2));
    assertNull(model.getCellOwnership(0, 1));
    model.drawCard();
    model.placeCard(0, 0, 0);
    assertEquals(Pawn.RED, model.getCellOwnership(0, 1));
  }

  @Test
  public void testInvalidGetCellOwnershipWithInvalidRowIndex() {
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertThrows(IllegalArgumentException.class, () -> model.getCellOwnership(-1, 0));
  }

  @Test
  public void testInvalidGetCellOwnershipWithInvalidColumnIndex() {
    model.startGame(redDeck, blueDeck, 2, 3, 2);
    assertThrows(IllegalArgumentException.class, () -> model.getCellOwnership(0, -1));
  }
}