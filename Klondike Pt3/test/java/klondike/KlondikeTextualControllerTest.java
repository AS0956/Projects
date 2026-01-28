package klondike;

import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import klondike.controller.KlondikeTextualController;
import klondike.mock.MockKlondikeModel;
import org.junit.Test;

/**
 * Mock-based tests for KlondikeTextualController.
 */
public class KlondikeTextualControllerTest {

  /**
   * Tests that startGame is called and the quitting works.
   */

  @Test
  public void testControllerStartGameAndQuit() {
    MockKlondikeModel mockModel = new MockKlondikeModel();
    StringReader input = new StringReader("q"); // quit immediately
    StringWriter output = new StringWriter();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    controller.playGame(mockModel, List.of(), false, 1, 1);

    assertTrue("startGame should be called", mockModel.startGameCalled);
    assertTrue("Output should contain 'Game quit!'", output.toString().contains("Game quit!"));
  }

  /**
   * Tests that movePile command calls the model correctly.
   */

  @Test
  public void testControllerMovePileCalled() {
    MockKlondikeModel mockModel = new MockKlondikeModel();
    StringReader input = new StringReader("mpp 1 1 2 q"); // movePile then quit
    StringWriter output = new StringWriter();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    controller.playGame(mockModel, List.of(), false, 2, 1);

    assertTrue("movePile should be called", mockModel.movePileCalled);
    assertTrue("Output should contain 'Game quit!'", output.toString().contains("Game quit!"));
  }

  /**
   * Tests that moveDraw command calls the model properly.
   */

  @Test
  public void testControllerMoveDrawCalled() {
    MockKlondikeModel mockModel = new MockKlondikeModel();
    StringReader input = new StringReader("md 1 q"); // moveDraw then quit
    StringWriter output = new StringWriter();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    controller.playGame(mockModel, List.of(), false, 2, 1);

    assertTrue("moveDraw should be called", mockModel.moveDrawCalled);
    assertTrue("Output should contain 'Game quit!'", output.toString().contains("Game quit!"));
  }

  /**
   * Tests that game over is detected and output is right.
   */
  @Test
  public void testControllerDetectsGameOver() {
    MockKlondikeModel mockModel = new MockKlondikeModel();
    mockModel.setGameOver(true);

    StringReader input = new StringReader("invalid q");
    StringWriter output = new StringWriter();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    controller.playGame(mockModel, List.of(), false, 1, 1);

    assertTrue("isGameOver should be called", mockModel.isGameOverCalled);
    assertTrue("Output should contain 'Game over!'", output.toString().contains("Game over!"));
  }

  /**
   * Tests that an invalid command shows the proper message.
   */
  @Test
  public void testControllerInvalidCommand() {
    MockKlondikeModel mockModel = new MockKlondikeModel();
    StringReader input = new StringReader("invalid q"); // invalid command then quit
    StringWriter output = new StringWriter();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    controller.playGame(mockModel, List.of(), false, 1, 1);

    assertTrue("Output should contain 'Invalid move. Play again.'",
        output.toString().contains("Invalid move. Play again."));
  }

  /**
   * Tests multiple commands are handled correctly.
   */
  @Test
  public void testControllerMultipleCommands() {
    MockKlondikeModel mockModel = new MockKlondikeModel();
    StringReader input = new StringReader("mpp 1 1 2 md 1 q"); // multiple commands then quit
    StringWriter output = new StringWriter();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    controller.playGame(mockModel, List.of(), false, 2, 1);

    assertTrue("movePile should be called", mockModel.movePileCalled);
    assertTrue("moveDraw should be called", mockModel.moveDrawCalled);
    assertTrue("Output should contain 'Game quit!'", output.toString().contains("Game quit!"));
  }

  /**
   * Tests that moveToFoundation command calls the model properly.
   */
  @Test
  public void testControllerMoveToFoundationCalled() {
    MockKlondikeModel mockModel = new MockKlondikeModel();
    StringReader input = new StringReader("mpf 1 1 q");
    StringWriter output = new StringWriter();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    controller.playGame(mockModel, List.of(), false, 2, 1);
    assertTrue(mockModel.moveToFoundationCalled);
    assertTrue(output.toString().contains("Game quit!"));

  }

  /**
   * Tests that moveDrawToFoundation command calls the model properly.
   */
  @Test
  public void testControllerMoveDrawToFoundationCalled() {
    MockKlondikeModel mockModel = new MockKlondikeModel();
    StringReader input = new StringReader("mdf 1 1 q");
    StringWriter output = new StringWriter();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    controller.playGame(mockModel, List.of(), false, 2, 1);

    assertTrue(mockModel.moveDrawToFoundationCalled);
    assertTrue(output.toString().contains("Game quit!"));
  }

  /**
   * Tests that discardDraw command calls the model correctly.
   */

  @Test
  public void testControllerDiscardDrawCalled() {
    MockKlondikeModel mockModel = new MockKlondikeModel();
    StringReader input = new StringReader("dd q");
    StringWriter output = new StringWriter();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    controller.playGame(mockModel, List.of(), false, 2, 1);

    assertTrue(mockModel.discardDrawCalled);
    assertTrue(output.toString().contains("Game quit!"));
  }

  /**
   * Tests that extra spaces in command are handled correctly.
   */
  @Test
  public void testControllerCommandWithExtraSpaces() {
    MockKlondikeModel mockModel = new MockKlondikeModel();
    StringReader input = new StringReader(" mpp  1 1 2 q ");
    StringWriter output = new StringWriter();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    controller.playGame(mockModel, List.of(), false, 2, 1);

    assertTrue("movePile should be called", mockModel.movePileCalled);
    assertTrue(output.toString().contains("Game quit!"));
  }

  /**
   * Tests that quitting with uppercase 'Q' works properly.
   */
  @Test
  public void testQuitUpperCase() {
    MockKlondikeModel mockModel = new MockKlondikeModel();
    mockModel.setScore(10);
    StringReader input = new StringReader("Q");
    StringWriter output = new StringWriter();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    controller.playGame(mockModel, List.of(), false, 2, 1);
    assertTrue(output.toString().contains("Game quit!"));
    assertTrue(output.toString().contains("10"));
  }

  /**
   * Tests that an invalid move can be retried.
   * Shows proper message.
   */
  @Test
  public void testControllerRetriesInvalidMove() {
    MockKlondikeModel mockModel = new MockKlondikeModel();
    mockModel.setSimulateInvalidMove(true);
    StringReader input = new StringReader("mpp 1 1 2 q");
    StringWriter output = new StringWriter();
    KlondikeTextualController controller = new KlondikeTextualController(input, output);

    controller.playGame(mockModel, List.of(), false, 2, 1);

    assertTrue(output.toString().contains("Invalid move. Play again."));
  }

}



