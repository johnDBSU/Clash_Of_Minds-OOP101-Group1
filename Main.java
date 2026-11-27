import java.util.NoSuchElementException;

/**
 * Entry point of the Clash of Minds game.
 */
public class Main {

    public static void main(String[] args) {
        Game game = new Game();
        try {
            game.start();
        } catch (NoSuchElementException e) {
            // input stream was closed (e.g. Ctrl+D / piped input ran out)
            ConsoleUtil.println();
            ConsoleUtil.println(ConsoleUtil.FG_MUTED + "Input closed. Goodbye!");
        }
    }
}
