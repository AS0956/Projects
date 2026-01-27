package klondike.view;

import java.io.IOException;

/**
 * A marker interface for all text-based views, to be used in the Klondike game.
 * Any class which implements the interface should
 * provide a way to render current game state.
 */
public interface TextualView {
  /**
   * Renders current game state to output.
   *
   * @throws IOException if writing state to the output
   */

  void render() throws IOException;
}
