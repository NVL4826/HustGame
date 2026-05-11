package hust.adventure.items.types;

import hust.adventure.entities.base.BaseActor;
import hust.adventure.items.BaseItem;
import hust.adventure.items.Consumable;

public class DriedChicken extends BaseItem implements Consumable {
    @Override
    public String getId() { return "kho_ga"; }
    @Override
    public String getName() { return "Khô gà lá chanh"; }
    @Override
    public String getDescription() { return "Hồi 25 HP."; }

    @Override
    public void consume(BaseActor consumer) {
        consumer.heal(25f);
    }
}
