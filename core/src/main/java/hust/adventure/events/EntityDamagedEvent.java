package hust.adventure.events;
 
import hust.adventure.entities.base.GameEntity;
 
public class EntityDamagedEvent {
    private final GameEntity entity;
    private final float amount;
    private final boolean isCrit;
 
    public EntityDamagedEvent(GameEntity entity, float amount, boolean isCrit) {
        if (entity == null) throw new NullPointerException("entity cannot be null");
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
}
