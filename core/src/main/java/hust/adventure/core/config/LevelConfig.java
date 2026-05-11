package hust.adventure.core.config;

import com.badlogic.gdx.graphics.Color;

/**
 * Data object holding configuration for a specific game level. Used to inject data into StandardLevelScreen.
 */
public class LevelConfig {
    private final LevelID levelId;
    private final String mapPath;
    private final float spawnX;
    private final float spawnY;
    private final float zoom;
    private final String bgmPath;
    private final Color ambientColor;

    public LevelConfig(LevelID levelId, String mapPath, float spawnX, float spawnY) {
        this(levelId, mapPath, spawnX, spawnY, 1.0f, null, Color.BLACK);
    }

    public LevelConfig(LevelID levelId, String mapPath, float spawnX, float spawnY, float zoom, String bgmPath,
            Color ambientColor) {
        this.levelId = levelId;
        this.mapPath = mapPath;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.zoom = zoom;
        this.bgmPath = bgmPath;
        this.ambientColor = ambientColor != null ? ambientColor : Color.BLACK;
    }

    public LevelID getLevelId() {
        return levelId;
    }

    public String getMapPath() {
        return mapPath;
    }

    public float getSpawnX() {
        return spawnX;
    }

    public float getSpawnY() {
        return spawnY;
    }

    public float getZoom() {
        return zoom;
    }

    public String getBgmPath() {
        return bgmPath;
    }

    public Color getAmbientColor() {
        return ambientColor;
    }
}
