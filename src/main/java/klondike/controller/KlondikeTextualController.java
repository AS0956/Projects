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
   * Constructs a KlondikeTextualController with the given Readable and Appendable.
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

  /**
   * Plays text based game of Klondike using the
   * given model and deck.
   * reads the commands from input then
   * updates model and prints game state
   * supports moving cards between the piles, in foundation
   * and discarding draw cards, and quitting game.
   * The game loop contrinues until the game
   * is officially over.
   *
   * @param model    the Klondike model to use; must not be null
   * @param deck     the list of cards to start the game with
   * @param shuffle  whether to shuffle the deck before starting
   * @param numPiles the number of cascade piles to use
   * @param numDraw  the number of cards in the draw pile
   * @param <C> type of card used in the game
   */
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
      throw new IllegalStateException("Could not start game: " + e.getMessage());
    }

    while (!model.isGameOver()) {
      if (!sc.hasNext()) {
        throw new IllegalStateException("Ran out of input");
      }

      String cmd = sc.next();

      // Handles quitting anytime
      if (cmd.equalsIgnoreCase("q")) {
        quitGame(view, model);
        return;
      }

      boolean validMove = true;

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
            appendSafe("Invalid move. Play again.\n");
            validMove = false;
            break;
        }

        if (validMove) {
          renderState(view, model);
        }

      } catch (IllegalStateException e) {
        if ("quit".equals(e.getMessage())) {
          quitGame(view, model);
          return;
        }
        appendSafe("Invalid move. Play again.\n");
      } catch (IllegalArgumentException e) {
        appendSafe("Invalid move. Play again.\n");
      }
    }

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
   * Reads the next integer from scanner, skipping faulty input.
   * Throws if input runs out or user quits.
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
        //catches exception
      }
    }
    throw new IllegalStateException("Ran out of input");
  }

  /**
   * Renders the current state and score.
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
   * Prints final state and quits.
   */
  private <C extends Card> void quitGame(KlondikeTextualView view, KlondikeModel<C> m) {
    try {
      ap.append("Game quit!\nState of game when quit:\n");
      view.render();
      ap.append("Score: " + m.getScore() + "\n");
    } catch (IOException e) {
      throw new IllegalStateException("Quit failed", e);
    }
  }

  /**
   * Appends a message to the Appendable.
   */
  private void appendSafe(String msg) {
    try {
      ap.append(msg);
    } catch (IOException e) {
      throw new IllegalStateException("Output failed", e);
    }
  }
}