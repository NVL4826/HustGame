package hust.adventure.items;

/**
 * Represents a passive Gear item in the player's inventory that provides stat boosts.
 * Supports levels 1 through 5.
 */
public class Gear {
    private final String id;
    private final String name;
    private final String description;
    private int level;

    public Gear(final String id, final String name, final String description) {
        if (id == null) {
            throw new IllegalArgumentException("Gear ID cannot be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Gear Name cannot be null");
        }
        if (description == null) {
            throw new IllegalArgumentException("Gear Description cannot be null");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.level = 1;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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
