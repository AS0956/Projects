package sanguine.view;

import sanguine.model.SanguineModel;

/**
 * Represents the view for the Sanguine game.
 */
public interface View {
  /**
   * Renders the current game state.
   *
   * @param game the model to be rendered
   * @return a string representing the current game state of the board
   * @throws IllegalArgumentException if the given game is null
   */
  String render(SanguineModel game) throws IllegalArgumentException;
}
