package sanguine;

import java.util.List;
import sanguine.controller.DeckReader;
import sanguine.controller.StubController;
import sanguine.model.BasicSanguine;
import sanguine.model.Card;
import sanguine.view.SanguineFrame;


/**
 * This class's purpose is to run the game.
 */
public final class SanguineGame {
  /**
   * This produces the ability to run the game.
   *
   * @param args the arguments
   */
  public static void main(String[] args) {
    List<Card> deck = DeckReader.readDeck("src/example_deck.txt");

    BasicSanguine model = new BasicSanguine();
    model.startGame(deck, deck, 5, 7, 5);
    model.drawCard();

    SanguineFrame view = new SanguineFrame(model);
    StubController controller = new StubController(model, view);
  }
}