package hust.adventure.items.equipment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import hust.adventure.items.gear.GearConfig;
import hust.adventure.core.data.GearDataLoader;
import hust.adventure.core.data.WeaponDataLoader;
import hust.adventure.entities.player.Player;
import hust.adventure.items.gear.Gear;
import hust.adventure.items.gear.GearFactory;
import hust.adventure.items.weapons.BaseWeapon;
import hust.adventure.items.weapons.WeaponFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Concrete implementation of the deep StudentEquipment module.
 * Centralizes weapon management, gear stat multipliers, upgrade evaluation, and persistence.
 */
public class DefaultStudentEquipment implements StudentEquipment {
    private final Player owner;
    private final WeaponFactory weaponFactory;
    private final GearFactory gearFactory;
    private final WeaponDataLoader weaponDataLoader;
    private final GearDataLoader gearDataLoader;

    private final Array<BaseWeapon> weapons = new Array<>();
    private final Array<Gear> gears = new Array<>();

    private float powerMultiplier = 1.0f;
    private float cooldownMultiplier = 1.0f;
    private float areaMultiplier = 1.0f;
    private float magnetMultiplier = 1.0f;
    private float speedMultiplier = 1.0f;

    public DefaultStudentEquipment(final Player owner,
                                  final WeaponFactory weaponFactory,
                                  final GearFactory gearFactory,
                                  final WeaponDataLoader weaponDataLoader,
                                  final GearDataLoader gearDataLoader) {
        this.owner = owner;
        this.weaponFactory = weaponFactory;
        this.gearFactory = gearFactory;
        this.weaponDataLoader = weaponDataLoader;
        this.gearDataLoader = gearDataLoader;
    }

    @Override
    public void update(final float delta) {
        for (int i = 0; i < weapons.size; i++) {
            weapons.get(i).updateTimer(delta);
        }
    }

    @Override
    public void addWeapon(final BaseWeapon weapon) {
        if (weapon == null) {
            throw new IllegalArgumentException("Weapon cannot be null");
        }
        if (!hasWeapon(weapon.getId())) {
            weapons.add(weapon);
        }
    }

    @Override
    public void removeWeapon(final String weaponId) {
        if (weaponId == null) {
            return;
        }
        for (int i = 0; i < weapons.size; i++) {
            if (weapons.get(i).getId().equalsIgnoreCase(weaponId)) {
                weapons.removeIndex(i);
                break;
            }
        }
    }

    @Override
    public BaseWeapon getWeapon(final String weaponId) {
        if (weaponId == null) {
            return null;
        }
        for (int i = 0; i < weapons.size; i++) {
            final BaseWeapon weapon = weapons.get(i);
            if (weapon.getId().equalsIgnoreCase(weaponId)) {
                return weapon;
            }
        }
        return null;
    }

    @Override
    public Array<BaseWeapon> getWeapons() {
        return weapons;
    }

    @Override
    public boolean hasWeapon(final String weaponId) {
        return getWeapon(weaponId) != null;
    }

    @Override
    public void addGear(final Gear gear) {
        if (gear == null) {
            throw new IllegalArgumentException("Gear cannot be null");
        }
        if (!hasGear(gear.getId())) {
            gears.add(gear);
            applyImmediateGearEquip(gear.getId(), 1);
            recalculateMultipliers();
        }
    }

    @Override
    public void removeGear(final String gearId) {
        if (gearId == null) {
            return;
        }
        for (int i = 0; i < gears.size; i++) {
            if (gears.get(i).getId().equalsIgnoreCase(gearId)) {
                gears.removeIndex(i);
                recalculateMultipliers();
                break;
            }
        }
    }

    @Override
    public Gear getGear(final String gearId) {
        if (gearId == null) {
            return null;
        }
        for (int i = 0; i < gears.size; i++) {
            final Gear gear = gears.get(i);
            if (gear.getId().equalsIgnoreCase(gearId)) {
                return gear;
            }
        }
        return null;
    }

    @Override
    public Array<Gear> getGears() {
        return gears;
    }

    @Override
    public boolean hasGear(final String gearId) {
        return getGear(gearId) != null;
    }

    @Override
    public float getPowerMultiplier() {
        return powerMultiplier;
    }

    @Override
    public float getCooldownMultiplier() {
        return cooldownMultiplier;
    }

    @Override
    public float getAreaMultiplier() {
        return areaMultiplier;
    }

    @Override
    public float getMagnetMultiplier() {
        return magnetMultiplier;
    }

    @Override
    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    private void recalculateMultipliers() {
        float power = 1.0f;
        float cooldown = 1.0f;
        float area = 1.0f;
        float magnet = 1.0f;
        float speed = 1.0f;

        for (int i = 0; i < gears.size; i++) {
            final Gear gear = gears.get(i);
            final GearConfig config = gearDataLoader != null ? gearDataLoader.getConfig(gear.getId()) : null;
            if (config != null && config.getOnEquipEffect() != null) {
                final String effect = config.getOnEquipEffect().toLowerCase();
                final float totalVal = config.getOnEquipValue() * gear.getLevel();
                switch (effect) {
                    case "add_power" -> power += totalVal;
                    case "reduce_cooldown" -> cooldown = Math.max(0.2f, cooldown - totalVal);
                    case "add_area" -> area += totalVal;
                    case "add_magnet" -> magnet += totalVal;
                    case "add_speed" -> speed += totalVal;
                    default -> {
                        // Other effects like increase_max_hp handled directly
                    }
                }
            }
        }

        this.powerMultiplier = power;
        this.cooldownMultiplier = cooldown;
        this.areaMultiplier = area;
        this.magnetMultiplier = magnet;
        this.speedMultiplier = speed;
        if (owner != null) {
            owner.setSpeedMultiplier(speed);
        }
    }

    private void applyImmediateGearEquip(final String gearId, final int levelCount) {
        if (owner == null || gearDataLoader == null || levelCount <= 0) {
            return;
        }
        final GearConfig config = gearDataLoader.getConfig(gearId);
        if (config != null && config.getOnEquipEffect() != null) {
            final String effect = config.getOnEquipEffect().toLowerCase();
            if ("increase_max_hp".equals(effect)) {
                owner.increaseMaxHp(config.getOnEquipValue() * levelCount);
            }
        }
    }

    @Override
    public List<UpgradeOption> getAvailableUpgrades(final int maxChoices) {
        final List<UpgradeOption> pool = new ArrayList<>();

        // 1. Weapon upgrade choices
        if (weaponDataLoader != null) {
            final Array<String> weaponIds = weaponDataLoader.getAllWeaponIds();
            for (int i = 0; i < weaponIds.size; i++) {
                final String id = weaponIds.get(i);
                final BaseWeapon weapon = getWeapon(id);
                if (weapon == null) {
                    pool.add(new UpgradeOption(
                            id,
                            weaponDataLoader.getWeaponName(id),
                            weaponDataLoader.getWeaponLevelDescription(id, 1),
                            true,
                            true,
                            1
                    ));
                } else if (weapon.getLevel() < 5) {
                    final int nextLevel = weapon.getLevel() + 1;
                    pool.add(new UpgradeOption(
                            id,
                            weaponDataLoader.getWeaponName(id) + " (Cấp " + nextLevel + ")",
                            weaponDataLoader.getWeaponLevelDescription(id, nextLevel),
                            false,
                            true,
                            nextLevel
                    ));
                }
            }
        }

        // 2. Gear upgrade choices
        if (gearDataLoader != null) {
            final Array<String> gearIds = gearDataLoader.getAllGearIds();
            for (int i = 0; i < gearIds.size; i++) {
                final String id = gearIds.get(i);
                final Gear gear = getGear(id);
                if (gear == null) {
                    pool.add(new UpgradeOption(
                            id,
                            gearDataLoader.getGearName(id),
                            gearDataLoader.getGearLevelDescription(id, 1),
                            true,
                            false,
                            1
                    ));
                } else if (gear.getLevel() < 5) {
                    final int nextLevel = gear.getLevel() + 1;
                    pool.add(new UpgradeOption(
                            id,
                            gearDataLoader.getGearName(id) + " (Cấp " + nextLevel + ")",
                            gearDataLoader.getGearLevelDescription(id, nextLevel),
                            false,
                            false,
                            nextLevel
                    ));
                }
            }
        }

        Collections.shuffle(pool);
        if (pool.size() > maxChoices && maxChoices > 0) {
            return pool.subList(0, maxChoices);
        }
        return pool;
    }

    @Override
    public boolean applyUpgrade(final String id) {
        if (id == null) {
            return false;
        }

        // 1. Upgrade existing weapon
        final BaseWeapon existingWeapon = getWeapon(id);
        if (existingWeapon != null) {
            if (existingWeapon.getLevel() < 5) {
                existingWeapon.upgrade(0f, 0f);
                return true;
            }
            return false;
        }

        // 2. Upgrade existing gear
        final Gear existingGear = getGear(id);
        if (existingGear != null) {
            if (existingGear.getLevel() < 5) {
                existingGear.upgrade();
                applyImmediateGearEquip(id, 1);
                recalculateMultipliers();
                return true;
            }
            return false;
        }

        // 3. Unlock new weapon
        if (weaponDataLoader != null && weaponDataLoader.getConfig(id) != null) {
            if (weaponFactory != null) {
                final BaseWeapon newWeapon = weaponFactory.createWeapon(id, owner);
                if (newWeapon != null) {
                    addWeapon(newWeapon);
                    return true;
                }
            }
        }

        // 4. Unlock new gear
        if (gearDataLoader != null && gearDataLoader.getConfig(id) != null) {
            if (gearFactory != null) {
                final Gear newGear = gearFactory.createGear(id);
                if (newGear != null) {
                    addGear(newGear);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Map<String, Integer> getWeaponLevels() {
        final Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < weapons.size; i++) {
            final BaseWeapon weapon = weapons.get(i);
            map.put(weapon.getId().toLowerCase(), weapon.getLevel());
        }
        return map;
    }

    @Override
    public Map<String, Integer> getGearLevels() {
        final Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < gears.size; i++) {
            final Gear gear = gears.get(i);
            map.put(gear.getId().toLowerCase(), gear.getLevel());
        }
        return map;
    }

    @Override
    public void restore(final Map<String, Integer> savedWeapons, final Map<String, Integer> savedGears) {
        weapons.clear();
        gears.clear();

        // 1. Restore weapons
        if (savedWeapons == null || savedWeapons.isEmpty()) {
            if (weaponFactory != null) {
                final BaseWeapon defaultWeapon = weaponFactory.createWeapon("bun_dau", owner);
                if (defaultWeapon != null) {
                    weapons.add(defaultWeapon);
                }
            }
            if (savedWeapons != null) {
                savedWeapons.put("bun_dau", 1);
            }
        } else {
            for (final Map.Entry<String, Integer> entry : savedWeapons.entrySet()) {
                final String weaponId = entry.getKey();
                final int targetLevel = entry.getValue();
                if (weaponFactory != null) {
                    final BaseWeapon weapon = weaponFactory.createWeapon(weaponId, owner);
                    if (weapon != null) {
                        for (int i = 1; i < targetLevel; i++) {
                            weapon.upgrade(0f, 0f);
                        }
                        weapons.add(weapon);
                    }
                }
            }
        }

        // 2. Restore gears
        if (savedGears != null) {
            for (final Map.Entry<String, Integer> entry : savedGears.entrySet()) {
                final String gearId = entry.getKey();
                final int targetLevel = entry.getValue();
                final Gear gear = gearFactory != null ? gearFactory.createGear(gearId) : new Gear(gearId, gearId, "", gearFactory);
                if (gear != null) {
                    gear.setLevel(Math.min(5, Math.max(1, targetLevel)));
                    gears.add(gear);
                    applyImmediateGearEquip(gearId, targetLevel);
                }
            }
        }

        recalculateMultipliers();
    }
}
