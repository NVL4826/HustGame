package hust.adventure.ui;

import hust.adventure.core.data.EnemyDataLoader;
import hust.adventure.core.data.GearDataLoader;
import hust.adventure.core.data.ItemDataLoader;
import hust.adventure.core.data.LevelDataLoader;
import hust.adventure.core.data.WeaponDataLoader;
import hust.adventure.entities.enemies.EnemyConfig;
import hust.adventure.items.base.ItemConfig;
import hust.adventure.screens.LevelConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry containing dynamic lists of debug options for maps, items, and monsters.
 */
public class DebugOptionRegistry {
    private static EnemyDataLoader enemyDataManager;
    private static ItemDataLoader itemDataManager;
    private static WeaponDataLoader weaponDataManager;
    private static GearDataLoader gearDataManager;
    private static LevelDataLoader levelDataManager;

    public static void setEnemyDataManager(final EnemyDataLoader manager) {
        enemyDataManager = manager;
    }

    public static void setItemDataManager(final ItemDataLoader manager) {
        itemDataManager = manager;
    }

    public static void setWeaponDataManager(final WeaponDataLoader manager) {
        weaponDataManager = manager;
    }

    public static void setGearDataManager(final GearDataLoader manager) {
        gearDataManager = manager;
    }

    public static void setLevelDataManager(final LevelDataLoader manager) {
        levelDataManager = manager;
    }

    private static DebugOption[] getMapOptions() {
        final List<DebugOption> list = new ArrayList<>();
        if (levelDataManager != null) {
            for (final LevelConfig config : levelDataManager.getAllConfigs()) {
                list.add(new DebugOption(config.getMapPath(), config.getName()));
            }
        }
        return list.toArray(new DebugOption[0]);
    }

    private static DebugOption[] getItemOptions() {
        final List<DebugOption> list = new ArrayList<>();
        if (itemDataManager != null) {
            for (final ItemConfig config : itemDataManager.getAllConfigs()) {
                final String id = config.getId();
                if (weaponDataManager != null && weaponDataManager.getAllWeaponIds().contains(id, false)) {
                    continue; // Skip weapon dummy items from items.json
                }
                list.add(new DebugOption(id, config.getName()));
            }
        }
        if (weaponDataManager != null) {
            for (final String id : weaponDataManager.getAllWeaponIds()) {
                list.add(new DebugOption(id, weaponDataManager.getWeaponName(id) + " (Weapon)"));
            }
        }
        if (gearDataManager != null) {
            for (final String id : gearDataManager.getAllGearIds()) {
                list.add(new DebugOption(id, gearDataManager.getGearName(id) + " (Gear)"));
            }
        }
        return list.toArray(new DebugOption[0]);
    }

    private static DebugOption[] getMonsterOptions() {
        final List<DebugOption> list = new ArrayList<>();
        if (enemyDataManager != null) {
            for (final String type : enemyDataManager.getAllEnemyTypes()) {
                final EnemyConfig config = enemyDataManager.getEnemyConfig(type);
                final String displayName = config != null ? config.getName() : type;
                list.add(new DebugOption(type, displayName));
            }
        }
        return list.toArray(new DebugOption[0]);
    }

    /**
     * Gets the corresponding array of debug options for the given SelectionMode.
     *
     * @param mode the selection mode
     * @return an array of DebugOption, or null if the mode is NONE or invalid
     */
    public static DebugOption[] getOptions(final SelectionMode mode) {
        if (mode == null) {
            return null;
        }
        switch (mode) {
        case MAP:
            return getMapOptions();
        case ITEM:
            return getItemOptions();
        case MONSTER:
            return getMonsterOptions();
        default:
            return null;
        }
    }
}
