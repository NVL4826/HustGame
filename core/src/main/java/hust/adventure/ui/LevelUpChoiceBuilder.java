package hust.adventure.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import hust.adventure.core.GearDataManager;
import hust.adventure.core.WeaponDataManager;
import hust.adventure.entities.Player;
import hust.adventure.items.Gear;
import hust.adventure.items.UpgradeCatalog;
import hust.adventure.weapons.BaseWeapon;
import hust.adventure.ui.components.DamageIncreaseAction;
import hust.adventure.ui.components.GearUpgradeAction;
import hust.adventure.ui.components.HealAction;
import hust.adventure.ui.components.UpgradeAction;
import hust.adventure.ui.components.WeaponUpgradeAction;

/**
 * Builder class that generates random upgrade choices for a player when leveling up. Evaluates the player's current
 * weapons and gears to determine possible upgrades.
 */
public final class LevelUpChoiceBuilder {
    private static GearDataManager gearDataManager;
    private static WeaponDataManager weaponDataManager;

    private LevelUpChoiceBuilder() {
        // Prevent instantiation
    }

    /**
     * Sets the GearDataManager instance used for retrieving available gears.
     *
     * @param manager the GearDataManager instance
     */
    public static void setGearDataManager(final GearDataManager manager) {
        gearDataManager = manager;
    }

    /**
     * Sets the WeaponDataManager instance used for retrieving available weapons.
     *
     * @param manager the WeaponDataManager instance
     */
    public static void setWeaponDataManager(final WeaponDataManager manager) {
        weaponDataManager = manager;
    }

    /**
     * Gathers all eligible weapon and gear upgrade actions, shuffles them, and returns up to 3 upgrade choices for the
     * player.
     *
     * @param player The player who leveled up and is choosing an upgrade.
     * @return An Array of UpgradeAction choices.
     */
    public static Array<UpgradeAction> getLevelUpChoices(final Player player) {
        if (player == null) {
            return new Array<>();
        }
        final Array<UpgradeAction> possibleChoices = new Array<>();

        // 1. Gather Weapon Choices
        if (weaponDataManager == null) {
            Gdx.app.error("LevelUpChoiceBuilder", "WeaponDataManager is null!");
        }
        final Array<String> weaponIds = weaponDataManager != null
                ? weaponDataManager.getAllWeaponIds()
                : new Array<>();
        for (final String id : weaponIds) {
            BaseWeapon weapon = null;
            for (final BaseWeapon w : player.getWeaponManager().getWeapons()) {
                if (w.getId().equalsIgnoreCase(id)) {
                    weapon = w;
                    break;
                }
            }

            if (weapon == null) {
                possibleChoices.add(new WeaponUpgradeAction(id, UpgradeCatalog.getWeaponName(id),
                        UpgradeCatalog.getWeaponLevelDescription(id, 1), true));
            } else if (weapon.getLevel() < 5) {
                final int nextLevel = weapon.getLevel() + 1;
                possibleChoices
                        .add(new WeaponUpgradeAction(id, UpgradeCatalog.getWeaponName(id) + " (Cấp " + nextLevel + ")",
                                UpgradeCatalog.getWeaponLevelDescription(id, nextLevel), false));
            }
        }

        // 2. Gather Gear Choices
        if (gearDataManager == null) {
            Gdx.app.error("LevelUpChoiceBuilder", "GearDataManager is null!");
        }
        final Array<String> gearIds = gearDataManager != null
                ? gearDataManager.getAllGearIds()
                : new Array<>();
        for (final String id : gearIds) {
            final Gear gear = player.getGearManager().getGear(id);
            if (gear == null) {
                possibleChoices.add(new GearUpgradeAction(id, UpgradeCatalog.getGearName(id),
                        UpgradeCatalog.getGearLevelDescription(id, 1), true));
            } else if (gear.getLevel() < 5) {
                final int nextLevel = gear.getLevel() + 1;
                possibleChoices
                        .add(new GearUpgradeAction(id, UpgradeCatalog.getGearName(id) + " (Cấp " + nextLevel + ")",
                                UpgradeCatalog.getGearLevelDescription(id, nextLevel), false));
            }
        }

        // Fallbacks if nothing is available
        if (possibleChoices.size == 0) {
            possibleChoices.add(new HealAction());
            possibleChoices.add(new DamageIncreaseAction());
        }

        possibleChoices.shuffle();
        final Array<UpgradeAction> finalChoices = new Array<>();
        for (int i = 0; i < Math.min(3, possibleChoices.size); i++) {
            finalChoices.add(possibleChoices.get(i));
        }
        return finalChoices;
    }
}
