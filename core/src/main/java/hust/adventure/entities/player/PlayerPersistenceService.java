package hust.adventure.entities.player;

import hust.adventure.core.context.ProgressContext;
import hust.adventure.items.gear.Gear;
import hust.adventure.items.gear.GearFactory;
import hust.adventure.items.weapons.BaseWeapon;
import hust.adventure.items.weapons.WeaponFactory;

import java.util.Map;

/**
 * Service to handle saving and restoring Player stats, weapons, and gears to/from the global ProgressContext.
 */
public class PlayerPersistenceService {
    private static GearFactory gearFactory;
    private static WeaponFactory weaponFactory;

    /**
     * Sets the GearFactory instance used for restoring gear instances.
     *
     * @param factory the GearFactory instance
     */
    public static void setGearFactory(final GearFactory factory) {
        gearFactory = factory;
    }

    /**
     * Sets the WeaponFactory instance used for restoring weapon instances.
     *
     * @param factory the WeaponFactory instance
     */
    public static void setWeaponFactory(final WeaponFactory factory) {
        weaponFactory = factory;
    }

    public static GearFactory getGearFactory() {
        return gearFactory;
    }

    public static WeaponFactory getWeaponFactory() {
        return weaponFactory;
    }

    /**
     * Saves the player's active weapons and gears levels to the ProgressContext.
     *
     * @param player The player entity instance.
     */
    public static void saveWeaponsAndGears(final Player player) {
        if (ProgressContext.instance == null || player == null) {
            return;
        }

        ProgressContext.instance.getWeaponLevels().clear();
        for (final BaseWeapon weapon : player.getWeaponManager().getWeapons()) {
            ProgressContext.instance.getWeaponLevels().put(weapon.getId().toLowerCase(), weapon.getLevel());
        }

        ProgressContext.instance.getGearLevels().clear();
        if (player.getGearManager() != null) {
            for (final Gear gear : player.getGearManager().getGears()) {
                ProgressContext.instance.getGearLevels().put(gear.getId().toLowerCase(), gear.getLevel());
            }
        }
    }

    /**
     * Restores the player's weapons and gears from the ProgressContext.
     *
     * @param player The player entity instance.
     */
    public static void restoreWeaponsAndGears(final Player player) {
        if (ProgressContext.instance == null || player == null) {
            return;
        }

        final Map<String, Integer> savedWeapons = ProgressContext.instance.getWeaponLevels();
        final Map<String, Integer> savedGears = ProgressContext.instance.getGearLevels();

        // Restore weapons
        if (savedWeapons.isEmpty()) {
            // Initialize with default weapon for a new game
            if (weaponFactory != null) {
                final BaseWeapon defaultWeapon = weaponFactory.createWeapon("bun_dau", player);
                defaultWeapon.equip(player);
            }
            savedWeapons.put("bun_dau", 1);
        } else {
            for (final Map.Entry<String, Integer> entry : savedWeapons.entrySet()) {
                final String weaponId = entry.getKey();
                final int targetLevel = entry.getValue();
                if (weaponFactory != null) {
                    final BaseWeapon weapon = weaponFactory.createWeapon(weaponId, player);
                    for (int i = 1; i < targetLevel; i++) {
                        weapon.upgrade(0f, 0f);
                    }
                    weapon.equip(player);
                }
            }
        }

        // Restore gears
        if (player.getGearManager() != null) {
            for (final Map.Entry<String, Integer> entry : savedGears.entrySet()) {
                final String gearId = entry.getKey();
                final int targetLevel = entry.getValue();
                final Gear gear = gearFactory != null ? gearFactory.createGear(gearId) : new Gear(gearId, gearId, "");
                for (int i = 1; i < targetLevel; i++) {
                    gear.upgrade();
                }
                gear.equip(player);

                // Re-apply cumulative equip effects for restored levels > 1
                if (gearFactory != null) {
                    for (int i = 1; i < targetLevel; i++) {
                        gearFactory.applyEquipEffect(gearId, player);
                    }
                }
            }
        }
    }

}
