package hust.adventure.entities.base;

/**
 * Interface for entities that can take damage and have HP.
 */
public interface Damageable {
    void takeDamage(float damage);
    float getHp();
    float getMaxHp();
    boolean isDead();
}
