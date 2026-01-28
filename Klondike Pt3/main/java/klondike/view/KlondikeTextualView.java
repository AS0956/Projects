package klondike.view;

import java.io.IOException;
import klondike.model.hw02.KlondikeModel;

/**
 * text-based view of Klondike game.
 * view depends on the game state and writes
 * outputs to the provided appendable
 *
 */
public class KlondikeTextualView implements TextualView {
  private final KlondikeModel<?> model;
  private final Appendable ap;

  /**
   * constructs a view which prints the
   * game state to regular output.
   *
   * @param model Klondike model to display,
   *              must not be null.
   */
  public KlondikeTextualView(KlondikeModel<?> model) {
    if (model == null) {
      throw new IllegalArgumentException("model cannot be null");
    }
    this.model = model;
    this.ap = System.out;
  }

  /**
   * Creates a textual view of the game model.
   *
   * @param model the Klondike model to show,
   *              cant be null
   * @param ap the Appendable to write output to,
   *           cant be null
   */
  public KlondikeTextualView(KlondikeModel<?> model, Appendable ap) {
    if (model == null || ap == null) {
      throw new IllegalArgumentException("model or ap is null");
    }
    this.model = model;
    this.ap = ap;
  }

  /**
   * Returns the string representation
   * of the game state.
   *
   * @return the model's string output
   */

  public String toString() {
    return this.model.toString();
  }

  /**
   * Renders current game state to the Appendable.
   *
   * @throws IllegalStateException if
   *            writing to Appendable fails.
   */
  public void render() {
    try {
      ap.append(model.toString());
      ap.append("\n");
    } catch (IOException e) {
      throw new IllegalStateException("Append didn't work", e);
    }
  }

  /**
   * Renders message to Appendable.
   *
   * @param message message to dispaly,
   *                ,can't be null
   * @throws IllegalStateException if writing
   *                    to the Appendable fails.
   */
  public void renderMessage(String message) {
    try {
      ap.append(message);
      ap.append("\n");
    } catch (IOException e) {
      throw new IllegalStateException("Append didn't work", e);
    }
  }

}

