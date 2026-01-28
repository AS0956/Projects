package klondike.view;

import java.io.IOException;
import klondike.model.hw02.KlondikeModel;

/**
 * Text-based view of the Klondike game.
 * Renders the game state to an Appendable destination.
 * The view depends on the game model's string representation.
 */
public class KlondikeTextualView implements TextualView {
  private final KlondikeModel<?> model;
  private final Appendable ap;

  /**
   * Constructs a textual view with the given model.
   * Output is sent to System.out.
   *
   * @param model the Klondike model to display; must not be null
   * @throws IllegalArgumentException if model is null
   */
  public KlondikeTextualView(KlondikeModel<?> model) {
    if (model == null) {
      throw new IllegalArgumentException("model cannot be null");
    }
    this.model = model;
    this.ap = System.out;
  }

  /**
   * Constructs a textual view with the given model and output destination.
   *
   * @param model the Klondike model to display; must not be null
   * @param ap the Appendable to send output to; must not be null
   * @throws IllegalArgumentException if model or ap is null
   */
  public KlondikeTextualView(KlondikeModel<?> model, Appendable ap) {
    if (model == null || ap == null) {
      throw new IllegalArgumentException("model or ap is null");
    }
    this.model = model;
    this.ap = ap;
  }

  /**
   * Returns the string representation of the current game state.
   *
   * @return the model's string output
   */
  public String toString() {
    return this.model.toString();
  }

  /**
   * Renders the current game state to the output destination.
   *
   * @throws IllegalStateException if writing to the Appendable fails
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
   * Renders a custom message to the output destination.
   *
   * @param message the message to display
   * @throws IllegalStateException if writing to the Appendable fails
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