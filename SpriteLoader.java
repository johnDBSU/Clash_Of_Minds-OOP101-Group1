import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Loads UTF-8 CLI sprites from the sprites/ directory with fallback to inline art.
 */
public final class SpriteLoader {

    private static final String SPRITES_DIR = "sprites";

    private SpriteLoader() {
    }

    public static String load(String spriteId, String fallback) {
        Path path = Paths.get(SPRITES_DIR, spriteId + ".txt");
        if (!Files.isRegularFile(path)) {
            return fallback;
        }
        try {
            String raw = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            String normalized = raw.replace("\r\n", "\n").replace("\r", "\n");
            return normalized.isEmpty() ? fallback : normalized;
        } catch (IOException e) {
            return fallback;
        }
    }
}
