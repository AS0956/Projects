import java.awt.*;

/**
 * Represents a single pixel in an image.
 * <p>
 * Each pixel contains its color information, computed energy,
 * and optional links to neighboring pixels (left and right).
 * This class is commonly used in image processing tasks like seam carving.
 * </p>
 */

public class Pixel { //Represents one pixel in the image and holds some useful info about it

    /** Reference to the pixel to the left (used in seam tracing or linking). */
    Pixel left;

    /** Reference to the pixel to the right. */
    Pixel right;

    /** Stores the computed energy of the pixel (used in energy-based seam carving). */
    double energy;

    /** Color of the pixel. */
    Color color;

    /**
     * Constructs a Pixel object using an RGB integer value.
     *
     * @param rgb the integer representing RGB color.
     */

    public Pixel(int rgb) {
        this.color = new Color(rgb);
    }

    /**
     * Constructs a Pixel object using a {@link Color} object.
     *
     * @param color the color of the pixel.
     */
    public Pixel(Color color) {
        this.color = color;
    }

    /**
     * Calculates and returns the perceived brightness of the pixel
     * using the standard luminance formula.
     *
     * @return the brightness value between 0 and 255.
     */

    public double brightness() {

        return 0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue();
    }


    /**
     * Returns the green channel intensity of the pixel.
     *
     * @return the green value (0–255).
     */

    public double getGreen() {
        return color.getGreen();
    }
}


