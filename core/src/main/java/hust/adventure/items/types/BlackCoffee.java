package hust.adventure.items.types;

import hust.adventure.effects.types.SpeedBoostEffect;
import hust.adventure.entities.base.BaseActor;
import hust.adventure.items.Consumable;

public class BlackCoffee extends BaseCoffee {
    @Override
    public String getId() { return "coffee_den"; }
    @Override
    public String getName() { return "Cà Phê Đen"; }
    @Override
    public String getDescription() { return "Hồi 10 HP, 30 Thể lực, tăng tốc trong 30s."; }

    @Override
    public void consume(BaseActor consumer) {
        super.consume(consumer);
        consumer.heal(10f);
        consumer.restoreStamina(30f);
        consumer.getStatusEffectManager().addEffect(new SpeedBoostEffect(30f, 1.3f));
    }
}
