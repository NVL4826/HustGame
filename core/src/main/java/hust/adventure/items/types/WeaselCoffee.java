package hust.adventure.items.types;

import hust.adventure.effects.types.ConfusionEffect;
import hust.adventure.effects.types.SpeedBoostEffect;
import hust.adventure.entities.base.BaseActor;
import hust.adventure.items.Consumable;

public class WeaselCoffee extends BaseCoffee {
    @Override
    public String getId() { return "coffee_chon"; }
    @Override
    public String getName() { return "Cà Phê Chồn"; }
    @Override
    public String getDescription() { return "Hồi 60 Thể lực, siêu tăng tốc nhưng gây ảo giác."; }

    @Override
    public void consume(BaseActor consumer) {
        super.consume(consumer);
        consumer.heal(5f);
        consumer.restoreStamina(60f);
        consumer.getStatusEffectManager().addEffect(new SpeedBoostEffect(45f, 1.5f));
        consumer.getStatusEffectManager().addEffect(new ConfusionEffect(45f));
    }
}
