package hust.adventure.items;

import java.util.Objects;

/**
 * Base class for all logical items.
 * Implements immutability and Flyweight pattern.
 */
public abstract class BaseItem implements Item {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
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
