package sanguine.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a single cell for the Sanguine game.
 * Every cell keeps track of its list of pawns, a card, if there is one,
 * and the color of the player who currently owns the cell, if applicable.
 * Pawns can be added to the cell (up to 3) and be cleared.
 * However, once a card is placed in the cell, that is all that occupies the cell,
 * and it can't be removed from the cell.
 */
public class SanguineCell implements Cell {
  private final List<Pawn> pawns;
  private Card card;
  private Pawn owningColor;

  /**
   * Constructs the cell with no pawns, no card, and thus no color ownership.
   */
  public SanguineCell() {
    pawns = new ArrayList<>();
    card = null;
    owningColor = null;
  }

  /**
   * Constructs a starting cell, which has a singular pawn of the color indicated
   * in the argument. The starting cell has no card, and its color ownership is the color
   * of the pawn.
   *
   * @param color the color of the pawn to be added to the cell
   */
  public SanguineCell(Pawn color) throws IllegalArgumentException {
    if (color == null) {
      throw new IllegalArgumentException("Null color!\n");
    }

    pawns = new ArrayList<>();
    pawns.add(color);
    card = null;
    owningColor = color;
  }


  @Override
  public List<Pawn> getPawns() throws IllegalStateException {
    if (pawns.isEmpty()) {
      throw new IllegalStateException("No pawns in cell!\n");
    }
    return new ArrayList<>(pawns);
  }

  @Override
  public Card getCard() throws IllegalStateException {
    if (card == null) {
      throw new IllegalStateException("No card in cell!\n");
    }
    return new SanguineCard(card.getName(), card.getCost(), card.getValue(), card.getGrid());
  }

  @Override
  public Pawn getOwningColor() {
    return owningColor;
  }

  @Override
  public void addPawn(Pawn pawn) throws IllegalArgumentException, IllegalStateException {
    if (pawn == null) {
      throw new IllegalArgumentException("Pawn cannot be null!\n");
    }
    if (!pawns.isEmpty()) {
      if (!pawn.equals(pawns.getFirst())) {
        throw new IllegalArgumentException("Can't add pawn of the opposite color!\n");
      }
    }
    if (pawns.size() == 3) {
      throw new IllegalStateException("Cell already has maximum allowed pawns!\n");
    }
    if (card != null) {
      throw new IllegalStateException("Can't add a pawn to a cell with a card!\n");
    }
    pawns.add(pawn);
    owningColor = pawn;
  }

  @Override
  public void placeCard(Card card) throws IllegalArgumentException, IllegalStateException {
    if (card == null) {
      throw new IllegalArgumentException("Card cannot be null!\n");
    }
    if (this.card != null) {
      throw new IllegalStateException("The cell already has a card!\n");
    }
    if (pawns.size() < card.getCost()) {
      throw new IllegalStateException("Not enough pawns to cover the cost of the card!\n");
    }

    clearPawns();
    this.card = card;
  }

  @Override
  public boolean hasCard() {
    return card != null;
  }

  @Override
  public boolean isEmpty() {
    return pawns.isEmpty() && card == null;
  }

  @Override
  public int getPawnCount() {
    return pawns.size();
  }

  @Override
  public void clearPawns() {
    pawns.clear();
  }

  @Override
  public void convertPawns() throws IllegalStateException {
    if (pawns.isEmpty()) {
      throw new IllegalStateException("There are no pawns to convert!\n");
    }
    if (card != null) {
      throw new IllegalStateException("Can't convert pawns in a cell occupied by a card!\n");
    }

    for (int pawnIndex = 0; pawnIndex < pawns.size(); pawnIndex++) {
      if (pawns.getFirst().equals(Pawn.RED)) {
        pawns.set(pawnIndex, Pawn.BLUE);
      } else {
        pawns.set(pawnIndex, Pawn.RED);
      }
    }
    owningColor = owningColor == Pawn.RED ? Pawn.BLUE : Pawn.RED;
  }
}