package game.gui;

import javafx.scene.image.Image;
import javafx.scene.text.Font;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class ResourceLoader {

    private static final Map<String, Font>  FONT_CACHE  = new HashMap<>();
    private static final Map<String, Image> IMAGE_CACHE = new HashMap<>();

    private static final String F_BANGERS = "resources/fonts/Bangers-Regular.ttf";
    private static final String F_PIXEL   = "resources/fonts/PressStart2P-Regular.ttf";
    private static final String F_INTER   = "resources/fonts/Inter-VariableFont_opsz,wght.ttf";

    public static void preload() {
        // Register each font file once — after this, Font.loadFont() reads the
        // already-registered family and skips the disk read.
        warmFont(F_BANGERS, 12);
        warmFont(F_PIXEL,   12);
        warmFont(F_INTER,   12);

        // Pre-decode the 7 distinct board-cell images at the two sizes used by
        // GameView (53 px) and InstructionsView (49 px).  These were the worst
        // offender: loadImage called 100× per screen, same 7 files every time.
        String[] boardImages = {
            "metallic_hud_texture", "scarer_door", "laugher_door",
            "conveyor", "sock", "card_cell", "monster_cell",
            "exhausted_door", "activated_door", "dice"
        };
        for (String name : boardImages) {
            fetchAndCache(name, 53, 53);
            fetchAndCache(name, 49, 49);
        }
    }

    private static void warmFont(String path, double size) {
        font(path, size);   // just to trigger the load+register
    }

    // ── Font ─────────────────────────────────────────────────────────────────

    public static Font font(String path, double size) {
        String key = path + "@" + size;
        Font cached = FONT_CACHE.get(key);
        if (cached != null) return cached;
        Font f = loadFontUncached(path, size);
        FONT_CACHE.put(key, f);
        return f;
    }

    private static Font loadFontUncached(String path, double size) {
        InputStream s = ResourceLoader.class.getResourceAsStream("/" + path);
        if (s != null) {
            try {
                Font f = Font.loadFont(s, size);
                if (f != null) return f;
            } catch (Exception ignored) {
            } finally {
                try { s.close(); } catch (Exception ignored) {}
            }
        }
        return Font.font("System", size);
    }

    // ── Image ────────────────────────────────────────────────────────────────

    public static Image loadImage(String name, double w, double h) {
        String key = name + "@" + (int) w + "x" + (int) h;
        if (IMAGE_CACHE.containsKey(key)) return IMAGE_CACHE.get(key);
        return fetchAndCache(name, w, h);
    }

    private static Image fetchAndCache(String name, double w, double h) {
        String key = name + "@" + (int) w + "x" + (int) h;
        String[] paths = {
            "/resources/images/" + name + ".png",
            "/images/"           + name + ".png",
            "/resources/images/" + name + ".jpg",
        };
        for (String path : paths) {
            InputStream s = ResourceLoader.class.getResourceAsStream(path);
            if (s != null) {
                try {
                    Image img = new Image(s, w, h, false, true);
                    IMAGE_CACHE.put(key, img);
                    return img;
                } catch (Exception ignored) {
                } finally {
                    try { s.close(); } catch (Exception ignored) {}
                }
            }
        }
        IMAGE_CACHE.put(key, null);
        return null;
    }

    private ResourceLoader() {}
}
