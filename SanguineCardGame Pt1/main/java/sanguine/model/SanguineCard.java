package sanguine.model;

import java.util.Arrays;

/**
 * Represents a card for the Sanguine game.
 * Two cards are equal to each other if they have
 * the same name, cost, value, and influence grid.
 */
public class SanguineCard implements Card {
  private final String name;
  private final int cost;
  private final int value;
  private final Influence[][] grid;

  /**
   * Initializes the card with the given arguments.
   *
   * @param name  the name of the card
   * @param cost  the amount of pawns need to utilize the card
   * @param value the value of the card
   * @param grid  a 5x5 array of Influence,
   *              representing the influence the card has on the board
   */
  public SanguineCard(String name, int cost, int value, Influence[][] grid) {
    if (name == null) {
      throw new IllegalArgumentException("Can't have a null name!\n");
    } else if (cost < 1 || value < 1) {
      throw new IllegalArgumentException("The cost and value of the card has to be positive!\n");
    } else if (grid == null || grid.length != 5) {
      throw new IllegalArgumentException("Invalid grid!\n");
    }
    for (int row = 0; row < 5; row++) {
      if (grid[row].length != 5) {
        throw new IllegalArgumentException("Invalid grid!\n");
      }
      for (int col = 0; col < 5; col++) {
        if (grid[row][col] == null) {
          throw new IllegalArgumentException("Null tile in grid!\n");
        }
      }
    }
    this.name = name;
    this.cost = cost;
    this.value = value;
    this.grid = grid;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public int getCost() {
    return this.cost;
  }

  @Override
  public int getValue() {
    return this.value;
  }

  @Override
  public Influence[][] getGrid() {
    return this.grid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof Card that) {
      return (this.name.equals(that.getName()) && (this.cost == that.getCost())
          && (this.value == that.getValue()) && (Arrays.deepEquals(this.grid, that.getGrid())));
    }
    return false;
  }
}
