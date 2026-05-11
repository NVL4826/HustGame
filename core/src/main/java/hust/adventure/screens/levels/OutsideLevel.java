package hust.adventure.screens.levels;

import hust.adventure.HustGame;
import hust.adventure.core.config.LevelConfig;

/**
 * Initial outside level of the game.
 */
public class OutsideLevel extends BaseLevelScreen {

    public OutsideLevel(final HustGame game, final LevelConfig config) {
        super(game, config);
    }

    @Override
    protected void initLevel() {
        // No specific initialization for outside level yet
    }

    @Override
    protected void updateLevel(float delta) {
        // No specific update logic for outside level yet
    }

    @Override
    protected void drawLevel() {
        // No specific drawing logic for outside level yet
    }
}
