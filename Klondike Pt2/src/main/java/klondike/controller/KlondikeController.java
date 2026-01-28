package klondike.controller;

import java.util.List;
import klondike.model.hw02.Card;
import klondike.model.hw02.KlondikeModel;

/**
 * Controller interface for a text-based Klondike game.
 * The controller handles user input and communicates with the model to run the game.
 */
public interface KlondikeController {

  /**
   * Plays a game of Klondike using the given model and deck.
   * The controller should handle input/output and quit requests, and
   * transmit the game state to the user via a view.
   *
   * @param model the Klondike model to use; must not be null
   * @param deck the list of cards to start the game with
   * @param shuffle whether to shuffle the deck before starting
   * @param numPiles the number of cascade piles to use
   * @param numDraw the number of cards in the draw pile
   * @param <C> the type of Card used in the model
   * @throws IllegalArgumentException if the model is null or arguments are invalid
   * @throws IllegalStateException if the controller cannot receive input or transmit output,
   *                               or if the game cannot be started
   */
  <C extends Card> void playGame(
      KlondikeModel<C> model,
      List<C> deck,
      boolean shuffle,
      int numPiles,
      int numDraw
  ) throws IllegalStateException, IllegalArgumentException;
}
