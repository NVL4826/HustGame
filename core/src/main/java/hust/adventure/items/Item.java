package hust.adventure.items;

/**
 * Common interface for all logical items in the game.
 * Implementations MUST be immutable and follow the Flyweight pattern.
 * State (like quantity) should be managed by the Inventory, not the Item itself.
 */
public interface Item {
    String getId();
    String getName();
    String getDescription();
    String getSpritePath();
}
