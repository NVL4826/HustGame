package hust.adventure.screens.levels;

import hust.adventure.HustGame;
import hust.adventure.core.config.LevelConfig;

/**
 * Level screen for map_1, supporting repeating backgrounds.
 */
public class Map1Level extends BaseLevelScreen {

    public Map1Level(final HustGame game, final LevelConfig config) {
        super(game, config);
    }

    @Override
    protected void initLevel() {
        // Spawn player at a reasonable position if not already set
        // The config should handle this, but we can add some entities for testing
    }

    @Override
    protected void updateLevel(float delta) {
    }

    @Override
    protected void drawLevel() {
    }
}
