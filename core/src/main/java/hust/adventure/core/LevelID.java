package hust.adventure.core;

/**
 * Identifiers for all game levels to support data-driven screen logic.
 */
public enum LevelID {
    TANG_1, LIBRARY, LAB, FINAL_OUTSIDE, BOSS_ROOM;

    public static LevelID fromMapPath(String path) {
        if (path == null)
            return null;
        if (path.equals("tang1.tmx"))
            return TANG_1;
        if (path.equals("library.tmx"))
            return LIBRARY;
        if (path.equals("lab.tmx"))
            return LAB;
        if (path.equals("boss_room.tmx"))
            return BOSS_ROOM;
        if (path.equals("Final Outside.tmx"))
            return FINAL_OUTSIDE;
        return null;
    }
}
