package hust.adventure.core.config;

/**
 * Identifiers for all game levels to support data-driven screen logic.
 */
public enum LevelID {
    TANG_1, LIBRARY, LAB, FINAL_OUTSIDE, BOSS_ROOM, TEST_LEVEL, MAP_1;

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
        if (path.equals("test.tmx"))
            return TEST_LEVEL;
        if (path.equals("tsx/map_1.tmx"))
            return MAP_1;
        return null;
    }

    public com.badlogic.gdx.graphics.Color getAmbientColor() {
        switch (this) {
        case LIBRARY:
            return new com.badlogic.gdx.graphics.Color(0.6f, 0.6f, 0.6f, 1f);
        case BOSS_ROOM:
            return new com.badlogic.gdx.graphics.Color(0.6f, 0.6f, 0.6f, 1f);
        case LAB:
            return new com.badlogic.gdx.graphics.Color(0.6f, 0.6f, 0.6f, 1f);
        default:
            return new com.badlogic.gdx.graphics.Color(0.9f, 0.9f, 0.9f, 1f); // White for outside
        }
    }

    public boolean isInfinite() {
        return this == MAP_1;
    }
}
