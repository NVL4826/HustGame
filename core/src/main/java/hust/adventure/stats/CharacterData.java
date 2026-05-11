package hust.adventure.stats;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable metadata for a character definition.
 * Holds initial configuration for UI and stats initialization.
 */
public final class CharacterData {
    private final String charName;
    private final String textureName;
    private final String spriteName;
    private final String description;
    private final String startingWeaponId;
    private final Map<StatType, Float> baseStats;

    public CharacterData(
            final String charName,
            final String textureName,
            final String spriteName,
            final String description,
            final String startingWeaponId,
            final Map<StatType, Float> baseStats
    ) {
        this.charName = Objects.requireNonNull(charName);
        this.textureName = Objects.requireNonNull(textureName);
        this.spriteName = Objects.requireNonNull(spriteName);
        this.description = Objects.requireNonNull(description);
        this.startingWeaponId = Objects.requireNonNull(startingWeaponId);
        this.baseStats = Collections.unmodifiableMap(new EnumMap<>(Objects.requireNonNull(baseStats)));
    }

    public String getCharName() {
        return charName;
    }

    public String getTextureName() {
        return textureName;
    }

    public String getSpriteName() {
        return spriteName;
    }

    public String getDescription() {
        return description;
    }

    public String getStartingWeaponId() {
        return startingWeaponId;
    }

    public float getBaseStat(final StatType type) {
        return baseStats.getOrDefault(type, 0.0f);
    }

    public Map<StatType, Float> getBaseStats() {
        return baseStats;
    }
}
