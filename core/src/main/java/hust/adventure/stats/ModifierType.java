package hust.adventure.stats;

/**
 * Defines how a modifier affects a base stat value.
 */
public enum ModifierType {
    /**
     * Added directly to the base value.
     */
    FLAT,

    /**
     * Multiplied as a percentage of the base value.
     * Multiple percentage modifiers are additive before being applied.
     */
    PERCENTAGE
}
