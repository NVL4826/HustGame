package hust.adventure.events;

import com.badlogic.gdx.utils.Pool;
import hust.adventure.entities.base.GameEntity;

public class EntityDamagedEvent implements Pool.Poolable {
    private GameEntity entity;
    private float amount;
    private boolean isCrit;

    public EntityDamagedEvent() {
    }

    public void init(GameEntity entity, float amount, boolean isCrit) {
        this.entity = entity;
        this.amount = amount;
        this.isCrit = isCrit;
    }

    public GameEntity getEntity() {
        return entity;
    }

    public float getAmount() {
        return amount;
    }

    public boolean isCrit() {
        return isCrit;
    }

    @Override
    public void reset() {
        this.entity = null;
        this.amount = 0;
        this.isCrit = false;
    }
}
