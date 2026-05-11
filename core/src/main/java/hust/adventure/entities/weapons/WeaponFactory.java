package hust.adventure.entities.weapons;

import hust.adventure.entities.Player;
import hust.adventure.stats.StatType;
import java.util.EnumMap;
import java.util.Map;

/**
 * Factory for creating weapons.
 */
public class WeaponFactory {

    public static Weaponable createWeapon(final String id, final Player player) {
        final Map<StatType, Float> initialStats = new EnumMap<>(StatType.class);
        
        switch (id.toLowerCase()) {
            case "whip":
                initialStats.put(StatType.POWER, 10f);
                initialStats.put(StatType.COOLDOWN, 1.5f);
                initialStats.put(StatType.AREA, 1.0f);
                return new WhipWeapon(player, new WeaponStats(initialStats));
            case "magic_wand":
                initialStats.put(StatType.POWER, 8f);
                initialStats.put(StatType.COOLDOWN, 1.0f);
                initialStats.put(StatType.AREA, 1.0f);
                return new MagicWandWeapon(player, new WeaponStats(initialStats));
            case "garlic":
                initialStats.put(StatType.POWER, 2f);
                initialStats.put(StatType.COOLDOWN, 0.5f);
                initialStats.put(StatType.AREA, 60f); // Radius
                return new GarlicAuraWeapon(player, new WeaponStats(initialStats));
            default:
                throw new IllegalArgumentException("Unknown weapon id: " + id);
        }
    }
}
