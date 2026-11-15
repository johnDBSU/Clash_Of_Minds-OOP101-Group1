import java.util.Scanner;

/**
 * Utility class for console input and output.
 * Holds the shared Scanner, the game banner, boxed panels, HP bars,
 * screen clearing and safe number input.
 */
public class ConsoleUtil {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final int BAR_WIDTH = 10;
    private static final int BOX_WIDTH = 68;   // usable characters inside a box
    private static final int CLEAR_LINES = 50; // blank lines used to clean the screen

    /** Utility class: no instances allowed. */
    private ConsoleUtil() {
    }

    /** Prints the game title screen. */
    public static void printGameTitle() {
        System.out.println();
        System.out.println("=========================================================");
        System.out.println("    C L A S H   O F   M I N D S  -  a Java quiz battle   ");
        System.out.println("=========================================================");
        System.out.println("  Defeat the students. Survive the professors. Graduate. ");
        System.out.println();
    }

    /** Prints a horizontal separator line. */
    public static void printLine() {
        System.out.println("---------------------------------------------------------");
    }

    /** Prints a section heading inside a box frame. */
    public static void printBanner(String text) {
        System.out.println();
        printBox(new String[]{text});
    }

    /** Pushes the old text off the screen so the next panel appears clean. */
    public static void clearConsole() {
        for (int i = 0; i < CLEAR_LINES; i++) {
            System.out.println();
        }
    }

    /** Prints the given lines inside a box frame. */
    public static void printBox(String[] lines) {
        printBox(null, lines);
    }

    /** Prints the given lines inside a box frame with a title set into the top border. */
    public static void printBox(String title, String[] lines) {
        String topBorder = repeat('-', BOX_WIDTH);
        if (title != null && !title.isEmpty()) {
            String label = " " + title + " ";
            if (label.length() <= BOX_WIDTH) {
                int start = (BOX_WIDTH - label.length()) / 2;
                topBorder = topBorder.substring(0, start) + label
                        + topBorder.substring(start + label.length());
            }
        }
        System.out.println("  +" + topBorder + "+");
        for (String line : lines) {
            System.out.println("  |" + padToWidth(line, BOX_WIDTH) + "|");
        }
        System.out.println("  +" + repeat('-', BOX_WIDTH) + "+");
    }

    /** Returns text padded with spaces so boxes stay aligned. Overlong text is cut. */
    public static String padToWidth(String text, int width) {
        if (text.length() > width) {
            return text.substring(0, width);
        }
        return text + repeat(' ', width - text.length());
    }

    /** Returns a string made of the same character repeated count times. */
    public static String repeat(char character, int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(character);
        }
        return builder.toString();
    }

    /** Builds "Name [####------] 40/100" style HP text for boxed panels. */
    public static String buildHpBar(String name, int hp, int maxHp) {
        int filled = 0;
        if (maxHp > 0) {
            filled = BAR_WIDTH * hp / maxHp;
        }
        if (filled < 0) {
            filled = 0;
        }
        if (filled > BAR_WIDTH) {
            filled = BAR_WIDTH;
        }
        String bar = repeat('#', filled) + repeat('-', BAR_WIDTH - filled);
        return String.format("%-22s [%s] %d/%d", name, bar, Math.max(hp, 0), maxHp);
    }

    /** Keeps asking until the player types a whole number between min and max. */
    public static int readChoice(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // input was not a number: show the error message below
            }
            System.out.println("  >> Please type a number between " + min + " and " + max + ".");
        }
    }

    /** Waits until the player presses ENTER. */
    public static void pressEnter(String message) {
        System.out.print(message);
        SCANNER.nextLine();
    }
}
