package klondike;

import klondike.model.hw02.BasicKlondike;
import klondike.model.hw02.KlondikeModel;
import klondike.model.hw04.KlondikeCreator;
import klondike.model.hw04.WhiteheadKlondike;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the KlondikeCreator factory class.
 */
public class KlondikeCreatorTest {

  /**
   * Tests that creating a BASIC game returns a BasicKlondike instance.
   */
  @Test
  public void testCreateBasic() {
    KlondikeModel<?> game = KlondikeCreator.create(KlondikeCreator.GameType.BASIC);
    Assert.assertTrue(game instanceof BasicKlondike);
  }

  /**
   * Tests that creating a WHITEHEAD game returns a WhiteheadKlondike instance.
   */
  @Test
  public void testCreateWhitehead() {
    KlondikeModel<?> game = KlondikeCreator.create(KlondikeCreator.GameType.WHITEHEAD);
    Assert.assertTrue(game instanceof WhiteheadKlondike);
  }

  /**
   * Tests that the BasicKlondike is not null.
   */
  @Test
  public void testBasicNotNull() {
    KlondikeModel<?> game = KlondikeCreator.create(KlondikeCreator.GameType.BASIC);
    Assert.assertNotNull(game);
  }

  /**
   * Tests that created WhiteheadKlondike is not null.
   */
  @Test
  public void testWhiteheadNotNull() {
    KlondikeModel<?> game = KlondikeCreator.create(KlondikeCreator.GameType.WHITEHEAD);
    Assert.assertNotNull(game);
  }

  /**
   * Tests that the factory method creates new instances every time.
   */
  @Test
  public void testDifferentInstances() {
    KlondikeModel<?> game1 = KlondikeCreator.create(KlondikeCreator.GameType.BASIC);
    KlondikeModel<?> game2 = KlondikeCreator.create(KlondikeCreator.GameType.BASIC);
    Assert.assertNotSame(game1, game2);
  }

  /**
   * Tests that both enum values do exist.
   */
  @Test
  public void testEnumHasBothTypes() {
    // check that both enum values exist
    Assert.assertNotNull(KlondikeCreator.GameType.BASIC);
    Assert.assertNotNull(KlondikeCreator.GameType.WHITEHEAD);
  }

  /**
   * Tests that GameType enum has exactly two values.
   */
  @Test
  public void testEnumCorrectSize() {
    Assert.assertEquals(2, KlondikeCreator.GameType.values().length);
  }
}