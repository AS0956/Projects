package sanguine.controller;

import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;
import sanguine.strategy.Move;

/**
 * A human player relies on the real life user clicking on a cell and board to choose the move.
 * A move is confirmed after the user hits enter on the keyboard.
 */
public class HumanPlayerActions implements PlayerActions {
  private final Pawn color;

  /**
   * Initializes the color of the player.
   *
   * @param color the color of the player (RED or BLUE)
   */
  public HumanPlayerActions(Pawn color) {
    if (color == null) {
      throw new IllegalArgumentException("Can't have null color!\n");
    }
    this.color = color;
  }

  @Override
  public Pawn getColor() {
    return color;
  }

  // For the below three methods, it's default to 0 because the user itself is
  // making the decisions, we don't need a machine to decide.

  @Override
  public int chooseRow(ReadOnlySanguineModel model) {
    return 0;
  }

  @Override
  public int chooseCol(ReadOnlySanguineModel model) {
    return 0;
  }

  @Override
  public int chooseHandIndex(ReadOnlySanguineModel model) {
    return 0;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof HumanPlayerActions;
  }

  @Override
  public String toString() {
    return "Human";
  }
}