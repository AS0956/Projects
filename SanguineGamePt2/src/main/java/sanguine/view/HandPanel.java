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
import sanguine.model.Influence;
import sanguine.model.Pawn;
import sanguine.model.ReadOnlySanguineModel;

/**
 * Panel that displays the current player's hand.
 */
public class HandPanel extends JPanel {
  private final ReadOnlySanguineModel model;
  private final int cardWidth = 120;
  private final int cardHeight = 160;  // Made taller to fit all info
  private final int cardSpacing = 10;
  private final int offsetX = 20;
  private final int offsetY = 10;
  private int selectedCardIndex = -1;

  /**
   * Initializes the hand panel with the background color of dark gray.
   *
   * @param model the game model
   */
  public HandPanel(ReadOnlySanguineModel model) {
    this.model = model;
    setBackground(Color.DARK_GRAY);
  }

  private List<Card> getCurrentPlayerHand() {
    try {
      Pawn currentPlayer = model.getTurn();
      return currentPlayer == Pawn.RED ? model.getRedPlayerHand() : model.getBluePlayerHand();
    } catch (IllegalStateException e) {
      return List.of();
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;

    List<Card> hand = getCurrentPlayerHand();

    for (int i = 0; i < hand.size(); i++) {
      drawCard(g2d, hand.get(i), i);
    }
  }

  private void drawCard(Graphics2D g2d, Card card, int index) {
    int x = offsetX + index * (cardWidth + cardSpacing);
    int y = offsetY;

    if (selectedCardIndex == index) {
      g2d.setColor(Color.CYAN);
    } else {
      g2d.setColor(Color.WHITE);
    }
    g2d.fillRect(x, y, cardWidth, cardHeight);

    g2d.setColor(Color.BLACK);
    g2d.drawRect(x, y, cardWidth, cardHeight);

    // Draw card information
    Font largeFont = new Font("Times New Roman", Font.BOLD, 18);
    g2d.setColor(Color.BLACK);
    g2d.setFont(largeFont);
    FontMetrics metrics = g2d.getFontMetrics(largeFont);
    int stringPosX = x + ((cardWidth - metrics.stringWidth(card.getName())) / 2);
    int stringPosY = y + metrics.getHeight();
    g2d.drawString(card.getName(), stringPosX, stringPosY);

    Font smallFont = new Font("Times New Roman", Font.PLAIN, 14);
    g2d.setFont(smallFont);
    metrics = g2d.getFontMetrics(smallFont);
    stringPosX = x + ((cardWidth - metrics.stringWidth("Cost: " + card.getCost())) / 2);
    stringPosY = y + metrics.getHeight() * 2;
    g2d.drawString("Cost: " + card.getCost(), stringPosX, stringPosY);

    stringPosX = x + ((cardWidth - metrics.stringWidth("Value: " + card.getValue())) / 2);
    stringPosY = y + metrics.getHeight() * 3;
    g2d.drawString("Value: " + card.getValue(), stringPosX, stringPosY);

    // Draw influence grid
    int gridSize = 14;
    int gridStartX = x + (cardWidth - 5 * gridSize) / 2;
    int gridStartY = y + 70;

    Influence[][] grid = card.getGrid();
    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        int gx = gridStartX + j * gridSize;
        int gy = gridStartY + i * gridSize;

        if (grid[i][j] == Influence.CARD) {
          g2d.setColor(Color.YELLOW);
        } else if (grid[i][j] == Influence.INFLUENCE) {
          g2d.setColor(Color.GREEN);
        } else {
          g2d.setColor(Color.LIGHT_GRAY);
        }

        g2d.fillRect(gx, gy, gridSize - 1, gridSize - 1);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(gx, gy, gridSize - 1, gridSize - 1);
      }
    }
  }

  @Override
  public Dimension getPreferredSize() {
    List<Card> hand = getCurrentPlayerHand();
    int width = offsetX + hand.size() * (cardWidth + cardSpacing);
    int height = cardHeight + offsetY * 2;
    return new Dimension(width, height);
  }

  /**
   * Adds click handling to the hand.
   *
   * @param features the controller to notify of clicks
   */
  public void addFeatures(Features features) {
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        List<Card> hand = getCurrentPlayerHand();

        // check each card to see if it was clicked
        for (int i = 0; i < hand.size(); i++) {
          int x = offsetX + i * (cardWidth + cardSpacing);
          int y = offsetY;

          // is the click inside this card?
          if (e.getX() >= x && e.getX() <= x + cardWidth
              && e.getY() >= y && e.getY() <= y + cardHeight) {

            // if clicking same card, deselect it
            if (selectedCardIndex == i) {
              selectedCardIndex = -1;
            } else {
              // otherwise select this card
              selectedCardIndex = i;
            }

            // tell the controller which card was clicked
            features.onCardSelected(i);
            repaint(); // redraw to show selection
          }
        }
      }
    });
  }
}