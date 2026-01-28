package klondike.view;

import java.io.IOException;

/**
 * A marker interface for all text-based views, to be used in the Klondike game.
 */
public interface TextualView {

  /**
   * Renders the game state to the text view.
   * Outputs the board and game information
   * to the user.
   *
   * @throws IOException if writing to the output fails
   */
  void render() throws IOException;
}