package hust.adventure.stats;

/**
 * Enumeration of all player statistics in the game.
 */
public enum StatType {
    // Survival Stats
    LEVEL,
    MAX_HP,
    ARMOR,
    REGEN,
    MOVE_SPEED,

    // Offensive Stats
    POWER,
    COOLDOWN,
    AREA,
    PROJECTILE_SPEED,
    DURATION,
    AMOUNT,

    // Utility & Economy Stats
    LUCK,
    GROWTH,
    GREED,
    CURSE,
    MAGNET,

    // Meta / UI Stats
    REVIVALS,
    REROLLS,
    SKIPS,
    BANISH
}
