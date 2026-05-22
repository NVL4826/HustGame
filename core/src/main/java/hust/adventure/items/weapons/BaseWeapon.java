package hust.adventure.entities.weapons;

import hust.adventure.entities.Player;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Abstract base class for all weapons.
 * Handles common functionality like cooldown management and level tracking.
 */
public abstract class BaseWeapon implements Weaponable {
    protected final Player owner;
    protected float baseDamage;
    protected float cooldown;
    protected float area;
    protected int level;
    protected float currentCooldownTimer;
    protected final String id;
    protected final String name;
    protected final String description;

    public BaseWeapon(final Player owner, final String id, final String name, final String description, 
                      final float baseDamage, final float cooldown, final float area) {
        if (owner == null) {
            throw new IllegalArgumentException("Owner cannot be null");
        }
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null");
        }
        this.owner = owner;
        this.id = id;
        this.name = name;
        this.description = description;
        this.level = 1;
        this.baseDamage = baseDamage;
        this.cooldown = cooldown;
        this.area = area;
        this.currentCooldownTimer = 0;
    }

    @Override
    public void updateTimer(final float deltaTime) {
        if (currentCooldownTimer > 0) {
            currentCooldownTimer -= deltaTime;
        }
        
        if (currentCooldownTimer <= 0) {
            fire();
        }
    }

    @Override
    public final void fire() {
        if (currentCooldownTimer <= 0) {
            executeAttackAction();
            currentCooldownTimer = cooldown;
        }
    }

    /**
     * The actual attack logic to be implemented by concrete weapons.
     */
    protected abstract void executeAttackAction();

    @Override
    public void draw(SpriteBatch batch) {
        // Default implementation does nothing
    }

    @Override
    public void upgrade(final float damageBonus, final float cooldownReduction) {
        this.level++;
        this.baseDamage += damageBonus;
        this.cooldown = Math.max(0.1f, this.cooldown - cooldownReduction);
    }

    @Override
    public final int getLevel() {
        return level;
    }

    public void addBaseDamage(final float amount) {
        this.baseDamage += amount;
    }

    @Override
    public final String getId() {
        return id;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final String getDescription() {
        return description;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof hust.adventure.items.Item)) return false;
        final hust.adventure.items.Item item = (hust.adventure.items.Item) o;
        return java.util.Objects.equals(getId(), item.getId());
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(getId());
    }
}
