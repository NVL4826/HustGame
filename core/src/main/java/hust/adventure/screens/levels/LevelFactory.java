package hust.adventure.screens.levels;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

import hust.adventure.HustGame;
import hust.adventure.core.config.LevelConfig;
import hust.adventure.core.config.LevelID;
import hust.adventure.screens.BaseScreen;
import hust.adventure.screens.PlayScreen;

/**
 * Factory for creating specific level screens based on configuration.
 */
public class LevelFactory {
    private static final Map<LevelID, Supplier<LevelBehavior>> REGISTRY = new EnumMap<>(LevelID.class);

    static {
        REGISTRY.put(LevelID.LAB, LabBehavior::new);
        REGISTRY.put(LevelID.LIBRARY, LibraryBehavior::new);
        REGISTRY.put(LevelID.TANG_1, Floor1Behavior::new);
        REGISTRY.put(LevelID.FINAL_OUTSIDE, OutsideBehavior::new);
        REGISTRY.put(LevelID.BOSS_ROOM, BossFightBehavior::new);
        REGISTRY.put(LevelID.TEST_LEVEL, TestBehavior::new);
        REGISTRY.put(LevelID.MAP_1, Map1Behavior::new);
    }

    public static BaseScreen createLevel(final HustGame game, final LevelConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("LevelConfig cannot be null");
        }
        if (game == null) {
            throw new IllegalArgumentException("HustGame cannot be null");
        }

        final Supplier<LevelBehavior> supplier = REGISTRY.get(config.getLevelId());
        if (supplier == null) {
            throw new IllegalArgumentException("Unknown or unregistered LevelID: " + config.getLevelId());
        }

        return new PlayScreen(game, config, supplier.get());
    }
}
