package hust.adventure.items;

import hust.adventure.entities.base.BaseActor;

/**
 * Interface for items that can be consumed by an actor.
 */
public interface Consumable extends Item {
    /**
     * Consumes the item, applying its effects to the consumer.
     * @param consumer The actor consuming the item.
     */
    void consume(BaseActor consumer);
}
