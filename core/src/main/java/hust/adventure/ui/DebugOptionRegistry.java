package hust.adventure.ui;

import hust.adventure.core.config.LevelID;
import hust.adventure.core.data.EnemyDataManager;
import hust.adventure.core.data.GearDataManager;
import hust.adventure.core.data.ItemDataManager;
import hust.adventure.core.data.WeaponDataManager;
import hust.adventure.entities.enemies.EnemyConfig;
import hust.adventure.items.base.ItemConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry containing dynamic lists of debug options for maps, items, and monsters.
 */
public class DebugOptionRegistry {
    private static EnemyDataManager enemyDataManager;
    private static ItemDataManager itemDataManager;
    private static WeaponDataManager weaponDataManager;
    private static GearDataManager gearDataManager;

    public static void setEnemyDataManager(final EnemyDataManager manager) {
        enemyDataManager = manager;
    }

    public static void setItemDataManager(final ItemDataManager manager) {
        itemDataManager = manager;
    }

    public static void setWeaponDataManager(final WeaponDataManager manager) {
        weaponDataManager = manager;
    }

    public static void setGearDataManager(final GearDataManager manager) {
        gearDataManager = manager;
    }

    private static DebugOption[] getMapOptions() {
        final List<DebugOption> list = new ArrayList<>();
        for (final LevelID level : LevelID.values()) {
            String path = null;
            String name = null;
            switch (level) {
            case TANG_1:
                path = "tang1.tmx";
                name = "Floor 1 (tang1)";
                break;
            case LIBRARY:
                path = "library.tmx";
                name = "Library";
                break;
            case LAB:
                path = "lab.tmx";
                name = "Lab";
                break;
            case BOSS_ROOM:
                path = "boss_room.tmx";
                name = "Boss Room";
                break;
            case FINAL_OUTSIDE:
                path = "Final Outside.tmx";
                name = "Final Outside";
                break;
            case TEST_LEVEL:
                path = "test.tmx";
                name = "Test Map";
                break;
            case MAP_1:
                path = "tsx/map_1.tmx";
                name = "Map 1";
                break;
            }
            if (path != null) {
                list.add(new DebugOption(path, name));
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
