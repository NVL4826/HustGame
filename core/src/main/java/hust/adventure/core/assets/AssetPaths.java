package hust.adventure.core.assets;

/**
 * Centralized registry of common asset path constants across UI, maps, and character subsystems.
 */
public final class AssetPaths {

    public static final String UI_TEXT_BOX = "ui/text_box.png";
    public static final String UI_BACKGROUND = "ui/background.png";

    public static final String MAP_LIBRARY_BG = "map/Library1.jpg";
    public static final String MAP_BOSS_ROOM_BG = "map/Boss Room.jpg";

    public static final String CHARACTER_ATLAS = "character/atlas.png";
    public static final String CHARACTER_BULLET = "character/bullet.png";

    public static final String MUSIC_WIN_MENU = "audio/music/win_menu.mp3";

    private AssetPaths() {
        // Prevent instantiation of constant utility class
    }
}
