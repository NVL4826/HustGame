package hust.adventure.weapons;

import hust.adventure.entities.Player;

/**
 * Factory for creating weapons.
 */
public class WeaponFactory {

    public static Weaponable createWeapon(final String id, final Player player) {
        if (id == null) {
            throw new IllegalArgumentException("Weapon ID cannot be null");
        }
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }

        switch (id.toLowerCase()) {
        case "whip":
            return new WhipWeapon(player, 10f, 1.35f, 1.0f);
        case "magic_wand":
            return new MagicWandWeapon(player, 10f, 1.2f, 1.0f);
        case "garlic":
            return new GarlicAuraWeapon(player, 5f, 1.3f, 60f);
        case "bun_dau":
            return new BunDauWeapon(player, 6.5f, 1.0f, 1.0f);
        default:
            throw new IllegalArgumentException("Unknown weapon id: " + id);
        }
    }
}
