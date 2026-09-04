package hust.adventure.items.equipment;

import com.badlogic.gdx.utils.Array;
import hust.adventure.items.gear.Gear;
import hust.adventure.items.weapons.BaseWeapon;

import java.util.List;
import java.util.Map;

/**
 * Deep domain module consolidating Student weapons, passive gears, active stat multipliers,
 * academic upgrade evaluation, and persistence state behind a cohesive interface.
 */
public interface StudentEquipment {

    /**
     * Updates all equipped weapons and ticks auto-attack intervals.
     *
     * @param delta elapsed time in seconds
     */
    void update(float delta);

    /**
     * Equips an offensive study tool (Weapon) on the Student.
     *
     * @param weapon the weapon instance to equip
     */
    void addWeapon(BaseWeapon weapon);

    /**
     * Removes an equipped weapon by identifier.
     *
     * @param weaponId the unique weapon identifier
     */
    void removeWeapon(String weaponId);

    /**
     * Retrieves an equipped weapon by identifier.
     *
     * @param weaponId the unique weapon identifier
     * @return the BaseWeapon instance, or null if unequipped
     */
    BaseWeapon getWeapon(String weaponId);

    /**
     * Retrieves all currently equipped weapons.
     *
     * @return array of equipped weapons
     */
    Array<BaseWeapon> getWeapons();

    /**
     * Checks whether a weapon is currently equipped.
     *
     * @param weaponId the unique weapon identifier
     * @return true if equipped, false otherwise
     */
    boolean hasWeapon(String weaponId);

    /**
     * Equips a passive essential (Gear) on the Student and recalculates stat multipliers.
     *
     * @param gear the gear instance to equip
     */
    void addGear(Gear gear);

    /**
     * Removes an equipped gear by identifier and recalculates stat multipliers.
     *
     * @param gearId the unique gear identifier
     */
    void removeGear(String gearId);

    /**
     * Retrieves an equipped gear by identifier.
     *
     * @param gearId the unique gear identifier
     * @return the Gear instance, or null if unequipped
     */
    Gear getGear(String gearId);

    /**
     * Retrieves all currently equipped gears.
     *
     * @return array of equipped gears
     */
    Array<Gear> getGears();

    /**
     * Checks whether a gear is currently equipped.
     *
     * @param gearId the unique gear identifier
     * @return true if equipped, false otherwise
     */
    boolean hasGear(String gearId);

    /**
     * Gets the effective attack power multiplier derived from equipped gears.
     *
     * @return power multiplier
     */
    float getPowerMultiplier();

    /**
     * Gets the effective cooldown reduction multiplier derived from equipped gears.
     *
     * @return cooldown multiplier
     */
    float getCooldownMultiplier();

    /**
     * Gets the effective attack area multiplier derived from equipped gears.
     *
     * @return area multiplier
     */
    float getAreaMultiplier();

    /**
     * Gets the effective Knowledge Gem pickup magnet multiplier derived from equipped gears.
     *
     * @return magnet multiplier
     */
    float getMagnetMultiplier();

    /**
     * Gets the effective movement speed multiplier derived from equipped gears.
     *
     * @return speed multiplier
     */
    float getSpeedMultiplier();

    /**
     * Evaluates equipment state to generate a randomized selection of eligible Upgrade choices.
     *
     * @param maxChoices maximum number of choices to return
     * @return list of available upgrade options
     */
    List<UpgradeOption> getAvailableUpgrades(int maxChoices);

    /**
     * Applies an academic Upgrade by identifier, unlocking the item if unowned or advancing its tier.
     *
     * @param id the weapon or gear identifier
     * @return true if successfully applied, false otherwise
     */
    boolean applyUpgrade(String id);

    /**
     * Exports a snapshot map of weapon IDs and their levels for persistence.
     *
     * @return map of weapon IDs to current levels
     */
    Map<String, Integer> getWeaponLevels();

    /**
     * Exports a snapshot map of gear IDs and their levels for persistence.
     *
     * @return map of gear IDs to current levels
     */
    Map<String, Integer> getGearLevels();

    /**
     * Restores weapons and gears to target levels in a single operation.
     *
     * @param savedWeapons map of weapon IDs to target levels
     * @param savedGears map of gear IDs to target levels
     */
    void restore(Map<String, Integer> savedWeapons, Map<String, Integer> savedGears);
}
