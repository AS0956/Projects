import sanguine.controller.DeckReader;
import java.io.File;
import java.util.List;
import sanguine.model.BasicSanguine;
import sanguine.model.Card;
import sanguine.model.Cell;
import sanguine.model.Pawn;
import sanguine.model.SanguineModel;
import sanguine.view.TextualView;
import sanguine.view.View;

/**
 * Main class to run Sanguine game.
 * Reads a deck configuration file (which was given)
 * and plays a game on a 3x5 board.
 */
public class Sanguine {
  /**
   * Main method to start and play the Sanguine game.
   * Reads a deck from file, then initializes the game,
   * then plays automatically, outputting the correct winner.
   *
   * @param args the command line arguments, where args[0] is the path to the deck file
   */
  public static void main(String[] args) {

    if (args.length < 1) {
      System.out.println("Provide a deck configuration file path.\n");
      return;
    }

    String filePath = args[0];
    File file = new File(filePath);

    if (!file.exists()) {
      System.out.println("File doesn't exist.\n");
      return;
    }

    List<Card> deck;
    try {
      deck = DeckReader.readDeck(filePath);
    } catch (IllegalArgumentException e) {
      System.out.println("Error reading deck file: " + e.getMessage() + "\n");
      return;
    }

    if (deck.size() < 15) {
      System.out.println("Not enough cards for a 3x5 board.\n");
      return;
    }
    SanguineModel game = new BasicSanguine();
    game.startGame(deck, deck, 3, 5, 5);

    View view = new TextualView();

    playGame(game, view);
  }

  /**
   * Plays game continuously until it ends.
   * Prints the board after every move.
   *
   * @param game the model to play the game with
   * @param view the view to render the game
   */
  private static void playGame(SanguineModel game, View view) {
    while (!game.isGameOver()) {
      System.out.println(view.render(game));
      System.out.println();

      boolean cardPlaced = tryPlaceCard(game);

      if (!cardPlaced) {
        game.pass();
      }
    }

    System.out.println(view.render(game));
    System.out.println();

    Pawn winner = game.getWinner();
    if (winner == null) {
      System.out.println("Game ended in a tie!");
    } else {
      System.out.println("Winner: " + winner);
    }
  }

  /**
   * Tries to place card from current players hand.
   * Tries all positions on board for each card in hand
   *
   * @param game the game state
   * @return true if a card was place, false otherwise
   */
  private static boolean tryPlaceCard(SanguineModel game) {
    Cell[][] board = game.getBoard();

    for (int handIndex = 0; handIndex < 5; handIndex++) {
      for (int row = 0; row < board.length; row++) {
        for (int col = 0; col < board[row].length; col++) {
          try {
            game.placeCard(row, col, handIndex);
            return true;
          } catch (IllegalArgumentException | IllegalStateException ignored) {
            // If any exceptions are thrown, we move on to check if the next possibility
            // can be played, so this is ignored.
          }
        }
      }
    }
    return false;
  }
}