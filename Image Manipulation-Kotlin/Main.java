import java.io.IOException;
import java.util.Scanner;

/*APPLICATION CONTROLLER LAYER*/
public class Main {
    private final ImageEditor editor = new ImageEditor();

    /**
     * Print the UI menu options to the user
     */
    private static void printMenu() {
        System.out.println("Please enter a command");
        System.out.println("g - Remove the greenest seam");
        System.out.println("e - Remove the seam with the lowest energy");
        System.out.println("u - Undo previous edit");
        System.out.println("q - Quit");
    }


    private void undo(Scanner scan) throws IOException {
        System.out.println("Undo. Continue? (Y/N)");
        if ("y".equalsIgnoreCase(scan.next())) {
            editor.undo();
        }
        editor.save("target/newImg.png");
    }

    private void energy(Scanner scan) throws IOException {
        editor.highlightLowestEnergySeam();
        editor.save("target/newImg.png");
        System.out.println("Remove a lowest energy seam. Continue? (Y/N)");
        if ("y".equalsIgnoreCase(scan.next())) {
            editor.removeHighlighted();
        } else {
            editor.undo();
        }
        editor.save("target/newImg.png");
    }

    private void greenest(Scanner scan) throws IOException {
        editor.highlightGreenest();
        editor.save("target/newImg.png");
        System.out.println("Remove the greenest seam. Continue? (Y/N)");
        if ("y".equalsIgnoreCase(scan.next())) {
            editor.removeHighlighted();
        } else {
            editor.undo();
        }
        editor.save("target/newImg.png");
    }

    private void run() throws IOException {
        try (Scanner scan = new Scanner(System.in)) {
            System.out.println("Welcome! Enter file path");
            String filePath = scan.next();
            editor.load(filePath);

            String choice = "";
            while (!"q".equalsIgnoreCase(choice)) {
                printMenu();
                choice = scan.next();
                switch (choice.toLowerCase()) {
                    case "g" -> greenest(scan);
                    case "e" -> energy(scan);
                    case "u" -> undo(scan);
                    case "q" -> System.out.println("Thanks for playing.");
                    default -> System.out.println("That is not a valid option.");
                }
            }
            editor.save("target/newImg.png");
        }
    }


    public static void main(String[] args) {
                try {
            new Main().run();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

