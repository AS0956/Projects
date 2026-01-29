package sanguine.mocks;

import java.util.ArrayList;
import java.util.List;
import sanguine.model.Card;
import sanguine.model.CellContent;
import sanguine.model.ModelStatusListener;
import sanguine.model.Pawn;
import sanguine.model.SanguineCell;
import sanguine.model.SanguineModel;

/**
 * Mocks the model for controller testing.
 */
public class ModelMock implements SanguineModel {
  public boolean listenerRegistered = false;
  public Pawn currentTurn = Pawn.RED;
  boolean placeCardCalled = false;
  boolean passCalled = false;

  @Override
  public void addModelStatusListener(ModelStatusListener listener) {
    listenerRegistered = true;
  }

  @Override
  public Pawn getTurn() {
    return currentTurn;
  }

  @Override
  public void placeCard(int row, int col, int handIndex) {
    placeCardCalled = true;
  }

  @Override
  public void pass() {
    passCalled = true;
  }

  @Override
  public void beginGame() {}

  @Override
  public void drawCard() {}

  @Override
  public void startGame(List<Card> redDeck, List<Card> blueDeck,
                        int numRows, int numCols, int numHand) {}

  @Override
  public boolean isGameOver() {
    return false;
  }

  @Override
  public Pawn getWinner() {
    return null;
  }

  @Override
  public SanguineCell[][] getBoard() {
    return new SanguineCell[0][0];
  }

  @Override
  public int getRedRowScore(int index) {
    return 0;
  }

  @Override
  public int getBlueRowScore(int index) {
    return 0;
  }

  @Override
  public int getRedScore() {
    return 0;
  }

  @Override
  public int getBlueScore() {
    return 0;
  }

  @Override
  public List<Card> getRedPlayerHand() {
    return new ArrayList<>();
  }

  @Override
  public List<Card> getBluePlayerHand() {
    return new ArrayList<>();
  }

  @Override
  public int getNumRows() {
    return 5;
  }

  @Override
  public int getNumCols() {
    return 7;
  }

  @Override
  public List<CellContent> getCellContent(int row, int col) {
    return new ArrayList<>();
  }

  @Override
  public Pawn getCellOwnership(int row, int col) {
    return null;
  }

  @Override
  public boolean isLegal(int row, int col, int handIndex) {
    return true;
  }
}
