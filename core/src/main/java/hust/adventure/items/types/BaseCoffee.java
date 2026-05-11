package hust.adventure.items.types;

import hust.adventure.core.context.ProgressContext;
import hust.adventure.entities.base.BaseActor;
import hust.adventure.items.BaseItem;
import hust.adventure.items.Consumable;

public abstract class BaseCoffee extends BaseItem implements Consumable {
    @Override
    public void consume(BaseActor consumer) {
        ProgressContext.instance.coffeeCount++;
    }
}
