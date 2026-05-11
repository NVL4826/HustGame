package hust.adventure.items.types;

import hust.adventure.effects.types.RegenEffect;
import hust.adventure.effects.types.SpeedBoostEffect;
import hust.adventure.entities.base.BaseActor;

public class MilkCoffee extends BaseCoffee {
    @Override
    public String getId() {
        return "coffee_sua";
    }

    @Override
    public String getName() {
        return "Cà Phê Sữa";
    }

    @Override
    public String getDescription() {
        return "Hồi 20 HP, 25 Thể lực, tăng tốc và hồi máu theo thời gian.";
    }

    @Override
    public void consume(BaseActor consumer) {
        super.consume(consumer);
        consumer.heal(20f);
        consumer.restoreStamina(25f);
        consumer.getStatusEffectManager().addEffect(new SpeedBoostEffect(20f, 1.3f));
        consumer.getStatusEffectManager().addEffect(new RegenEffect(20f, 1f));
    }
}
