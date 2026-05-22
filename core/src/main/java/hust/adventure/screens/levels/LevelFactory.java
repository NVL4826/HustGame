package hust.adventure.screens.levels;

import hust.adventure.HustGame;
import hust.adventure.core.config.LevelConfig;
import hust.adventure.screens.BaseScreen;
import hust.adventure.screens.PlayScreen;

/**
 * Factory for creating specific level screens based on configuration.
 */
public class LevelFactory {

    public static BaseScreen createLevel(final HustGame game, final LevelConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("LevelConfig cannot be null");
        }
        if (game == null) {
            throw new IllegalArgumentException("HustGame cannot be null");
        }

        final LevelBehavior behavior;
        switch (config.getLevelId()) {
            case LAB:
                behavior = new LabBehavior();
                break;
            case LIBRARY:
                behavior = new LibraryBehavior();
                break;
            case TANG_1:
                behavior = new Floor1Behavior();
                break;
            case FINAL_OUTSIDE:
                behavior = new OutsideBehavior();
                break;
            case BOSS_ROOM:
                behavior = new BossFightBehavior();
                break;
            case TEST_LEVEL:
                behavior = new TestBehavior();
                break;
            case MAP_1:
                behavior = new Map1Behavior();
                break;
            default:
                throw new IllegalArgumentException("Unknown LevelID: " + config.getLevelId());
        }

        return new PlayScreen(game, config, behavior);
    }
}
