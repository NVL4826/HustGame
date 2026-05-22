package hust.adventure.items;

import hust.adventure.core.context.ProgressContext;
import hust.adventure.entities.base.BaseActor;
import java.util.function.Consumer;

/**
 * A specific type of ConsumableItem that increments the coffee consumption count.
 */
public class CoffeeItem extends ConsumableItem {

    public CoffeeItem(final String id, final String name, final String description, final Consumer<BaseActor> consumptionEffect) {
        super(id, name, description, createCoffeeEffect(consumptionEffect));
    }

    private static Consumer<BaseActor> createCoffeeEffect(final Consumer<BaseActor> effect) {
        if (effect == null) {
            throw new IllegalArgumentException("Coffee effect cannot be null");
        }
        return consumer -> {
            ProgressContext.instance.setCoffeeCount(ProgressContext.instance.getCoffeeCount() + 1);
            effect.accept(consumer);
        };
    }
}
