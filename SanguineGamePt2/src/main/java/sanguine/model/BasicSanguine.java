package sanguine.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the SanguineModel interface.
 * This class allows you to start the game given the decks, number of rows and columns, and
 * number of cards allowed in each player's hand.
 * Methods allow the current player to either pass or play a card in their hand.
 * The game is over once both players pass consecutively.
 */
public class BasicSanguine implements SanguineModel {
  //INVARIANT: Board is never null after startGame() is called.
  private SanguineCell[][] board; // row first, then column
  //INVARIANT: Red and blue players are never null after startGame() is called.
  private SanguinePlayer red;
  private SanguinePlayer blue;
  //INVARIANT: isRedPlayer changed between true and false after every turn.
  private boolean isRedPlayer;
  //INVARIANT: gameStarted is true if and only if startGame() has been called with success.
  boolean gameStarted;
  //INVARIANT: Both redPassed and bluePassed can only be true together if the game ends.
  private boolean redPassed;
  private boolean bluePassed;
  private int numRows;
  private int numCols;
  private boolean cardDrawn;

  @Override
  public void startGame(List<Card> redDeck, List<Card> blueDeck, int numRows, int numCols,
                        int numHand)
      throws IllegalArgumentException, IllegalStateException {
    if (gameStarted) {
      throw new IllegalStateException("The game has already started!\n");
    }
    if (numRows < 1) {
      throw new IllegalArgumentException("There has to be at least one row!\n");
    }
    if (numCols < 3 || numCols % 2 == 0) {
      throw new IllegalArgumentException("There has to be an odd number of columns "
          + "greater than 2!\n");
    }
    if (numRows == numCols) {
      throw new IllegalArgumentException("The board can't be a square!\n");
    }
    if ((redDeck.size() < numRows * numCols) || (blueDeck.size() < numRows * numCols)) {
      throw new IllegalArgumentException("The deck has to have enough cards "
          + "to fill every cell on the board!\n");
    }

    red = new SanguinePlayer(new ArrayList<>(redDeck), numHand, numRows);
    red.dealHand(numHand);
    blue = new SanguinePlayer(new ArrayList<>(blueDeck), numHand, numRows);
    blue.dealHand(numHand);
    board = new SanguineCell[numRows][numCols];
    this.numRows = numRows;
    this.numCols = numCols;
    cardDrawn = false;

    for (int row = 0; row < numRows; row++) {
      for (int col = 0; col < numCols; col++) {
        if (col == 0) {
          board[row][col] = new SanguineCell(Pawn.RED);
        } else if (col == numCols - 1) {
          board[row][col] = new SanguineCell(Pawn.BLUE);
        } else {
          board[row][col] = new SanguineCell();
        }
      }
    }

    isRedPlayer = true;
    gameStarted = true;
    redPassed = false;
    bluePassed = false;
  }

  @Override
  public void drawCard() throws IllegalStateException {
    checkGameStarted();
    if (isGameOver()) {
      throw new IllegalStateException("The game is over!\n");
    }
    try {
      (isRedPlayer ? red : blue).drawCard();
      cardDrawn = true;
    } catch (IllegalStateException ignored) {
      // Deck is empty, so continue game without drawing
    }
  }

  @Override
  public void pass() throws IllegalStateException {
    checkGameStarted();
    if (isGameOver()) {
      throw new IllegalStateException("The game is over!\n");
    }
    if (!cardDrawn) {
      throw new IllegalStateException("Draw card first!\n");
    }

    if (isRedPlayer) {
      redPassed = true;
    } else {
      bluePassed = true;
    }
    isRedPlayer = !isRedPlayer;
    cardDrawn = false;
  }

  @Override
  public void placeCard(int row, int col, int handIndex)
      throws IllegalArgumentException, IllegalStateException {
    checkGameStarted();
    if (isGameOver()) {
      throw new IllegalStateException("The game is over!\n");
    }
    if (!cardDrawn) {
      throw new IllegalStateException("Draw card first!\n");
    }

    Player currentPlayer = isRedPlayer ? red : blue;
    Card card;
    SanguineCell cell;

    if (isLegal(row, col, handIndex)) {
      card = currentPlayer.getHand().get(handIndex);
      cell = board[row][col];
      cell.placeCard(card);
      currentPlayer.useCard(row, handIndex);
      applyInfluence(row, col, card);
      if (isRedPlayer) {
        redPassed = false;
      } else {
        bluePassed = false;
      }
      isRedPlayer = !isRedPlayer;
    } else {
      throw new IllegalStateException("Illegal move!\n");
    }
    cardDrawn = false;
  }

  // Helper method to apply card influence to the board.
  private void applyInfluence(int row, int col, Card card) {
    Influence[][] grid = card.getGrid();
    Pawn currentPawn = isRedPlayer ? Pawn.RED : Pawn.BLUE;

    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        if (grid[i][j] != Influence.INFLUENCE) {
          continue;
        }

        int boardRow = row + (i - 2);
        int boardCol;

        if (isRedPlayer) {
          boardCol = col + (j - 2);
        } else {
          boardCol = col - (j - 2);
        }

        if (boardRow < 0 || boardRow >= board.length
            || boardCol < 0 || boardCol >= board[0].length) {
          continue;
        }

        SanguineCell targetCell = board[boardRow][boardCol];

        if (targetCell.isEmpty()) {
          targetCell.addPawn(currentPawn);
        } else if (!targetCell.hasCard()) {
          if (targetCell.getOwningColor() == currentPawn) {
            if (targetCell.getPawnCount() < 3) {
              targetCell.addPawn(currentPawn);
            }
          } else {
            targetCell.convertPawns();
          }
        }
      }
    }
  }

  @Override
  public boolean isGameOver() throws IllegalStateException {
    if (!gameStarted) {
      throw new IllegalStateException("The game hasn't started yet!\n");
    }
    return redPassed && bluePassed;
  }

  @Override
  public Pawn getWinner() throws IllegalStateException {
    checkGameStarted();
    if (!isGameOver()) {
      throw new IllegalStateException("The game is not over yet!\n");
    }

    if (getRedScore() > getBlueScore()) {
      return Pawn.RED;
    } else if (getBlueScore() > getRedScore()) {
      return Pawn.BLUE;
    } else {
      return null;
    }
  }

  @Override
  public Pawn getTurn() throws IllegalStateException {
    checkGameStarted();
    if (isGameOver()) {
      throw new IllegalStateException("The game is over!\n");
    }
    if (isRedPlayer) {
      return Pawn.RED;
    }
    return Pawn.BLUE;
  }

  @Override
  public SanguineCell[][] getBoard() throws IllegalStateException {
    checkGameStarted();
    SanguineCell[][] board = new SanguineCell[this.board.length][this.board[0].length];
    for (int row = 0; row < board.length; row++) {
      for (int col = 0; col < board[0].length; col++) {
        SanguineCell thisCell = this.board[row][col];
        board[row][col] = new SanguineCell();
        if (thisCell.hasCard()) {
          for (int pawn = 0; pawn < thisCell.getCard().getCost(); pawn++) {
            board[row][col].addPawn(thisCell.getOwningColor());
          }
          board[row][col].placeCard(thisCell.getCard());
        } else if (!thisCell.isEmpty()) {
          for (int pawn = 0; pawn < thisCell.getPawnCount(); pawn++) {
            board[row][col].addPawn(thisCell.getOwningColor());
          }
        }
      }
    }
    return board;
  }

  @Override
  public int getRedRowScore(int index) throws IllegalArgumentException, IllegalStateException {
    return getRowScore(index, red);
  }

  @Override
  public int getBlueRowScore(int index) throws IllegalArgumentException, IllegalStateException {
    return getRowScore(index, blue);
  }

  private int getRowScore(int index, SanguinePlayer player) {
    checkGameStarted();
    if (index < 0 || index > numRows - 1) {
      throw new IllegalArgumentException("Invalid index!\n");
    }
    return player.getScores()[index];
  }

  @Override
  public int getRedScore() throws IllegalStateException {
    return getScore(red);
  }

  @Override
  public int getBlueScore() throws IllegalStateException {
    return getScore(blue);
  }

  private int getScore(SanguinePlayer player) {
    checkGameStarted();
    int total = 0;
    for (SanguineCell[] sanguineCells : board) {
      int redRowScore = 0;
      int blueRowScore = 0;

      for (int col = 0; col < board[0].length; col++) {
        SanguineCell cell = sanguineCells[col];
        if (cell.hasCard()) {
          if (cell.getOwningColor() == Pawn.RED) {
            redRowScore += cell.getCard().getValue();
          } else if (cell.getOwningColor() == Pawn.BLUE) {
            blueRowScore += cell.getCard().getValue();
          }
        }
      }

      if (player.equals(red) && redRowScore > blueRowScore) {
        total += redRowScore;
      } else if (player.equals(blue) && blueRowScore > redRowScore) {
        total += blueRowScore;
      }
    }
    return total;
  }

  @Override
  public List<Card> getRedPlayerHand() throws IllegalStateException {
    checkGameStarted();
    return new ArrayList<>(red.getHand());
  }

  @Override
  public List<Card> getBluePlayerHand() throws IllegalStateException {
    checkGameStarted();
    return new ArrayList<>(blue.getHand());
  }

  @Override
  public int getNumRows() throws IllegalStateException {
    checkGameStarted();
    return numRows;
  }

  @Override
  public int getNumCols() throws IllegalStateException {
    checkGameStarted();
    return numCols;
  }

  @Override
  public List<CellContent> getCellContent(int row, int col) throws IllegalArgumentException,
      IllegalStateException {
    checkGameStarted();
    if (row < 0 || row > numRows - 1 || col < 0 || col > numCols - 1) {
      throw new IllegalArgumentException("Invalid row or column index!\n");
    }
    List<CellContent> contents = new ArrayList<>();
    if (getBoard()[row][col].hasCard()) {
      contents.add(getBoard()[row][col].getCard());
    } else if (!getBoard()[row][col].isEmpty()) {
      contents.addAll(getBoard()[row][col].getPawns());
    }
    return contents;
  }

  @Override
  public Pawn getCellOwnership(int row, int col) throws IllegalArgumentException,
      IllegalStateException {
    checkGameStarted();
    if (row < 0 || row > numRows - 1 || col < 0 || col > numCols - 1) {
      throw new IllegalArgumentException("Invalid row or column index!\n");
    }
    return getBoard()[row][col].getOwningColor();
  }

  @Override
  public boolean isLegal(int row, int col, int handIndex) throws IllegalArgumentException,
      IllegalStateException {
    checkGameStarted();
    Player currentPlayer = isRedPlayer ? red : blue;
    Pawn currentPawn = isRedPlayer ? Pawn.RED : Pawn.BLUE;
    if (row < 0 || row > numRows - 1 || col < 0 || col > numCols - 1 || handIndex < 0
        || handIndex > currentPlayer.getHand().size() - 1) {
      throw new IllegalArgumentException("Invalid row, column, or hand index!\n");
    }

    Card card = currentPlayer.getHand().get(handIndex);
    SanguineCell cell = board[row][col];

    if (cell.getOwningColor() != currentPawn) {
      throw new IllegalArgumentException("Cell is not owned by current player!\n");
    }
    if (cell.hasCard() || cell.getPawnCount() < card.getCost()) {
      throw new IllegalArgumentException("Can't place card in this cell!\n");
    }
    return true;
  }

  private void checkGameStarted() {
    if (!gameStarted) {
      throw new IllegalStateException("The game hasn't started yet!\n");
    }
  }
}