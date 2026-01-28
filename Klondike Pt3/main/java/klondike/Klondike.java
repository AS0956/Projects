package klondike;

import java.io.InputStreamReader;
import klondike.controller.KlondikeController;
import klondike.controller.KlondikeTextualController;
import klondike.model.hw02.KlondikeModel;
import klondike.model.hw04.KlondikeCreator;
import klondike.model.hw04.KlondikeCreator.GameType;

/**
 * g.
 */
public final class Klondike {
  /**
   * y.
   *
   * @param args yty
   */
  public static void main(String[] args) {
    if (args.length == 0) {
      throw new IllegalArgumentException("Usage: klondike <game-type>");
    }

    GameType gameType;
    String gameTypeString = args[0].toLowerCase();

    if (gameTypeString.equals("basic")) {
      gameType = GameType.BASIC;
    } else if (gameTypeString.equals("whitehead")) {
      gameType = GameType.WHITEHEAD;
    } else {
      throw new IllegalArgumentException("Unknown game-type: " + args[0]);
    }

    int numPiles = 7;
    int numDraw = 3;

    try {
      if (args.length >= 2) {
        numPiles = Integer.parseInt(args[1]);
      }
      if (args.length >= 3) {
        numDraw = Integer.parseInt(args[2]);
      }
    } catch (NumberFormatException e) {
      System.err.println("Invalid number format, use default");
    }

    KlondikeModel model = KlondikeCreator.create(gameType);

    KlondikeController controller = new KlondikeTextualController(
        new InputStreamReader(System.in),
        System.out
    );

    try {
      controller.playGame(model, model.createNewDeck(), true, numPiles, numDraw);
    } catch (IllegalStateException e) {
      System.err.println(e.getMessage());
    }
  }
}