package hust.adventure.items;

/**
 * Represents a passive Gear item in the player's inventory that provides stat boosts.
 * Supports levels 1 through 5.
 */
public class Gear extends EquipmentItem {
    private int level;

    public Gear(final String id, final String name, final String description) {
        super(id, name, description, null);
        this.level = 1;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(final int level) {
        if (level < 1 || level > 5) {
            throw new IllegalArgumentException("Gear level must be between 1 and 5");
        }
        this.level = level;
    }

    public void upgrade() {
        if (level < 5) {
            level++;
        }
    }
}
