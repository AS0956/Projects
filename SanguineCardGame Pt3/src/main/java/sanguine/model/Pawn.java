package sanguine.model;

/**
 * Represents a pawn on the playing board, which can either be red or blue.
 */
public enum Pawn implements CellContent {
  RED, BLUE;

  /**
   * Returns a string friendly version of the color.
   *
   * @return a string representing the color (red or blue)
   */
  public String toString() {
    if (this == RED) {
      return "Red";
    } else {
      return "Blue";
    }
  }
}
