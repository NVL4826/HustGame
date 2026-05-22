package hust.adventure.items.weapons;

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
            return new WhipWeapon(player, 10f, 1.5f, 1.0f);
        case "magic_wand":
            return new MagicWandWeapon(player, 8f, 1.0f, 1.0f);
        case "garlic":
            return new GarlicAuraWeapon(player, 2f, 0.5f, 60f);
        case "bun_dau":
            return new BunDauWeapon(player, 12f, 0.4f, 1.0f);
        default:
            throw new IllegalArgumentException("Unknown weapon id: " + id);
        }
    }
}
