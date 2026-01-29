package sanguine.model;

/**
 * Represents a tile in the 5x5 influence grid for a card.
 * Each tile can either have no influence, influence, or
 * is just the card itself at the center of the grid.
 */
public enum Influence {
  NONE, INFLUENCE, CARD
}
