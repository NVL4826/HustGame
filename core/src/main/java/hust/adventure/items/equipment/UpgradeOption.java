package hust.adventure.items.equipment;

/**
 * Immutable descriptor representing an available academic Upgrade choice for the Student.
 *
 * @param id the unique weapon or gear identifier
 * @param name the display name of the upgrade
 * @param description description of the upgrade effects
 * @param isUnlock true if unlocking an unowned item, false if upgrading an equipped item
 * @param isWeapon true for offensive study tools (Weapons), false for passive essentials (Gear)
 * @param targetLevel the resulting level after applying this upgrade
 */
public record UpgradeOption(
        String id,
        String name,
        String description,
        boolean isUnlock,
        boolean isWeapon,
        int targetLevel
) {}
