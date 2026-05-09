package hust.adventure.items.types;

import hust.adventure.core.ProgressContext;
import hust.adventure.entities.base.BaseActor;
import hust.adventure.items.Consumable;

public abstract class BaseCoffee implements Consumable {
    @Override
    public void consume(BaseActor consumer) {
        ProgressContext.instance.coffeeCount++;
    }
}
