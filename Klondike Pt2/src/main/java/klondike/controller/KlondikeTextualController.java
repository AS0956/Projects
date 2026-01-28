package klondike.controller;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import klondike.model.hw02.Card;
import klondike.model.hw02.KlondikeModel;
import klondike.view.KlondikeTextualView;

/**
 * Text-based controller for the Klondike game.
 * Handles user input and updates the view based on model state.
 */
public class KlondikeTextualController implements KlondikeController {
  private final Readable rd;
  private final Appendable ap;

  /**
   * Constructs a KlondikeTextualController with given Readable and Appendable.
   *
   * @param rd the source of input
   * @param ap the destination for output
   * @throws IllegalArgumentException if rd or ap is null
   */
  public KlondikeTextualController(Readable rd, Appendable ap) {
    if (rd == null || ap == null) {
      throw new IllegalArgumentException("Readable and Appendable cannot be null");
    }
    this.rd = rd;
    this.ap = ap;
  }

  @Override
  public <C extends Card> void playGame(KlondikeModel<C> model, List<C> deck,
                                        boolean shuffle, int numPiles, int numDraw) {
    if (model == null) {
      throw new IllegalArgumentException("Model cannot be null");
    }
    KlondikeTextualView view = new KlondikeTextualView(model, ap);
    Scanner sc = new Scanner(rd);

    try {
      model.startGame(deck, shuffle, numPiles, numDraw);
      renderState(view, model);
    } catch (Exception e) {
      e.printStackTrace();
      throw new IllegalStateException("Could not start game: " + e.getMessage());
    }

    boolean quit = false;

    while (!model.isGameOver() && !quit) {
      if (!sc.hasNext()) {
        throw new IllegalStateException("quit");
      }
      String cmd = sc.next();
      if (cmd.equalsIgnoreCase("q")) {
        quit = true;
        quitGame(view, model);
        return;
      }
      try {
        switch (cmd.toLowerCase()) {
          case "mpp":
            model.movePile(readInt(sc) - 1, readInt(sc), readInt(sc) - 1);
            break;
          case "md":
            model.moveDraw(readInt(sc) - 1);
            break;
          case "mpf":
            model.moveToFoundation(readInt(sc) - 1, readInt(sc) - 1);
            break;
          case "mdf":
            model.moveDrawToFoundation(readInt(sc) - 1);
            break;
          case "dd":
            model.discardDraw();
            break;
          default:
            ap.append("Invalid move. Play again.\n");
            continue;
        }
        renderState(view, model);
      } catch (IllegalStateException e) {
        if (e.getMessage().equals("quit")) {
          quit = true;
          quitGame(view, model);
          return;
        }
        try {
          ap.append("Invalid move. Play again.\n");
        } catch (IOException io) {
          throw new IllegalStateException("Output failed", io);
        }
      } catch (IllegalArgumentException e) {
        try {
          ap.append("Invalid move. Play again.\n");
        } catch (IOException io) {
          throw new IllegalStateException("Output failed", io);
        }
      } catch (IOException e) {
        throw new IllegalStateException("Output failed", e);
      }
    }

    // Game is over
    try {
      renderState(view, model);
      if (model.getScore() == 52) {
        ap.append("You win!\n");
      } else {
        ap.append("Game over. Score: " + model.getScore() + "\n");
      }
    } catch (IOException e) {
      throw new IllegalStateException("Failed to render final state", e);
    }
  }

  /**
   * Reads the next integer from the scanner.
   *
   * @param sc the Scanner
   * @return the parsed integer
   * @throws IllegalStateException if input is "q" or no more input is available
   */
  private int readInt(Scanner sc) {
    while (sc.hasNext()) {
      String s = sc.next();
      if (s.equalsIgnoreCase("q")) {
        throw new IllegalStateException("quit");
      }
      try {
        return Integer.parseInt(s);
      } catch (NumberFormatException ignored) {
        // Continue reading until we get a valid integer
      }
    }
    throw new IllegalStateException("quit");
  }

  /**
   * Renders the current state of the game.
   *
   * @param view the textual view
   * @param m    the model
   * @param <C>  card type
   */
  private <C extends Card> void renderState(KlondikeTextualView view, KlondikeModel<C> m) {
    try {
      view.render();
      ap.append("Score: " + m.getScore() + "\n");
    } catch (IOException e) {
      throw new IllegalStateException("Render failed", e);
    }
  }

  /**
   * Quits the game and prints the final state.
   *
   * @param view the textual view
   * @param m    the model
   * @param <C>  card type
   */
  private <C extends Card> void quitGame(KlondikeTextualView view, KlondikeModel<C> m) {
    try {
      ap.append("Game quit!\nState of game when quit:\n");
      view.render();
      ap.append("\n");
      ap.append("Score: " + m.getScore() + "\n");
    } catch (IOException e) {
      throw new IllegalStateException("Quit failed", e);
    }
  }
}