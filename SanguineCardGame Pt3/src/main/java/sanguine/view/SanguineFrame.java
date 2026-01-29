package sanguine.view;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;

/**
 * Creates GUI view.
 * This overall frame deals with keyboard inputs as well.
 */
public class SanguineFrame extends JFrame {
  private final BoardPanel boardPanel;
  private final HandPanel handPanel;

  /**
   * Composes with the board and hand panel.
   *
   * @param model the game model
   */
  public SanguineFrame(ReadOnlySanguineModel model, Pawn myColor) {
    super("Sanguine Game");

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(true);
    setLayout(new BorderLayout(10, 10));

    // creates the board and hand panels
    boardPanel = new BoardPanel(model);
    handPanel = new HandPanel(model, myColor);

    // adds board to the center, hand to the bottom
    add(boardPanel, BorderLayout.CENTER);
    add(handPanel, BorderLayout.SOUTH);

    pack(); //size the window in order to fit everything
    setLocationRelativeTo(null); //center on screen
  }

  /**
   * Sets the frame view to be visible.
   */
  public void display() {
    setVisible(true);
  }

  /**
   * Connects the controller so it can handle any clicks or key presses.
   *
   * @param feature the feature (controller) to add as the listener
   */
  public void addFeatures(Features feature) {
    boardPanel.addFeatures(feature);
    handPanel.addFeatures(feature);

    //handle keyboard input
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          feature.onConfirmMove();
        } else if (e.getKeyCode() == KeyEvent.VK_P) {
          feature.onPass();
        }
      }
    });
  }

  /**
   * Clears the selected cell and card.
   * Then, refreshes the view.
   * This is called after the controller confirms a move.
   */
  public void clearSelections() {
    boardPanel.clearSelection();
    handPanel.clearSelection();
    boardPanel.repaint();
    handPanel.repaint();
  }
}