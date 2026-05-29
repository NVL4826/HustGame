package hust.adventure.items;

import hust.adventure.core.GearDataManager;
import hust.adventure.core.WeaponDataManager;

/**
 * Catalog for weapon names, upgrade descriptions, and gear properties.
 * Provides unified access to upgrade descriptions for level up screens.
 */
public final class UpgradeCatalog {
    private static GearDataManager gearDataManager;
    private static WeaponDataManager weaponDataManager;

    private UpgradeCatalog() {
        // Prevent instantiation
    }

    /**
     * Sets the GearDataManager instance used for retrieving gear details.
     *
     * @param manager the GearDataManager instance
     */
    public static void setGearDataManager(final GearDataManager manager) {
        gearDataManager = manager;
    }

    /**
     * Sets the WeaponDataManager instance used for retrieving weapon details.
     *
     * @param manager the WeaponDataManager instance
     */
    public static void setWeaponDataManager(final WeaponDataManager manager) {
        weaponDataManager = manager;
    }

    /**
     * Gets the display name of a weapon by its unique identifier.
     *
     * @param id The unique weapon identifier (e.g. "whip", "magic_wand").
     * @return The localized display name of the weapon.
     */
    public static String getWeaponName(final String id) {
        if (weaponDataManager == null) {
            return id;
        }
        return weaponDataManager.getWeaponName(id);
    }

    /**
     * Gets the description of a weapon at a specific level.
     *
     * @param id    The unique weapon identifier.
     * @param level The level of the weapon (1 to 5).
     * @return The description of the upgrade/unlock effect for that level.
     */
    public static String getWeaponLevelDescription(final String id, final int level) {
        if (weaponDataManager == null) {
            return "";
        }
        return weaponDataManager.getWeaponLevelDescription(id, level);
    }

    /**
     * Gets the display name of a gear item by its unique identifier.
     *
     * @param id The unique gear identifier.
     * @return The localized display name of the gear.
     */
    public static String getGearName(final String id) {
        if (gearDataManager == null) {
            return id;
        }
        return gearDataManager.getGearName(id);
    }

    /**
     * Gets the description of a gear item at a specific level.
     *
     * @param id    The unique gear identifier.
     * @param level The level of the gear (1 to 5).
     * @return The description of the upgrade/unlock effect for that level.
     */
    public static String getGearLevelDescription(final String id, final int level) {
        if (gearDataManager == null) {
            return "";
        }
        return gearDataManager.getGearLevelDescription(id, level);
    }
}
