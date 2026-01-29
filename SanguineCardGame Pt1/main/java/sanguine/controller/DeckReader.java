package sanguine.controller;

import sanguine.model.Card;
import sanguine.model.Influence;
import sanguine.model.SanguineCard;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Reads deck configuration files and creates a list of cards.
 */
public class DeckReader {
  /**
   * Reads a deck configuration file and translates it into a deck of cards.
   *
   * @param filePath the file path to read from
   * @return a deck of cards as a list of Card
   * @throws IllegalArgumentException if the file isn't formatted properly
   */
  public static List<Card> readDeck(String filePath) throws IllegalArgumentException {
    File file = new File(filePath);
    List<Card> deck = new ArrayList<>();

    try {
      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        String cardLine = scanner.nextLine().trim();

        if (cardLine.isEmpty()) {
          continue;
        }

        String[] parts = cardLine.split(" ");
        if (parts.length != 3) {
          throw new IllegalArgumentException("Invalid card format!\n");
        }

        String name = parts[0];
        int cost = Integer.parseInt(parts[1]);
        int value = Integer.parseInt(parts[2]);

        Influence[][] grid = new Influence[5][5];
        readGrid(scanner, grid);
        deck.add(new SanguineCard(name, cost, value, grid));
      }
      scanner.close();
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException("File not found!\n");
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid number format!\n");
    }

    return deck;
  }

  private static void readGrid(Scanner scanner, Influence[][] grid) {
    for (int row = 0; row < 5; row++) {
      if (!scanner.hasNextLine()) {
        throw new IllegalArgumentException("Incomplete card data!\n");
      }
      String rowLine = scanner.nextLine().trim();
      if (rowLine.length() != 5) {
        throw new IllegalArgumentException("Invalid grid row!\n");
      }
      for (int col = 0; col < 5; col++) {
        char c = rowLine.charAt(col);
        if (c == 'X') {
          grid[row][col] = Influence.NONE;
        } else if (c == 'I') {
          grid[row][col] = Influence.INFLUENCE;
        } else if (c == 'C') {
          grid[row][col] = Influence.CARD;
        } else {
          throw new IllegalArgumentException("Invalid character in grid!\n");
        }
      }
    }
  }
}

