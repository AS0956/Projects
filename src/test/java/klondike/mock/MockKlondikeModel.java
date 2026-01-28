package klondike.mock;

import java.util.List;
import klondike.model.hw02.BasicCard;
import klondike.model.hw02.BasicKlondike;

/**
 * Mock Klondike model for controller testing.
 * Tracks method calls and provides a fake game state for quit messages.
 */
public class MockKlondikeModel extends BasicKlondike {

  public boolean startGameCalled = false;
  public boolean movePileCalled = false;
  public boolean moveDrawCalled = false;
  public boolean moveToFoundationCalled = false;
  public boolean moveDrawToFoundationCalled = false;
  public boolean discardDrawCalled = false;
  public boolean isGameOverCalled = false;
  public boolean getScoreCalled = false;

  private boolean gameOver = false;
  private int score = 0;
  private boolean firstCheck = true;
  private boolean simulateInvalidMove = false;

  /**
   * Starts the game. Tracks call.
   */
  @Override
  public void startGame(List<BasicCard> deck, boolean shuffle, int numPiles, int numDraw) {
    startGameCalled = true;
  }

  /**
   * Moves cards from one pile to another. Tracks call.
   */
  @Override
  public void movePile(int srcPile, int numCards, int destPile) {
    if (simulateInvalidMove) {
      throw new IllegalArgumentException("Invalid move simulated.");
    }
    movePileCalled = true;
  }

  /**
   * Moves card from draw to cascade pile. Tracks call.
   */
  @Override
  public void moveDraw(int destPile) {
    if (simulateInvalidMove) {
      throw new IllegalArgumentException("Invalid move simulated.");
    }
    moveDrawCalled = true;
  }

  /**
   * Moves card from cascade to foundation. Tracks call.
   */
  @Override
  public void moveToFoundation(int srcPile, int foundationPile) {
    if (simulateInvalidMove) {
      throw new IllegalArgumentException("Invalid move simulated.");
    }
    moveToFoundationCalled = true;
  }

  /**
   * Moves card from draw to foundation. Tracks call.
   */
  @Override
  public void moveDrawToFoundation(int foundationPile) {
    if (simulateInvalidMove) {
      throw new IllegalArgumentException("Invalid move simulated.");
    }
    moveDrawToFoundationCalled = true;
  }

  /**
   * Discards card from draw. Tracks call.
   */
  @Override
  public void discardDraw() {
    if (simulateInvalidMove) {
      throw new IllegalArgumentException("Invalid move simulated.");
    }
    discardDrawCalled = true;
  }

  /**
   * Returns whether the game is over. Tracks call.
   */
  @Override
  public boolean isGameOver() {
    isGameOverCalled = true;
    if (firstCheck) {
      firstCheck = false;
      return false; // allow controller loop to run once
    }
    return gameOver;
  }

  /**
   * Returns current score. Tracks call.
   */
  @Override
  public int getScore() {
    getScoreCalled = true;
    return score;
  }

  /**
   * Sets the game over state. Resets first check for loop.
   */
  public void setGameOver(boolean value) {
    gameOver = value;
    firstCheck = true;
  }

  /**
   * Sets the current score.
   */
  public void setScore(int s) {
    score = s;
  }

  /**
   * Set whether to simulate invalid moves for testing controller retries.
   */
  public void setSimulateInvalidMove(boolean value) {
    simulateInvalidMove = value;
  }

  /**
   * Returns a fake board state as string for quit messages.
   */
  public String getFakeBoardState() {
    return "Draw: 3♢, 2♢, 8♡\n"
        +
        "Foundation: <none>, <none>, <none>, <none>\n"
        +
        " 2♠  ?  ?  ?  ?  ?  ?\n"
        +
        "    6♣  ?  ?  ?  ?  ?\n"
        +
        "       A♢  ?  ?  ?\n"
        +
        "          K♣  ?  ?\n"
        +
        "             7♠  ?  ?\n"
        +
        "                7♣  ?\n"
        +
        "                   5♡";
  }
}
