package hust.adventure.items;

import hust.adventure.core.data.GearDataLoader;
import hust.adventure.core.data.WeaponDataLoader;

/**
 * Catalog for weapon names, upgrade descriptions, and gear properties. Provides unified access to upgrade descriptions
 * for level up screens.
 */
public class UpgradeCatalog {
    private final GearDataLoader gearDataManager;
    private final WeaponDataLoader weaponDataManager;

    /**
     * Constructs a new UpgradeCatalog with dependencies.
     */
    public UpgradeCatalog(final GearDataLoader gearDataManager, final WeaponDataLoader weaponDataManager) {
        this.gearDataManager = gearDataManager;
        this.weaponDataManager = weaponDataManager;
    }

    /**
     * Gets the display name of a weapon by its unique identifier.
     *
     * @param id The unique weapon identifier (e.g. "whip", "magic_wand").
     * @return The localized display name of the weapon.
     */
    public String getWeaponName(final String id) {
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
    public String getWeaponLevelDescription(final String id, final int level) {
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
    public String getGearName(final String id) {
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
    public String getGearLevelDescription(final String id, final int level) {
        if (gearDataManager == null) {
            return "";
        }
        return gearDataManager.getGearLevelDescription(id, level);
    }
}
