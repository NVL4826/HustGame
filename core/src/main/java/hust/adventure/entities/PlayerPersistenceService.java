package hust.adventure.entities;

import hust.adventure.core.context.ProgressContext;
import hust.adventure.items.Gear;
import hust.adventure.items.weapons.WeaponFactory;
import hust.adventure.items.weapons.Weaponable;

import java.util.Map;

/**
 * Service to handle saving and restoring Player stats, weapons, and gears to/from the global ProgressContext.
 */
public class PlayerPersistenceService {

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
        for (final Weaponable w : player.getWeaponManager().getWeapons()) {
            ProgressContext.instance.getWeaponLevels().put(w.getId().toLowerCase(), w.getLevel());
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
            final Weaponable defaultWeapon = WeaponFactory.createWeapon("bun_dau", player);
            player.getWeaponManager().addWeapon(defaultWeapon);
            savedWeapons.put("bun_dau", 1);
        } else {
            for (final Map.Entry<String, Integer> entry : savedWeapons.entrySet()) {
                final String weaponId = entry.getKey();
                final int targetLevel = entry.getValue();
                final Weaponable weapon = WeaponFactory.createWeapon(weaponId, player);
                for (int i = 1; i < targetLevel; i++) {
                    weapon.upgrade(0f, 0f);
                }
                player.getWeaponManager().addWeapon(weapon);
            }
        }

        // Restore gears
        if (player.getGearManager() != null) {
            for (final Map.Entry<String, Integer> entry : savedGears.entrySet()) {
                final String gearId = entry.getKey();
                final int targetLevel = entry.getValue();
                final String gearName = Gear.getDefaultName(gearId);
                final String gearDesc = Gear.getDefaultDescription(gearId, 1);
                final Gear gear = new Gear(gearId, gearName, gearDesc);
                for (int i = 1; i < targetLevel; i++) {
                    gear.upgrade();
                }
                player.getGearManager().addGear(gear);
            }
        }
    }

    /**
     * Saves the player's core stats to the ProgressContext.
     *
     * @param player The player entity instance.
     */
    public static void saveStats(final Player player) {
        if (ProgressContext.instance == null || player == null) {
            return;
        }
        ProgressContext.instance.setHp(player.getHp());
        ProgressContext.instance.setMaxHp(player.getMaxHp());
        ProgressContext.instance.setStamina(player.getStamina());
    }

    /**
     * Restores the player's core stats from the ProgressContext.
     *
     * @param player The player entity instance.
     */
    public static void restoreStats(final Player player) {
        if (ProgressContext.instance == null || player == null) {
            return;
        }
        player.setPlayerMaxHp(ProgressContext.instance.getMaxHp());
        player.setPlayerHp(ProgressContext.instance.getHp());
        player.setStamina(ProgressContext.instance.getStamina());
    }
}
