package hust.adventure.screens.levels;

import hust.adventure.HustGame;
import hust.adventure.core.config.LevelConfig;

/**
 * Factory for creating specific level screens based on configuration.
 */
public class LevelFactory {

    public static BaseLevelScreen createLevel(final HustGame game, final LevelConfig config) {
        if (config == null)
            return null;

        switch (config.getLevelId()) {
        case LAB:
            return new LabLevel(game, config);
        case LIBRARY:
            return new LibraryLevel(game, config);
        case TANG_1:
            return new Floor1Level(game, config);
        case FINAL_OUTSIDE:
            return new OutsideLevel(game, config);
        case BOSS_ROOM:
            return new BossFightLevel(game, config);
        case TEST_LEVEL:
            return new TestLevel(game, config);
        case MAP_1:
            return new Map1Level(game, config);
        default:
            throw new IllegalArgumentException("Unknown LevelID: " + config.getLevelId());
        }
    }
}
