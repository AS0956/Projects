import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * The {@code ImageEditor} class provides basic image manipulation tools such as
 * seam highlighting, seam removal, and undo functionality.
 * It is designed for use with content-aware image resizing (seam carving).
 */

public class ImageEditor {

    private Image image;
    private List<Pixel> highlightedSeam = null;
    private Deque<Command> commandHistory = new ArrayDeque<>();  // Command history for undo


    /**
     * Loads an image from the specified file path.
     *
     * @param filePath the path to the image file
     * @throws IOException if the file cannot be read
     */

    public void load(String filePath) throws IOException {
        File originalFile = new File(filePath);
        BufferedImage img = ImageIO.read(originalFile);
        image = new Image(img);
    }

    /**
     * Saves the current image to the specified file path in PNG format.
     *
     * @param filePath the destination file path
     * @throws IOException if the file cannot be written
     */

    public void save(String filePath) throws IOException {
        BufferedImage img = image.toBufferedImage();
        ImageIO.write(img, "png", new File(filePath));
    }

    /**
     * Highlights the greenest seam in the image by coloring it green.
     * This is useful for visualizing the most green-saturated vertical path.
     *
     * @throws IOException if the image cannot be updated
     */

    public void highlightGreenest() throws IOException {
        List<Pixel> greenestSeam = image.getGreenestSeam();

        if (greenestSeam != null && !greenestSeam.isEmpty()) {
            highlightedSeam = image.highlightSeam(greenestSeam, Color.GREEN);

            commandHistory.push(new HighlightCommand(image, greenestSeam, Color.GREEN));
        } else {
            System.out.println("Error: Greenest seam is empty or null.");
        }
    }


    /**
     * Removes the currently highlighted seam from the image.
     * This is typically done after a seam is selected and confirmed for deletion.
     *
     * @throws IOException if the image cannot be updated
     */

    public void removeHighlighted() throws IOException {
        if (highlightedSeam != null) {
            image.removeSeam(highlightedSeam);  // Remove the highlighted seam
            commandHistory.push(new RemoveCommand(image, highlightedSeam));  // Push to undo stack
            highlightedSeam = null;  // Clear highlighted seam
        } else {
            System.out.println("No seam is currently highlighted.");
        }
    }

    /**
     * Undoes the last image manipulation action, such as highlighting or seam removal.
     *
     * @throws IOException if the undo operation fails
     */
    public void undo() throws IOException {
        if (!commandHistory.isEmpty()) {
            Command lastCommand = commandHistory.pop();
            lastCommand.undo();
        } else {
            System.out.println("Nothing to undo");
        }
    }

    /**
     * Highlights the lowest-energy vertical seam in the image in red.
     * This is based on pixel energy and useful for seam carving.
     *
     * @throws IOException if the image cannot be updated
     */
    public void highlightLowestEnergySeam() throws IOException {
        if (image == null) {
            System.out.println("Error: No image loaded");
            return;
        }

        List<Pixel> lowestEnergySeam = image.getLowestEnergySeam();
        if (lowestEnergySeam != null && !lowestEnergySeam.isEmpty()) {
            highlightedSeam = image.highlightSeam(lowestEnergySeam, Color.RED);  // Highlight it
            commandHistory.push(new HighlightCommand(image, lowestEnergySeam, Color.RED));  // Push to undo stack
        } else {
            System.out.println("Error: Lowest energy seam is empty or null.");
        }
    }

    /**
     * Interface for command pattern used in undo operations.
     */

    interface Command {
        void undo() throws IOException;
    }

    /**
     * Command implementation for highlighting a seam.
     * Stores the previous colors so the highlight can be undone.
     */

    private class HighlightCommand implements Command {
        private Image image;
        private List<Pixel> seam;
        private List<Color> previousColors;

        /**
         * Constructs a new HighlightCommand.
         *
         * @param image the image being modified
         * @param seam  the seam to highlight
         * @param color the highlight color
         */

        public HighlightCommand(Image image, List<Pixel> seam, Color color) {
            this.image = image;
            this.seam = seam;
            previousColors = new ArrayList<>();
            for(Pixel pixel: seam) {
                previousColors.add(pixel.color);
            }
            image.highlightSeam(seam, color);
        }

        /**
         * Restores the seam's original colors, undoing the highlight.
         */
        @Override
        public void undo() throws IOException {
            for(int i = 0; i < seam.size(); i++) {
                seam.get(i).color = previousColors.get(i);
            }
            refreshUI();
        }
        private void refreshUI() {
            // Call your actual image display component here
            // yourImageDisplayComponent.repaint();
        }
    }

    /**
     * Command implementation for removing a seam.
     * Supports undoing by re-adding the seam.
     */

    private class RemoveCommand implements Command {
        private Image image;
        private List<Pixel> seam;


        /**
         * Constructs a new RemoveCommand.
         *
         * @param image the image being modified
         * @param seam  the seam that was removed
         */

        public RemoveCommand(Image image, List<Pixel> seam) {
            this.image = image;
            this.seam = seam;
        }


        /**
         * Undoes the seam removal by restoring it.
         */

        @Override
        public void undo() throws IOException {
            image.addSeam(seam);
        }
    }

}
