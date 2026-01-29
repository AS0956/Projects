package sanguine.mocks;

import sanguine.model.Pawn;
import sanguine.view.Features;
import sanguine.view.SanguineFrame;

/**
 * Mocks the view for controller testing.
 */
public class ViewMock extends SanguineFrame {
  public boolean featuresRegistered = false;
  public boolean displayCalled = false;
  public boolean repaintCalled = false;
  String title = "";

  /**
   * Calls the SanguineFrame super class.
   */
  public ViewMock() {
    super(new ModelMock(), Pawn.RED);
  }

  @Override
  public void addFeatures(Features features) {
    featuresRegistered = true;
  }

  @Override
  public void display() {
    displayCalled = true;
  }

  @Override
  public void repaint() {
    repaintCalled = true;
  }

  @Override
  public void clearSelections() {}
}