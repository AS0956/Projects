package klondike.view;

import klondike.model.hw02.KlondikeModel;

/**
 * text-based view of Klondike game.
 * view depends on the game model's string output
 *
 */
public class KlondikeTextualView implements TextualView {
  private final KlondikeModel<?> model;
  // ... any other fields you need

  /**
   * Creates a textual view of the game model.
   *
   * @param model the Klondike model to show
   */
  public KlondikeTextualView(KlondikeModel<?> model) {
    this.model = model;
  }

  /**
   * Returns the string of the game state.
   *
   * @return the model's string output
   */

  public String toString() {
    return this.model.toString();
  }
}
