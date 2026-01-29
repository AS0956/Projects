package sanguine.controller;

import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.model.SanguineModel;
import sanguine.view.Features;
import sanguine.view.SanguineFrame;

/**
 * This stub controller that prints user actions to the console.
 */
public class StubController implements Features {
  private final SanguineModel model;
  private final SanguineFrame view;
  private int row;
  private int col;
  private int handIndex;

  /**
   * Makes a stub controller.
   *
   * @param model the game model to read from
   */
  public StubController(SanguineModel model, SanguineFrame view) {
    this.model = model;
    this.view = view;
    row = 0;
    col = 0;
    handIndex = 0;

    view.addFeatures(this);
    view.display();
  }

  @Override
  public void onCardSelected(int cardIndex) {
    try {
      Pawn currentPlayer = model.getTurn();
      System.out.printf("Player %s selected the card at index %d!\n",
          currentPlayer.toString(), cardIndex);
      handIndex = cardIndex;
    } catch (IllegalStateException e) {
      System.out.println("Error selecting card!\n");
    }
  }

  @Override
  public void onCellSelected(int row, int col) {
    System.out.println("Cell was selected at row " + row + " and column " + col + "!\n");
    this.row = row;
    this.col = col;
  }

  @Override
  public void onConfirmMove() {
    try {
      model.placeCard(row, col, handIndex);
      view.repaint();
    } catch (IllegalStateException e) {
      System.out.println("Game not started or game is over or must draw a card!\n");
    } catch (IllegalArgumentException invalidArg) {
      System.out.println("Invalid argument(s)!\n");
    }
    System.out.println("Move confirmed!\n");
  }

  @Override
  public void onPass() {
    try {
      model.pass();
      view.repaint();
    } catch (IllegalStateException e) {
      System.out.println("Game not started or game is over or must draw a card!\n");
    }
    System.out.println("Turn passed!\n");
  }
}
