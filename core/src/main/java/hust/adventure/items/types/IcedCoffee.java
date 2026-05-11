package hust.adventure.items.types;

import hust.adventure.entities.base.BaseActor;

public class IcedCoffee extends BaseCoffee {
    @Override
    public String getId() {
        return "coffee_da";
    }

    @Override
    public String getName() {
        return "Cà Phê Đá";
    }

    @Override
    public String getDescription() {
        return "Hồi 10 Thể lực.";
    }

    @Override
    public void consume(BaseActor consumer) {
        super.consume(consumer);
        consumer.restoreStamina(10f);
    }
}
