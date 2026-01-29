package sanguine.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JPanel;
import sanguine.model.Card;
import sanguine.model.CellContent;
import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;

/**
 * Panel that displays the game board.
 */
public class BoardPanel extends JPanel {
  private final ReadOnlySanguineModel model;
  private int cellSize = 80; // tried 60 first but too small
  private int offsetX = 60; // space for row scores on left
  private int offsetY = 80; // space for "Current Player" text

  // for tracking which cell is selected
  private int selectedRow = -1;
  private int selectedCol = -1;

  /**
   * Initializes the board panel with the background color of white.
   *
   * @param model the game model
   */
  public BoardPanel(ReadOnlySanguineModel model) {
    this.model = model;
    setBackground(Color.WHITE);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    calculateCellSize();

    Graphics2D g2d = (Graphics2D) g;

    int numCols = model.getNumCols();

    int boardWidth = numCols * cellSize;

    offsetX = (getWidth() - boardWidth) / 2;
    offsetY = 80;

    drawCurrentPlayer(g2d);

    int numRows = model.getNumRows();

    // draw all cells
    for (int row = 0; row < numRows; row++) {
      for (int col = 0; col < numCols; col++) {
        drawCell(g2d, row, col);
      }
    }

    drawRowScores(g2d, numRows);
    drawTotalScores(g2d, numRows, numCols);
  }

  private void drawCurrentPlayer(Graphics2D g2d) {
    g2d.setFont(new Font("Times New Roman", Font.BOLD, 24));
    try {
      Pawn currentPlayer = model.getTurn();

      // set color based on which player
      if (currentPlayer == Pawn.RED) {
        g2d.setColor(Color.RED);
      } else {
        g2d.setColor(Color.BLUE);
      }

      String text = "Current Player: " + currentPlayer;
      int stringWidth = g2d.getFontMetrics().stringWidth(text);
      int centeredX = (getWidth() - stringWidth) / 2;

      g2d.drawString(text, centeredX, 50);

    } catch (IllegalStateException e) {
      // the game would be over
      g2d.setColor(Color.BLACK);

      String text = "Game Over!";
      int stringWidth = g2d.getFontMetrics().stringWidth(text);
      int centeredX = (getWidth() - stringWidth) / 2;

      g2d.drawString(text, centeredX, 50);
    }
  }

  private void drawCell(Graphics2D g2d, int row, int col) {
    Pawn owner = model.getCellOwnership(row, col);

    // pick color based on who owns the cell
    Color bgColor;
    if (owner == Pawn.RED) {
      bgColor = new Color(255, 200, 200); // light red
    } else if (owner == Pawn.BLUE) {
      bgColor = new Color(200, 200, 255); // light blue
    } else {
      bgColor = Color.LIGHT_GRAY; // no owner
    }

    // highlight the selected cell
    if (selectedRow == row && selectedCol == col) {
      bgColor = Color.CYAN;
    }

    // calculate position
    int x = offsetX + col * cellSize;
    int y = offsetY + row * cellSize;

    // draw the cell
    g2d.setColor(bgColor);
    g2d.fillRect(x, y, cellSize, cellSize);

    // draw border
    g2d.setColor(Color.BLACK);
    g2d.drawRect(x, y, cellSize, cellSize);

    List<CellContent> contents = model.getCellContent(row, col);

    // draw what's inside the cell
    if (!contents.isEmpty()) {
      CellContent content = contents.getFirst();

      if (content instanceof Card card) {
        // cell has a card
        g2d.setFont(new Font("Times New Roman", Font.BOLD, 12));
        g2d.setColor(Color.BLACK);
        g2d.drawString(card.getName(), x + 5, y + 15);

        g2d.setFont(new Font("Times New Roman", Font.BOLD, 16));
        g2d.drawString("Value: " + card.getValue(), x + 5, y + 35);
      } else if (content instanceof Pawn) {
        int pawnCount = contents.size();
        g2d.setFont(new Font("Times New Roman", Font.BOLD, 32));

        // color the number based on who owns it
        if (owner == Pawn.RED) {
          g2d.setColor(Color.RED);
        } else {
          g2d.setColor(Color.BLUE);
        }

        // center the number in the cell
        String pawnStr = String.valueOf(pawnCount);
        int strWidth = g2d.getFontMetrics().stringWidth(pawnStr);
        g2d.drawString(pawnStr, x + (cellSize - strWidth) / 2, y + cellSize / 2 + 10);
      }
    }
  }

  // draws the row scores on left and right sides
  private void drawRowScores(Graphics2D g2d, int numRows) {
    Font font = new Font("Times New Roman", Font.BOLD, 14);
    FontMetrics metrics = g2d.getFontMetrics(font);
    g2d.setFont(font);

    for (int row = 0; row < numRows; row++) {
      int y = offsetY + row * cellSize + cellSize / 2 + 5;

      // red score on left
      String red = "R:" + model.getRedRowScore(row);
      g2d.setColor(Color.RED);
      g2d.drawString(red, offsetX - metrics.stringWidth(red) - 20, y);

      // blue score on right
      int rightX = offsetX + model.getNumCols() * cellSize + 20;
      g2d.setColor(Color.BLUE);
      g2d.drawString("B:" + model.getBlueRowScore(row), rightX, y);
    }
  }

  // draws total scores at the bottom
  private void drawTotalScores(Graphics2D g2d, int numRows, int numCols) {
    int y = offsetY + numRows * cellSize + 30;
    Font font = new Font("Times New Roman", Font.BOLD, 16);
    FontMetrics metrics = g2d.getFontMetrics(font);
    g2d.setFont(font);

    g2d.setColor(Color.RED);
    g2d.drawString("Red Total: " + model.getRedScore(), offsetX, y);

    g2d.setColor(Color.BLUE);
    g2d.drawString("Blue Total: " + model.getBlueScore(),
        offsetX + numCols * cellSize
            - (metrics.stringWidth("Blue Total: " + model.getBlueScore())), y);
  }

  @Override
  public Dimension getPreferredSize() {
    // calculate size based on board dimensions
    int width = offsetX * 2 + model.getNumCols() * cellSize + 35;
    int height = offsetY * 2 + model.getNumRows() * cellSize + 50;
    return new Dimension(width, height);
  }

  /**
   * Adds click handling to the board.
   *
   * @param features the controller to notify of clicks
   */
  public void addFeatures(Features features) {
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (!features.canSelect()) {
          return;
        }

        // convert pixel coordinates to grid coordinates
        int col = (e.getX() - offsetX) / cellSize;
        int row = (e.getY() - offsetY) / cellSize;

        // only do something if click is inside the board
        if (row >= 0 && row < model.getNumRows() && col >= 0 && col < model.getNumCols()) {

          // if clicking same cell, deselect it
          if (selectedRow == row && selectedCol == col) {
            selectedRow = -1;
            selectedCol = -1;
          } else {
            // otherwise select this cell
            selectedRow = row;
            selectedCol = col;
          }

          // tell the controller
          features.onCellSelected(row, col);
          repaint(); // redraw to show selection
        }
      }
    });
  }

  /**
   * Unhighlights the selected cell at the selected row and column.
   */
  public void clearSelection() {
    this.selectedRow = -1;
    this.selectedCol = -1;
    repaint();
  }

  /**
   * Calculates cell size based on panel dimensions.
   * Keeps cells square and between 40-100 pixels.
   */
  private void calculateCellSize() {
    int numRows = model.getNumRows();
    int numCols = model.getNumCols();

    int availableWidth = getWidth() - (offsetX * 2);
    int availableHeight = getHeight() - offsetY - 100;

    int maxCellWidth = availableWidth / numCols;
    int maxCellHeight = availableHeight / numRows;

    cellSize = Math.min(maxCellWidth, maxCellHeight);
    cellSize = Math.max(40, Math.min(cellSize, 100));
  }
}