package sanguine.view;

import sanguine.model.Cell;
import sanguine.model.Pawn;
import sanguine.model.SanguineModel;

/**
 * A textual view for the Sanguine game that renders the board as text.
 */
public class TextualView implements View {

  @Override
  public String render(SanguineModel game) throws IllegalArgumentException {
    if (game == null) {
      throw new IllegalArgumentException("Game cant be null!\n");
    }

    Cell[][] board = game.getBoard();

    StringBuilder output = new StringBuilder();

    for (int row = 0; row < board.length; row++) {
      // The left side should print the red player's score.
      output.append(game.getRedRowScore(row)).append(' ');

      // For each column, print the appropriate character (R/B), number (1-3), or '_' if empty.
      // This is done through the getCellString helper method which identifies which to print.
      for (int col = 0; col < board[row].length; col++) {
        output.append(getCellString(board[row][col]));
      }

      // The right side should print the blue player's score.
      output.append(' ').append(game.getBlueRowScore(row));

      // Move to the next line, unless it's the last row of the board, then we don't need to.
      if (row < board.length - 1) {
        output.append('\n');
      }
    }

    // Print the whole string representing the game board.
    return output.toString();
  }

  private String getCellString(Cell cell) {
    if (cell.hasCard()) {
      if (cell.getOwningColor() == Pawn.RED) {
        return "R";
      } else {
        return "B";
      }
    } else if (cell.isEmpty()) {
      return "_";
    } else {
      return String.valueOf(cell.getPawnCount());
    }
  }
}