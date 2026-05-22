package hust.adventure.items;

import java.util.Objects;

/**
 * Concrete class for all logical items.
 * Implements immutability and Flyweight pattern.
 */
public class BaseItem implements Item {
    private final String id;
    private final String name;
    private final String description;

    public BaseItem(final String id, final String name, final String description) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null");
        }
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        final Item item = (Item) o;
        return Objects.equals(getId(), item.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return getName() + " (" + getId() + ")";
    }
}
