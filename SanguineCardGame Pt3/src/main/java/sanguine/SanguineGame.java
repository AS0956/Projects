package sanguine;

import java.util.List;
import sanguine.controller.DeckReader;
import sanguine.controller.HumanPlayerActions;
import sanguine.controller.MachinePlayerActions;
import sanguine.controller.PlayerActions;
import sanguine.controller.SanguineController;
import sanguine.model.BasicSanguine;
import sanguine.model.Card;
import sanguine.model.Pawn;
import sanguine.strategy.ControlTheBoardStrategy;
import sanguine.strategy.FillFirstStrategy;
import sanguine.strategy.MaximizeRowScoreStrategy;
import sanguine.strategy.MinimaxStrategy;
import sanguine.strategy.PlayerStrategy;
import sanguine.view.SanguineFrame;

/**
 * This class's purpose is to run the game.
 */
public final class SanguineGame {
  /**
   * Runs the Sanguine game.
   *
   * <p></p>
   * Command-line arguments:
   * args[0] - number of rows (positive integer)
   * args[1] - number of columns (odd integer > 2)
   * args[2] - path to Red player's deck file
   * args[3] - path to Blue player's deck file
   * args[4] - Red player type ("human", "strategy1", "strategy2", "strategy3")
   * args[5] - Blue player type ("human", "strategy1", "strategy2", "strategy3")
   * Example: java -jar sanguine.jar 5 7 src/example_deck.txt src/example_deck.txt human strategy1
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args) {
    int numRows = 5;
    int numCols = 7;
    String redDeckPath = "src/example_deck.txt";
    String blueDeckPath = "src/example_deck.txt";
    String redPlayerType = "human";
    String bluePlayerType = "human";
    String errorMsg = "Usage: java -jar sanguine.jar <rows> <cols> "
        + "<red_deck> <blue_deck> <red_player> <blue_player>\n"
        + "Player types: human, strategy1, strategy2, strategy3";

    if (args.length == 6) {
      try {
        numRows = Integer.parseInt(args[0]);
        numCols = Integer.parseInt(args[1]);
        redDeckPath = args[2];
        blueDeckPath = args[3];
        redPlayerType = args[4];
        bluePlayerType = args[5];
      } catch (NumberFormatException e) {
        System.err.println("Invalid number format for rows or columns!");
        System.err.println(errorMsg);
        System.exit(1);
      }
    } else {
      System.err.println("Not enough arguments provided!");
      System.err.println(errorMsg);
      System.err.println("Using default values instead.");
    }

    // Read decks
    List<Card> redDeck;
    List<Card> blueDeck;
    try {
      redDeck = DeckReader.readDeck(redDeckPath);
      blueDeck = DeckReader.readDeck(blueDeckPath);
    } catch (IllegalArgumentException e) {
      System.err.println("Error reading deck files: " + e.getMessage());
      System.exit(1);
      return;
    }

    BasicSanguine model = new BasicSanguine();
    try {
      model.startGame(redDeck, blueDeck, numRows, numCols, 5);
    } catch (IllegalArgumentException | IllegalStateException e) {
      System.err.println("Error starting game: " + e.getMessage());
      System.exit(1);
      return;
    }

    SanguineFrame redView = new SanguineFrame(model, Pawn.RED);
    SanguineFrame blueView = new SanguineFrame(model, Pawn.BLUE);

    PlayerActions redPlayer = createPlayer(Pawn.RED, redPlayerType);
    PlayerActions bluePlayer = createPlayer(Pawn.BLUE, bluePlayerType);

    SanguineController redController = new SanguineController(model, redView, redPlayer);
    SanguineController blueController = new SanguineController(model, blueView, bluePlayer);

    model.beginGame();
  }

  /**
   * Creates a player based on the specified type.
   *
   * @param color the player's color
   * @param playerType the type of player ("human", "strategy1", "strategy2", "strategy3")
   * @return the created PlayerActions
   */
  private static PlayerActions createPlayer(Pawn color, String playerType) {
    if (playerType.equalsIgnoreCase("human")) {
      return new HumanPlayerActions(color);
    } else {
      // Create machine player with appropriate strategy
      PlayerStrategy strategy = getStrategy(playerType);
      return new MachinePlayerActions(color, strategy);
    }
  }

  /**
   * Gets a strategy based on the specified type.
   * strategy1 = FillFirstStrategy
   * strategy2 = MaximizeRowScoreStrategy
   * strategy3 = ControlTheBoardStrategy
   * strategy4 = MinimaxStrategy
   *
   * @param strategyType the type of strategy
   * @return the PlayerStrategy
   */
  private static PlayerStrategy getStrategy(String strategyType) {
    return switch (strategyType.toLowerCase()) {
      case "strategy1" -> new FillFirstStrategy();
      case "strategy2" -> new MaximizeRowScoreStrategy();
      case "strategy3" -> new ControlTheBoardStrategy();
      case "strategy4" -> new MinimaxStrategy();
      default -> {
        System.err.println("Unknown strategy: " + strategyType + ". Using FillFirstStrategy.");
        yield new FillFirstStrategy();
      }
    };
  }
}