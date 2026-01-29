package sanguine.model;

/**
 * Listener interface for observing status changes in the Sanguine game model.
 * Controllers register as listeners to be notified of turn changes and when game ends.
 */
public interface ModelStatusListener {
  /**
   * Called when the active player changes.
   */
  void onTurnChanged();

  /**
   * Called when the game ends.
   *
   * @param winner the winning player, or null if tie
   * @param redScore red player's final score
   * @param blueScore blue player's final score
   */
  void onGameOver(Pawn winner, int redScore, int blueScore);
}