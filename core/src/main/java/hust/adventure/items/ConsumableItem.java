package hust.adventure.items;

import hust.adventure.entities.base.BaseActor;
import java.util.function.Consumer;

/**
 * A concrete item that can be consumed.
 * Uses a Consumer function to execute its custom consumption logic.
 */
public class ConsumableItem extends BaseItem implements Consumable {
    private final Consumer<BaseActor> consumptionEffect;

    public ConsumableItem(final String id, final String name, final String description, final Consumer<BaseActor> consumptionEffect) {
        super(id, name, description);
        if (consumptionEffect == null) {
            throw new IllegalArgumentException("Consumption effect cannot be null");
        }
        this.consumptionEffect = consumptionEffect;
    }

    @Override
    public void consume(final BaseActor consumer) {
        if (consumer == null) {
            throw new IllegalArgumentException("Consumer cannot be null");
        }
        consumptionEffect.accept(consumer);
    }
}
