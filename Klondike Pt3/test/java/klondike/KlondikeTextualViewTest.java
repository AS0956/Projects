package klondike;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import klondike.controller.KlondikeTextualController;
import klondike.mock.MockKlondikeModel;
import klondike.view.KlondikeTextualView;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for KlondikeTextualView and controller integration using MockKlondikeModel.
 * Covers normal behavior and edge cases.
 */
public class KlondikeTextualViewTest {

  private MockKlondikeModel mockModel;
  private StringWriter output;

  /**
   * Setup for mockmodel.
   */
  @Before
  public void setUp() {
    mockModel = new MockKlondikeModel();
    output = new StringWriter();
  }

  /**
   * Counts how many times a substring appears in a string.
   *
   * @param text the string to look in
   * @param sub  the string to count
   * @return how many times sub appears in text
   */
  private static int countOccurrences(String text, String sub) {
    if (text == null || sub == null || sub.isEmpty()) {
      return 0;
    }
    int count = 0;
    int index = 0;
    while ((index = text.indexOf(sub, index)) != -1) {
      count++;
      index += sub.length();
    }
    return count;
  }

  /**
   * Tests basic rendering of message and the game state.
   * using render and renderMessage
   */
  @Test
  public void testViewRenderAndRenderMessageBasic() {
    KlondikeTextualView view = new KlondikeTextualView(mockModel, output);
    view.renderMessage("Hello world!");
    assertTrue("renderMessage should append message", output.toString().contains("Hello world!"));

    view.render();
    assertTrue("render should append Score or at least not crash", output.toString().length() > 0);
  }

  /**
   * Tests that constructors throw IllegalArgumentExceoption if model.
   * or appendable is null.
   */
  @Test
  public void testViewConstructorNullsThrow() {
    try {
      new KlondikeTextualView(null);
      fail("Expected IllegalArgumentException for null model");
    } catch (IllegalArgumentException ignored) {
      //ignored
    }

    try {
      new KlondikeTextualView(mockModel, null);
      fail("Expected IllegalArgumentException for null appendable");
    } catch (IllegalArgumentException ignored) {
      //ignored
    }
  }

  /**
   * Tests that IOException from Appendable.
   * is wrapped as IllegalStateException
   * in render methods
   */
  @Test
  public void testRenderI0ExceptionWrappedAsIllegalState() {
    Appendable badAp = new Appendable() {
      @Override
      public Appendable append(CharSequence csq) throws IOException {
        throw new IOException("fail");
      }

      @Override
      public Appendable append(CharSequence csq, int start, int end) throws IOException {
        throw new IOException("fail");
      }

      @Override
      public Appendable append(char c) throws IOException {
        throw new IOException("fail");
      }
    };

    KlondikeTextualView badView = new KlondikeTextualView(mockModel, badAp);

    try {
      badView.renderMessage("x");
      fail("Expected IllegalStateException when append fails");
    } catch (IllegalStateException ise) {
      // expected
      assertTrue(ise.getMessage().toLowerCase().contains("append"));
    }

    try {
      badView.render();
      fail("Expected IllegalStateException when append fails");
    } catch (IllegalStateException ise) {
      // expected
    }
  }

  /**
   * Tests that starting the game and quitting.
   * prints the board and quit message.
   */
  @Test
  public void testControllerStartGameAndQuitProducesQuitOutput() {
    MockKlondikeModel modelWithBoard = new MockKlondikeModel() {
      @Override
      public String toString() {
        return "MOCK BOARD\n";
      }
    };

    StringReader input = new StringReader("q");
    StringWriter out = new StringWriter();
    KlondikeTextualController controller = new KlondikeTextualController(input, out);

    controller.playGame(modelWithBoard, modelWithBoard.createNewDeck(), false, 1, 1);

    String s = out.toString();
    assertTrue("startGame should be called", modelWithBoard.startGameCalled);
    assertTrue("Should print quit message", s.contains("Game quit!"));
    // view.render should have produced board text
    assertTrue("Should include board representation", s.contains("MOCK BOARD"));
  }

  /**
   * Tests that invalid commands does not.
   * cause extra renders and show
   * invalid move message.
   */
  @Test
  public void testInvalidCommandDoesNotCauseExtraRender() {
    MockKlondikeModel modelWithBoard = new MockKlondikeModel() {
      @Override
      public String toString() {
        return "BOARD\n";
      }
    };

    StringReader input = new StringReader("x q");
    StringWriter out = new StringWriter();
    KlondikeTextualController controller = new KlondikeTextualController(input, out);

    controller.playGame(modelWithBoard, modelWithBoard.createNewDeck(), false, 1, 1);

    String s = out.toString();
    int scorePrints = countOccurrences(s, "Score:");
    assertEquals("Invalid command should not produce an extra render", 2, scorePrints);
    assertTrue("Should print invalid move message", s.contains("Invalid move. Play again."));
  }

  /**
   * Tests that bad inputs in the middle.
   * of a command is ignored and
   * command still executes
   */
  @Test
  public void testBadMidCommandIgnoredAndMoveExecuted() {
    MockKlondikeModel model = new MockKlondikeModel() {
      @Override
      public String toString() {
        return "BOARD\n";
      }
    };

    // mpp a 1 b 1 2 q -> the readInt should skip 'a' and 'b' and parse 1,1,2
    StringReader input = new StringReader("mpp a 1 b 1 2 q");
    StringWriter out = new StringWriter();
    KlondikeTextualController controller = new KlondikeTextualController(input, out);

    controller.playGame(model, model.createNewDeck(), false, 3, 1);

    assertTrue("movePile should be called even with garbage in input", model.movePileCalled);
    assertTrue("Output should contain quit message", out.toString().contains("Game quit!"));
  }

  /**
   * Tests that running out of input.
   * during a command
   * throws IllegalStateException
   */
  @Test
  public void testRunOutOfInputThrows() {
    MockKlondikeModel model = new MockKlondikeModel();
    StringReader input =
        new StringReader("mpp 1"); // will run out of input while expecting more integers
    StringWriter out = new StringWriter();
    KlondikeTextualController controller = new KlondikeTextualController(input, out);

    try {
      controller.playGame(model, model.createNewDeck(), false, 3, 1);
      fail("Expected IllegalStateException when input runs out");
    } catch (IllegalStateException ise) {
      // expected
      assertTrue(ise.getMessage().toLowerCase().contains("ran out of input"));
    }
  }

  /**
   * Tests that game over.
   * is detected at the start of loop
   * and finish board and score are
   * printed
   */
  @Test
  public void testGameOverPrintedAtStartOfLoop() {
    // Model that reports game over immediately and has readable board and score
    MockKlondikeModel model = new MockKlondikeModel() {
      @Override
      public String toString() {
        return "FINAL_BOARD\n";
      }
    };
    model.setGameOver(true);
    model.setScore(10);

    StringReader input =
        new StringReader("q"); // any input -> controller should detect game over first
    StringWriter out = new StringWriter();
    KlondikeTextualController controller = new KlondikeTextualController(input, out);

    controller.playGame(model, model.createNewDeck(), false, 1, 1);

    String s = out.toString();
    assertTrue("isGameOver should have been checked", model.isGameOverCalled);
    assertTrue("Should render final board", s.contains("FINAL_BOARD"));
    assertTrue("Should print game over with score",
        s.contains("Game over") || s.contains("You win!"));
    assertTrue("Should include score number", s.contains("10"));
  }

  /**
   * Tests that multiple commands in order.
   * are executed properly
   * and quit prints properly
   */
  @Test
  public void testMultipleCommandsAndOrder() {
    MockKlondikeModel model = new MockKlondikeModel() {
      @Override
      public String toString() {
        return "B\n";
      }
    };

    StringReader input = new StringReader("mpp 1 1 2 md 1 q");
    StringWriter out = new StringWriter();
    KlondikeTextualController controller = new KlondikeTextualController(input, out);

    controller.playGame(model, model.createNewDeck(), false, 3, 1);

    assertTrue("movePile should be called", model.movePileCalled);
    assertTrue("moveDraw should be called", model.moveDrawCalled);
    assertTrue("Output should contain quit message", out.toString().contains("Game quit!"));
  }

}
