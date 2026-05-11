package hust.adventure.items.types;

import hust.adventure.entities.base.BaseActor;
import hust.adventure.items.BaseItem;
import hust.adventure.items.Consumable;

public class EnergyDrink extends BaseItem implements Consumable {
    @Override
    public String getId() { return "energy_drink"; }
    @Override
    public String getName() { return "Nước tăng lực"; }
    @Override
    public String getDescription() { return "Hồi 40 Thể lực."; }

    @Override
    public void consume(BaseActor consumer) {
        consumer.restoreStamina(40f);
    }
}
