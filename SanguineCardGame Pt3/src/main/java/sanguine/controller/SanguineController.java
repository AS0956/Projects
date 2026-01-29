package sanguine.controller;

import javax.swing.JOptionPane;
import sanguine.model.ModelStatusListener;
import sanguine.model.Pawn;
import sanguine.model.SanguineModel;
import sanguine.view.Features;
import sanguine.view.SanguineFrame;

/**
 * Controller for a single player in Sanguine.
 * Delegates between model, view, and player.
 */
public class SanguineController implements Features, ModelStatusListener {

  private final SanguineModel model;
  private final SanguineFrame view;
  private final PlayerActions player;
  private final Pawn myColor;
  private boolean isMyTurn;
  private int row;
  private int col;
  private int handIndex;

  /**
   * Creates a controller for one player.
   *
   * @param model   the game model
   * @param view    the view for this player
   * @param player this controller's player
   */
  public SanguineController(SanguineModel model, SanguineFrame view, PlayerActions player) {
    this.model = model;
    this.view = view;
    this.player = player;
    this.myColor = player.getColor();
    isMyTurn = myColor.equals(Pawn.RED);
    row = -1;
    col = -1;
    handIndex = -1;

    model.addModelStatusListener(this);
    view.addFeatures(this);
    view.display();
  }

  @Override
  public void onCardSelected(int cardIndex) {
    if (isMyTurn && player instanceof HumanPlayerActions) {
      try {
        Pawn currentPlayer = model.getTurn();
        System.out.printf("Player %s selected the card at index %d!\n",
            currentPlayer.toString(), cardIndex);
        handIndex = cardIndex;
      } catch (IllegalStateException e) {
        System.out.println("Error selecting card!\n");
      }
    }
  }

  @Override
  public void onCellSelected(int row, int col) {
    if (isMyTurn && player instanceof HumanPlayerActions) {
      System.out.println("Cell was selected at row " + row + " and column " + col + "!\n");
      this.row = row;
      this.col = col;
    }
  }

  @Override
  public void onConfirmMove() {
    if (isMyTurn && player instanceof HumanPlayerActions) {
      try {
        if (handIndex == -1 || row == -1 || col == -1) {
          JOptionPane.showMessageDialog(view,
              "Please select both a card and a cell before confirming!",
              "Selection Error",
              JOptionPane.ERROR_MESSAGE);
          return;
        }
        model.placeCard(row, col, handIndex);
        view.clearSelections();

        view.repaint();

        System.out.println("Move confirmed!\n");
      } catch (IllegalStateException | IllegalArgumentException e) {
        JOptionPane.showMessageDialog(view, e.getMessage(), "Invalid Move",
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  @Override
  public void onPass() {
    if (isMyTurn && player instanceof HumanPlayerActions) {
      try {
        model.pass();
        view.clearSelections();
        view.repaint();

        System.out.println("Turn passed!\n");
      } catch (IllegalStateException e) {
        JOptionPane.showMessageDialog(view, e.getMessage(), "Cannot Pass",
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  @Override
  public void onTurnChanged() {
    if (model.isGameOver()) {
      return;
    }

    isMyTurn = model.getTurn().equals(myColor);

    if (isMyTurn) {
      model.drawCard();
      System.out.printf("It's %s's turn!%n", myColor);
      view.setTitle("Sanguine - " + myColor + "'s Turn");
      handleMachineTurn();
    } else {
      view.setTitle("Sanguine - Waiting...");
    }

    view.repaint();
  }

  @Override
  public void onGameOver(Pawn winner, int redScore, int blueScore) {
    String message;
    if (winner == null) {
      message = String.format("Game Over - It's a Tie!%nRed: %d, Blue: %d",
          redScore, blueScore);
    } else {
      message = String.format("Game Over - %s Wins!%nRed: %d, Blue: %d",
          winner, redScore, blueScore);
    }

    JOptionPane.showMessageDialog(view, message, "Game Over!",
        JOptionPane.INFORMATION_MESSAGE);

    System.out.println(message);
    view.setTitle("Sanguine - Game Over");
    view.repaint();
  }

  @Override
  public boolean canSelect() {
    return isMyTurn && player instanceof HumanPlayerActions;
  }

  private void handleMachineTurn() {
    if (player instanceof MachinePlayerActions machine) {
      view.repaint();
      int chosenRow = machine.chooseRow(model);
      int chosenCol = machine.chooseCol(model);
      int chosenIndex = machine.chooseHandIndex(model);

      try {
        if (chosenRow == -1 || chosenCol == -1 || chosenIndex == -1) {
          model.pass();
          System.out.printf("Machine (%s) passes.\n", myColor);
        } else {
          model.placeCard(chosenRow, chosenCol, chosenIndex);
          System.out.printf("Machine (%s) placed card index %d at (%d,%d).\n",
              myColor, chosenIndex, chosenRow, chosenCol);
        }
        machine.clearMove();
        view.repaint();
      } catch (IllegalArgumentException | IllegalStateException e) {
        System.out.println("Machine attempted invalid move: " + e.getMessage());
      }
    }
  }
}