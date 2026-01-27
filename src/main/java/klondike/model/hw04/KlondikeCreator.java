package klondike.model.hw04;

import klondike.model.hw02.BasicKlondike;
import klondike.model.hw02.KlondikeModel;

/**
 * Factory class to create different types of Klondike games.
 */
public class KlondikeCreator {

  /**
   * Enum which represents various types of Klondike Games which can.
   * be created
   */
  public enum GameType {
    BASIC,
    WHITEHEAD
  }

  /**
   * Creates new Klondike game model of the type chosen.
   *
   * @param gameType gameType the type of game to create.
   * @return a new KlondikeModel of the chosen type
   * @throws IllegalArgumentException if gameType is null/invalid
   */
  public static KlondikeModel<?> create(GameType gameType) {
    switch (gameType) {
      case BASIC:
        return new BasicKlondike();
      case WHITEHEAD:
        return new WhiteheadKlondike();
      default:
        throw new IllegalArgumentException("Invalid game type");
    }
  }
}