package hust.adventure.items.weapons;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import hust.adventure.entities.player.Player;
import hust.adventure.items.equipment.StudentEquipment;

/**
 * Adapter module maintaining backward compatibility for weapon operations while delegating
 * to the deepened StudentEquipment module when present.
 */
public class WeaponManager {
    private final Player player;
    private final StudentEquipment equipment;
    private final Array<BaseWeapon> legacyWeapons;

    public WeaponManager(final Player player) {
        this(player, null);
    }

    public WeaponManager(final Player player, final StudentEquipment equipment) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        this.player = player;
        this.equipment = equipment;
        this.legacyWeapons = (equipment == null) ? new Array<>() : null;
    }

    /**
     * Updates all equipped weapons.
     * 
     * @param delta Time elapsed.
     */
    public void update(final float delta) {
        if (equipment != null) {
            equipment.update(delta);
        } else {
            for (final BaseWeapon weapon : legacyWeapons) {
                weapon.updateTimer(delta);
            }
        }
    }

    /**
     * Draws effects for all weapons.
     * 
     * @param batch The SpriteBatch to use.
     */
    public void draw(final SpriteBatch batch) {
        final Array<BaseWeapon> currentWeapons = getWeapons();
        for (final BaseWeapon weapon : currentWeapons) {
            weapon.draw(batch);
        }
    }

    /**
     * Adds a new weapon to the player.
     * 
     * @param weapon The weapon to add.
     */
    public void addWeapon(final BaseWeapon weapon) {
        if (weapon == null) {
            throw new IllegalArgumentException("Weapon cannot be null");
        }
        if (equipment != null) {
            equipment.addWeapon(weapon);
        } else {
            legacyWeapons.add(weapon);
        }
    }

    public Array<BaseWeapon> getWeapons() {
        return equipment != null ? equipment.getWeapons() : legacyWeapons;
    }
}
