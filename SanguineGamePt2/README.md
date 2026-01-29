Overview:

This codebase implements the logic behind playing a game called "Sanguine". It
manages the game state, rules (what the player can do), and how the elements of the
game (like a card and a cell on the board) should interact with each other. It is
assumed that the player knows how to play the game (the rules).

Quick Start:

List<Card> redDeck = DeckReader.readDeck("docs/deck1.deck");
List<Card> blueDeck = DeckReader.readDeck("docs/deck2.deck");
SanguineGame model = new BasicSanguine();
model.startGame(redDeck, blueDeck, 3, 5, 5);
View view = new TextualView();
view.render(model);

This creates a deck for each player by reading from two different deck files, then
creates and starts the game through the model by passing in the two decks, a number
of rows and columns for the desired board size, as well as the number of cards
that should be in each player's hand. Creating the view and using the render method
allows visibility of what the current game state looks like.

The source code is organized into MVC style, plus a separate package containing the
strategies.

The board in the game goes by row first, then column.

Components:

Controller: The controller currently only has one class, the DeckReader, which only
has one method to read a file that's passed in and return a list of Card(s),
representing a deck. This is shown in the "Quick Start" snippet of code where we are
reading from two files to get the decks for each player.

Model: This folder contains the driving forces of the whole game, including all the
objects needed to play the game.

- SanguineCard: This class implements the Card interface. Every SanguineCard has a
  name, cost, value, and grid. This class only has getter methods so we can access
  a copy of each element.

- SanguineCell: This class implements the Cell interface. You are able to access
  the elements (pawns, card, color) of the cell through getter methods. You can
  also manipulate the cell by adding pawns or a card to it, and converting the pawns
  to the opposing color.

- SanguinePlayer: This class implements the Player interface. Each player has a
  deck, hand, and list of scores for each row on the game board. There are getter
  methods to access these fields. There are also methods to deal out the hand, which
  is used at the start of the game, or play or draw a card.

- BasicSanguine: This class implements the SanguineGame interface. This is where
  all the objects come together to make the game work. Start the game here and play
  the cards through this object. The game keeps track of whose turn it is. There
  are methods to handle actions like passing or playing a card. There are also
  methods to check whose turn it is, if the game is over, and who the winner is.
  There are also getter methods to get a copy of the current game board, the players,
  or their scores.

View: The view is driven by the model. Based on what the model is, the view will
output different games because the model creates the board.

- TextualView: This class implements the View interface. It only has one method,
  render, which takes in a game (of type SanguineGame) and outputs the current
  state of the game, aka the board.

Main (Sanguine): The main currently runs according to the instructions given by
the assignment. It reads in the arguments (args) and looks for a deck file path.
This creates the deck given to both players. Then, it automatically plays a 3x5
game through a while loop, outputting the game state after every move.

Changes made in Part Two:

We thought the numHands variable was the maximum number of cards allowed in a
player's hand at once, but realized it should only mean the starting variable of
each player's hand.

- Changed drawCard() method in SanguinePlayer by removing the check for if the
  hand is full.

Added and changed some getter methods because the assignment mentioned some that
we didn't include:

- getNumRows()
- getNumCols()
- getCellContents(int row, int col)
    - For this method, we had to create a new interface, CellContent,
      so our pawns and cards, which will implement and extend that interface,
      respectfully, can both be considered a valid return type for this method.
- Changed getRedPlayer() and getBluePlayer() to getRedPlayerHand() and
  getBluePlayerHand(), a bit more specific because their hand is all the view needs
  to show.
- getCellOwnership(int row, int col)
- isLegal(int row, int col, int handIndex)
    - For this method, we had to refactor our placeCard(int row, int col, int handIndex)
      method.
- Changed getRedScores() and getBlueScores() to getRedRowScore(int index) and
  getBlueRowScore(int index) to just get the score at the specified row.
- getRedScore()
- getBlueScore()

Abstracted some methods where the only difference was the red or blue player.

Slight change was made to the textual view because we changed one of the getter methods.
- getRedScores().get(row) -> getRedRowScore(row)
- getBlueScores().get(row) -> getBlueRowScore(row)

Tested the newly added and implemented methods mentioned above in BasicSanguineTest.

IMPORTANT KEYS:
The 'p' keybind correlates to pass.
The enter keybind correlates to confirming a move.

New Classes:

Controller:
    - The controller package has a Player interface and SanguinePlayer implementation
      of it, which can basically only 'play' (get a move).
    - The stub controller only prints the coordinates and key pressed actions for now.

Model:
    - The model has a new ReadOnlySanguineModel, which only includes observer methods,
      and strictly have no mutator methods for the purpose of preventing any player
      from cheating.

View:
    - The BoardPanel class is responsible to painting the board, row scores, current
      player, and total scores for each player on the GUI. It also handles the mouse
      clicks on a cell by highlighting it or unhighlighting it.
    - The HandPanel class is responsible for painting the hand (the current player's
      cards in hand) on the GUI. It also handles the mouse clicks on a card by 
      highlighting it or unhighlighting it.
    - The SanguineFrame class puts the BoardPanel and HandPanel together. It also
      handles the key pressed actions such as 'p' and enter.
    - The Features interface indicates all the actions the controller should be able
      to handle when it occurs, by delegating to the view (for now).

Strategy (Found in the strategy package in the src/main folder):
    - The FillFirstStrategy class implements a strategy that plays a move at the first
      legal available cell that the player can play in.
    - The MaximizeRowScoreStrategy class implements a strategy that plays a move based
      on getting a row score to have more points than the enemy's row score at that row.
    - The ControlTheBoardStrategy class implements a strategy that plays a move that 
      gives the player the most ownership of cells on the board.
    - The MinimaxStrategy class implements a strategy such that the move minimizes the
      opponents best move.
        - For this, we had to create a SimulatedBoard class that implemented the
          ReadOnlySanguineModel interface.
    - The CombinedStrategies class takes in a list of strategies above, and uses
      them together, with each following strategy in the list being a fallback.
      In the end, if none of those strategies produce a playing move, it's a pass.

The tests for these strategies are found in the tests folder (where all the other
tests are located, and are named accordingly.

To run .jar:
Copy the JAR file (f25-hw06-group-Christina-Ankita.main.jar) to a folder of your choice (e.g., TestJar)

Copy the following file from the project to the same folder:
src/example_deck.txt → keep this file inside a src folder in your new folder

Your folder structure should look like this:
TestJar/
├── f25-hw06-group-Christina-Ankita.main.jar
├── src/
│   └── example_deck.txt

Then type 'java -jar f25-hw06-group-Christina-Ankita.main.jar' in terminal to run.