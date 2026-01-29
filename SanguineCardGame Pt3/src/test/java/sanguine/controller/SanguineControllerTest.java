package sanguine.controller;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import sanguine.mocks.ModelMock;
import sanguine.mocks.ViewMock;
import sanguine.model.Pawn;

/**
 * Tests for SanguineController using mocks.
 */
public class SanguineControllerTest {

  private SanguineController controller;
  private ModelMock model;
  private ViewMock view;

  /**
   * Sets up the model, view, and player, to be given to the controller.
   */
  @Before
  public void setUp() {
    model = new ModelMock();
    view = new ViewMock();
    PlayerActions player = new HumanPlayerActions(Pawn.RED);
    controller = new SanguineController(model, view, player);
  }

  @Test
  public void testControllerRegistersWithModel() {
    assertTrue("Controller should register as model listener",
        model.listenerRegistered);
  }

  @Test
  public void testControllerRegistersWithView() {
    assertTrue("Controller should register with view",
        view.featuresRegistered);
  }

  @Test
  public void testCanSelectWhenMyTurn() {
    model.currentTurn = Pawn.RED;
    controller.onTurnChanged();
    assertTrue(controller.canSelect());
  }

  @Test
  public void testCannotSelectWhenNotMyTurn() {
    model.currentTurn = Pawn.BLUE;
    controller.onTurnChanged();
    assertFalse(controller.canSelect());
  }

  @Test
  public void testViewDisplayCalledOnCreation() {
    assertTrue(view.displayCalled);
  }

  @Test
  public void testViewRepaintCalledOnTurnChange() {
    model.currentTurn = Pawn.RED;
    assertFalse(view.repaintCalled);
    controller.onTurnChanged();
    assertTrue(view.repaintCalled);
  }

  @Test
  public void testViewTitleUpdatedOnMyTurn() {
    model.currentTurn = Pawn.RED;
    controller.onTurnChanged();
    assertTrue(view.getTitle().contains("Turn"));
  }

  @Test
  public void testViewTitleUpdatedOnNotMyTurn() {
    model.currentTurn = Pawn.BLUE;
    controller.onTurnChanged();
    assertTrue(view.getTitle().contains("Waiting"));
  }
}