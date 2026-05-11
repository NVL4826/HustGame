package hust.adventure.events;

import com.badlogic.gdx.utils.Pool;

public class ExpGainedEvent implements Pool.Poolable {
    private float amount;

    public ExpGainedEvent() {
    }

    public void init(float amount) {
        this.amount = amount;
    }

    public float getAmount() {
        return amount;
    }

    @Override
    public void reset() {
        this.amount = 0;
    }
}
