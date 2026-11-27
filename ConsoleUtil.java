import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Utility class for console input, fullscreen centering, ANSI styling,
 * boxed panels, color-coded HP bars, and safe number input.
 */
public class ConsoleUtil {

    public static final String ANSI_RESET  = "\u001B[0m";
    public static final String ANSI_BOLD   = "\u001B[1m";
    public static final String ANSI_DIM    = "\u001B[2m";

    public static final String FG_GOLD     = "\u001B[38;2;255;209;102m";
    public static final String FG_CYAN     = "\u001B[38;2;56;189;248m";
    public static final String FG_BLUE     = "\u001B[38;2;96;165;250m";
    public static final String FG_GREEN    = "\u001B[38;2;74;222;128m";
    public static final String FG_YELLOW   = "\u001B[38;2;251;191;36m";
    public static final String FG_RED      = "\u001B[38;2;248;113;113m";
    public static final String FG_PURPLE   = "\u001B[38;2;192;132;252m";
    public static final String FG_WHITE    = "\u001B[38;2;248;250;252m";
    public static final String FG_MUTED    = "\u001B[38;2;115;130;150m";

    private static final Pattern ANSI_PATTERN = Pattern.compile("\u001B\\[[0-9;]*m");
    private static final Scanner SCANNER = new Scanner(System.in);

    public static final int BOX_WIDTH = 82;        // usable columns inside box borders
    public static final int FRAME_WIDTH = BOX_WIDTH + 2; // includes left/right border chars
    private static final int BAR_WIDTH = 14;

    private static final int TERM_COLS = detectTermDim("term.cols", "COLUMNS", 120);
    private static final int TERM_ROWS = detectTermDim("term.rows", "LINES", 36);
    private static final String MARGIN = repeat(' ', Math.max(2, (TERM_COLS - FRAME_WIDTH) / 2));

    /** Utility class: no instances allowed. */
    private ConsoleUtil() {
    }

    private static int detectTermDim(String prop, String env, int fallback) {
        String val = System.getProperty(prop);
        if (val == null || val.trim().isEmpty()) {
            val = System.getenv(env);
        }
        if (val != null) {
            try {
                int parsed = Integer.parseInt(val.trim());
                if (parsed >= 40) {
                    return parsed;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return fallback;
    }

    public static int getTermCols() {
        return TERM_COLS;
    }

    public static int getTermRows() {
        return TERM_ROWS;
    }

    public static String getMargin() {
        return MARGIN;
    }

    /** Prints a line aligned to the centered game viewport. */
    public static void println(String text) {
        System.out.println(MARGIN + text + ANSI_RESET);
    }

    /** Prints a blank line. */
    public static void println() {
        System.out.println();
    }

    /** Prints a centered title splash screen. */
    public static void printGameTitle() {
        clearConsole();
        int topPad = Math.max(1, (TERM_ROWS - 22) / 4);
        for (int i = 0; i < topPad; i++) {
            System.out.println();
        }

        String[] logo = {
            "",
            FG_GOLD + ANSI_BOLD + centerLine("██████╗██╗      █████╗ ███████╗██╗  ██╗"),
            FG_GOLD + ANSI_BOLD + centerLine("██╔════╝██║     ██╔══██╗██╔════╝██║  ██║"),
            FG_YELLOW + ANSI_BOLD + centerLine("██║     ██║     ███████║███████╗███████║"),
            FG_YELLOW + ANSI_BOLD + centerLine("██║     ██║     ██╔══██║╚════██║██╔══██║"),
            FG_GOLD + ANSI_BOLD + centerLine("╚██████╗███████╗██║  ██║███████║██║  ██║"),
            FG_MUTED + centerLine(" ╚═════╝╚══════╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝"),
            FG_WHITE + ANSI_BOLD + centerLine("═══════════════   O F   ═══════════════"),
            FG_CYAN + ANSI_BOLD + centerLine("███╗   ███╗██╗███╗   ██╗██████╗ ███████╗"),
            FG_CYAN + ANSI_BOLD + centerLine("████╗ ████║██║████╗  ██║██╔══██╗██╔════╝"),
            FG_BLUE + ANSI_BOLD + centerLine("██╔████╔██║██║██╔██╗ ██║██║  ██║███████╗"),
            FG_BLUE + ANSI_BOLD + centerLine("██║╚██╔╝██║██║██║╚██╗██║██║  ██║╚════██║"),
            FG_PURPLE + ANSI_BOLD + centerLine("██║ ╚═╝ ██║██║██║ ╚████║██████╔╝███████║"),
            FG_MUTED + centerLine("╚═╝     ╚═╝╚═╝╚═╝  ╚═══╝╚═════╝ ╚══════╝"),
            "",
            FG_WHITE + ANSI_BOLD + centerLine("J A V A   Q U I Z   B A T T L E"),
            FG_MUTED + centerLine("────────────────────────────────────────────────────────────"),
            FG_CYAN + centerLine("Defeat the students.  Survive the professors.  Graduate."),
            ""
        };
        printDoubleBox(FG_GOLD + ANSI_BOLD + "OOP101 • GROUP 1" + ANSI_RESET, logo, FG_CYAN);
        System.out.println();
    }

    /** Prints a horizontal separator line across the centered frame. */
    public static void printLine() {
        println(FG_MUTED + repeat('─', FRAME_WIDTH));
    }

    /** Prints a styled section banner inside a double-line frame. */
    public static void printBanner(String text) {
        printBanner(text, FG_GOLD);
    }

    /** Prints a styled section banner with custom accent color. */
    public static void printBanner(String text, String accentColor) {
        System.out.println();
        printDoubleBox(null, new String[]{accentColor + ANSI_BOLD + centerLine(text)}, accentColor);
    }

    /** Clears the terminal cleanly using ANSI clear + cursor home. */
    public static void clearConsole() {
        System.out.print("\u001B[2J\u001B[H\u001B[3J");
        System.out.flush();
    }

    /** Prints the given lines inside a single-line box frame. */
    public static void printBox(String[] lines) {
        printBox(null, lines, FG_CYAN);
    }

    /** Prints the given lines inside a single-line box frame with a title. */
    public static void printBox(String title, String[] lines) {
        printBox(title, lines, FG_CYAN);
    }

    /** Prints the given lines inside a single-line box frame with custom border color. */
    public static void printBox(String title, String[] lines, String borderColor) {
        String topBorder = buildBorderWithTitle('─', title, borderColor);
        System.out.println(MARGIN + borderColor + "┌" + topBorder + borderColor + "┐" + ANSI_RESET);
        for (String line : lines) {
            System.out.println(MARGIN + borderColor + "│" + ANSI_RESET
                    + padToWidth(line, BOX_WIDTH) + ANSI_RESET + borderColor + "│" + ANSI_RESET);
        }
        System.out.println(MARGIN + borderColor + "└" + repeat('─', BOX_WIDTH) + "┘" + ANSI_RESET);
    }

    /** Prints the given lines inside a double-line box frame. */
    public static void printDoubleBox(String title, String[] lines, String borderColor) {
        String topBorder = buildBorderWithTitle('═', title, borderColor);
        System.out.println(MARGIN + borderColor + "╔" + topBorder + borderColor + "╗" + ANSI_RESET);
        for (String line : lines) {
            System.out.println(MARGIN + borderColor + "║" + ANSI_RESET
                    + padToWidth(line, BOX_WIDTH) + ANSI_RESET + borderColor + "║" + ANSI_RESET);
        }
        System.out.println(MARGIN + borderColor + "╚" + repeat('═', BOX_WIDTH) + "╝" + ANSI_RESET);
    }

    private static String buildBorderWithTitle(char fillChar, String title, String borderColor) {
        if (title == null || title.isEmpty()) {
            return repeat(fillChar, BOX_WIDTH);
        }
        String label = " " + title + " ";
        int labelVis = visibleLength(label);
        if (labelVis > BOX_WIDTH) {
            return repeat(fillChar, BOX_WIDTH);
        }
        int leftLen = (BOX_WIDTH - labelVis) / 2;
        int rightLen = BOX_WIDTH - labelVis - leftLen;
        return repeat(fillChar, leftLen) + ANSI_RESET + label + ANSI_RESET
                + borderColor + repeat(fillChar, rightLen);
    }

    /** Returns the visible column count, ignoring ANSI escape sequences. */
    public static int visibleLength(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return ANSI_PATTERN.matcher(text).replaceAll("").length();
    }

    /** Removes trailing plain spaces while preserving ANSI codes. */
    public static String trimTrailingVisible(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        if (!text.contains("\u001B[")) {
            int end = text.length();
            while (end > 0 && text.charAt(end - 1) == ' ') {
                end--;
            }
            return text.substring(0, end);
        }
        return text;
    }

    /** Returns text padded with spaces so boxes stay aligned. ANSI-aware. */
    public static String padToWidth(String text, int width) {
        if (text == null) {
            return repeat(' ', width);
        }
        int visible = visibleLength(text);
        if (visible > width) {
            return truncateVisible(text, width);
        }
        return text + repeat(' ', width - visible);
    }

    /** Centers text inside a specific width (ANSI-aware). */
    public static String centerInWidth(String text, int width) {
        int vis = visibleLength(text);
        int left = Math.max(0, (width - vis) / 2);
        return padToWidth(repeat(' ', left) + text, width);
    }

    /** Truncates text to maxVisible display columns without breaking ANSI escapes. */
    private static String truncateVisible(String text, int maxVisible) {
        StringBuilder out = new StringBuilder();
        int visible = 0;
        int i = 0;
        int len = text.length();
        while (i < len && visible < maxVisible) {
            char c = text.charAt(i);
            if (c == '\u001B' && i + 1 < len && text.charAt(i + 1) == '[') {
                int m = text.indexOf('m', i + 2);
                if (m != -1) {
                    out.append(text, i, m + 1);
                    i = m + 1;
                    continue;
                }
            }
            out.append(c);
            visible++;
            i++;
        }
        if (text.contains("\u001B[")) {
            out.append(ANSI_RESET);
        }
        return out.toString();
    }

    /** Centers text inside the box width (ANSI-aware). */
    public static String centerLine(String text) {
        int padding = Math.max(0, (BOX_WIDTH - visibleLength(text)) / 2);
        return repeat(' ', padding) + text;
    }

    /** Wraps plain text on word boundaries to fit maxWidth. */
    public static List<String> wrapPlain(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            lines.add("");
            return lines;
        }
        String remaining = text.trim();
        while (remaining.length() > maxWidth) {
            int breakAt = remaining.lastIndexOf(' ', maxWidth);
            if (breakAt <= 0) {
                breakAt = maxWidth;
            }
            lines.add(remaining.substring(0, breakAt).trim());
            remaining = remaining.substring(breakAt).trim();
        }
        if (!remaining.isEmpty()) {
            lines.add(remaining);
        }
        return lines;
    }

    /** Returns a string made of the same character repeated count times. */
    public static String repeat(char character, int count) {
        if (count <= 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            builder.append(character);
        }
        return builder.toString();
    }

    /** Builds a color-coded HP bar: "[████████░░░░░░] 80/140". */
    public static String buildBar(int hp, int maxHp) {
        int clamped = Math.max(hp, 0);
        int filled = (maxHp > 0) ? (BAR_WIDTH * clamped / maxHp) : 0;
        filled = Math.max(0, Math.min(BAR_WIDTH, filled));

        double ratio = (maxHp > 0) ? ((double) clamped / maxHp) : 0.0;
        String fillColor = (ratio > 0.50) ? FG_GREEN : (ratio > 0.25 ? FG_YELLOW : FG_RED);

        String bar = fillColor + repeat('█', filled)
                + FG_MUTED + repeat('░', BAR_WIDTH - filled) + ANSI_RESET;
        return FG_MUTED + "[" + bar + FG_MUTED + "] "
                + FG_WHITE + ANSI_BOLD + clamped + FG_MUTED + "/" + maxHp + ANSI_RESET;
    }

    /** Builds "Name [####------] 40/100" style HP text for boxed panels. */
    public static String buildHpBar(String name, int hp, int maxHp) {
        return String.format("%-22s ", name) + buildBar(hp, maxHp);
    }

    /** Keeps asking until the player types a whole number between min and max. */
    public static int readChoice(String prompt, int min, int max) {
        while (true) {
            System.out.print(MARGIN + FG_CYAN + ANSI_BOLD + prompt + ANSI_RESET);
            String line = SCANNER.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }
            println(FG_RED + "  >> Please type a number between " + min + " and " + max + ".");
        }
    }

    /** Waits until the player presses ENTER. */
    public static void pressEnter(String message) {
        System.out.print(MARGIN + FG_MUTED + message + ANSI_RESET);
        SCANNER.nextLine();
    }
}
