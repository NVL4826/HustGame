package hust.adventure.entities.weapons;

import hust.adventure.stats.StatType;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Configuration for weapon statistics at a specific level.
 */
public final class WeaponStats {
    private final Map<StatType, Float> stats;

    public WeaponStats(final Map<StatType, Float> stats) {
        this.stats = Collections.unmodifiableMap(new EnumMap<>(stats));
    }

    public float getStat(final StatType type) {
        return stats.getOrDefault(type, 0.0f);
    }

    public Map<StatType, Float> getAllStats() {
        return stats;
    }
}
