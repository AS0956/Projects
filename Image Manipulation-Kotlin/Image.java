import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.lang.Math;

/**
 * Represents an image as a 2D grid of linked Pixel Objects.
 * Each row in the image is implemented as a doubly-linked list of pixels
 * Does energy calculation, seam carving(removal and addition), and visualization
 */
public class Image { //Represents the entire image as a grid of linked Pixel objects
    /**
     * First pixel of each row; each pixel is doubly linked horizontally
     */
    private final List<Pixel> rows; // first column of image, with each item being a pixel

    /**
     * Width of the image (number of columns)
     */
    private int width; //number of columns

    /**
     * Height of the image (number of rows)
     */
    private int height; //number of rows


    /**
     * Constructs an Image from a given BufferedImage by
     * converting it into a linked structure of Pixel objects.
     * @param img
     */



    public Image(BufferedImage img) {
        width = img.getWidth();
        height = img.getHeight();
        rows = new ArrayList<>();

        Pixel current = null;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Pixel pixel = new Pixel(img.getRGB(col, row)); //copies each pixel into a new pixel
                if (col == 0) {
                    rows.add(pixel);
                } else {
                    current.right = pixel;
                    pixel.left = current;
                }
                current = pixel;
            }
        }
    }

    /**
     * Converts the internal Pixel structure back to a BufferedImage
     * @return BufferedImage representation of the current image.
     */

    public BufferedImage toBufferedImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int row = 0; row < height; row++) {
            Pixel pixel = rows.get(row);
            int col = 0;

            while (pixel != null && col < width) {
                image.setRGB(col, row, pixel.color.getRGB());
                pixel = pixel.right;
                col++;
            }
        }

        return image;
    }

    /**
     * @return the width of the image
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the height of the image
     */
    public int getHeight() {
        return height;
    }

    /**
     * Computes the gradient energy at a given pixel location
     * based on its neighbors.
     * @param above pixel directly above
     * @param current   current pixel
     * @param below     pixel directly below
     * @return  energy value as a double
     */

    double energy(Pixel above, Pixel current, Pixel below) {

        double horizEnergy =
                ((above != null && above.left != null ? above.left.brightness() : 0) +
                        2 * (current.left != null ? current.left.brightness() : 0) +
                        (below != null && below.left != null ? below.left.brightness() : 0)) -
                        ((above != null && above.right != null ? above.right.brightness() : 0) +
                                2 * (current.right != null ? current.right.brightness() : 0) +
                                (below != null && below.right != null ? below.right.brightness() : 0));


        double vertEnergy =
                ((above != null && above.left != null ? above.left.brightness() : 0) +
                        (above != null ? 2 * above.brightness() : 0) +
                        (above != null && above.right != null ? above.right.brightness() : 0)) -
                        ((below != null && below.left != null ? below.left.brightness() : 0) +
                                (below != null ? 2 * below.brightness() : 0) +
                                (below != null && below.right != null ? below.right.brightness() : 0));



        return Math.sqrt((horizEnergy * horizEnergy) + (vertEnergy * vertEnergy));
    }

    /**
     * Calculates and stores the energy for every pixel in the image.
     */
    public void calculateEnergy() {
        for (int row = 0; row < height; row++) {
            Pixel pixel = rows.get(row);

            for (int colu = 0; colu < width; colu++) {
                Pixel above = (row > 0) ? getPixelAt(row - 1, colu) : null;

                Pixel below = (row < height - 1) ? getPixelAt(row + 1, colu) : null;

                pixel.energy = energy(above, pixel, below);

                if (pixel != null) {
                    pixel = pixel.right;  // Move to the right pixel
                }
            }
        }
    }

    /**
     * Returns the Pixel at the specified row and column
     * @param row row index
     * @param col   column index
     * @return  the Pixel at (row,col) or null if out of bounds
     */

    private Pixel getPixelAt(int row, int col) {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            return null;
        }

        Pixel current = rows.get(row);
        for (int i = 0; i < col; i++) {
            if (current != null) {
                current = current.right;
            }
        }
        return current;
    }

    /**
     * Highlights a seam of pixels by setting each pixel's color
     * to a given highlight color
     * @param seam  list of pixels to highlight
     * @param color color the highlight color
     * @return  a list of copies of the original seam pixels before coloring.
     */

    public List<Pixel> highlightSeam(List<Pixel> seam, Color color) {

        List<Pixel> previousValues = new ArrayList<>();

        for (Pixel pixel : seam) {
            previousValues.add(new Pixel(pixel.color));

            pixel.color = color;
        }

        return previousValues;
    }

    /**
     * Removes a vertical seam of pixels from the image by adjusting pixel links.
     *
     * @param seam list of pixels forming the seam to remove
     */

    public void removeSeam(List<Pixel> seam) {
        for (Pixel pixel : seam) {

            if (pixel.left != null) {
                pixel.left.right = pixel.right;
            }


            if (pixel.right != null) {
                pixel.right.left = pixel.left;
            }
        }

        width--;
    }

    /**
     * Adds a vertical seam of pixels to the image by adjusting pixel links.
     *
     * @param seam list of pixels forming the seam to add
     */

    public void addSeam(List<Pixel> seam) {
        for (Pixel pixel : seam) {

            if (pixel.left != null) {
                pixel.left.right = pixel;
            }

            if (pixel.right != null) {
                pixel.right.left = pixel;
            }

            pixel.left = pixel.left != null ? pixel.left : null;
            pixel.right = pixel.right != null ? pixel.right : null;
        }

        width++;
    }

    /**
     * Computes a seam (vertical path from top to bottom) that maximizes a given value.
     *
     * @param valueGetter function that extracts a double value from each pixel
     * @return list of pixels forming the optimal seam
     */

    private List<Pixel> getSeamMaximizing(Function<Pixel, Double> valueGetter) {
        double[][] values = new double[height][width];

        for (int col = 0; col < width; col++) {
            values[0][col] = valueGetter.apply(getPixelAt(0, col));  // Apply the valueGetter function to each pixel in the first row
        }

        for (int row = 1; row < height; row++) {
            for (int col = 0; col < width; col++) {

                double left = (col > 0) ? values[row - 1][col - 1] : Double.POSITIVE_INFINITY;
                double above = values[row - 1][col];
                double right = (col < width - 1) ? values[row - 1][col + 1] : Double.POSITIVE_INFINITY;

                values[row][col] = valueGetter.apply(getPixelAt(row, col)) + Math.min(left, Math.min(above, right));
            }
        }

        List<Pixel> seam = new ArrayList<>();
        int minCol = 0;

        for (int col = 1; col < width; col++) {
            if (values[height - 1][col] < values[height - 1][minCol]) {
                minCol = col;
            }
        }


        for (int row = height - 1; row >= 0; row--) {
            seam.add(getPixelAt(row, minCol));

            if (row == 0) break;

            if (minCol > 0 && values[row][minCol] == values[row - 1][minCol - 1] + valueGetter.apply(getPixelAt(row, minCol))){
                minCol--;
            } else if (values[row][minCol] == values[row - 1][minCol] + valueGetter.apply(getPixelAt(row, minCol))) {
            } else if (minCol < width - 1 && values[row][minCol] == values[row - 1][minCol + 1] + valueGetter.apply(getPixelAt(row, minCol))) {
                minCol++;
            }
        }

        Collections.reverse(seam);
        return seam;
    }

    /**
     * Finds the vertical seam with the most green pixels.
     *
     * @return list of pixels forming the greenest seam
     */

    public List<Pixel> getGreenestSeam() {
        return getSeamMaximizing(pixel -> -(double) pixel.color.getGreen());
    }

    /**
     * Finds the vertical seam with the lowest energy.
     *
     * @return list of pixels forming the seam with minimal energy
     */

    public List<Pixel> getLowestEnergySeam() {
        calculateEnergy();

        return getSeamMaximizing(pixel -> -pixel.energy);
}}

