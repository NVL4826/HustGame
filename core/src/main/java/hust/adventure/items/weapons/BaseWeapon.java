package hust.adventure.items.weapons;

import hust.adventure.entities.Player;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Abstract base class for all weapons. Handles common functionality like cooldown management and level tracking.
 */
public abstract class BaseWeapon implements Weaponable {
    private final Player owner;
    private float baseDamage;
    private float cooldown;
    private float area;
    private int level;
    private float currentCooldownTimer;
    private final String id;
    private final String name;
    private final String description;

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
    public boolean isAutoFiring() {
        return true;
    }

    @Override
    public void updateTimer(final float deltaTime) {
        if (currentCooldownTimer > 0) {
            currentCooldownTimer -= deltaTime;
        }

        if (isAutoFiring() && currentCooldownTimer <= 0) {
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

    public final Player getOwner() {
        return owner;
    }

    public final float getBaseDamage() {
        return baseDamage;
    }

    public final void setBaseDamage(final float baseDamage) {
        this.baseDamage = baseDamage;
    }

    public final float getCooldown() {
        return cooldown;
    }

    public final void setCooldown(final float cooldown) {
        this.cooldown = cooldown;
    }

    public final float getArea() {
        return area;
    }

    public final void setArea(final float area) {
        this.area = area;
    }

    public final float getCurrentCooldownTimer() {
        return currentCooldownTimer;
    }

    protected final void setCurrentCooldownTimer(final float currentCooldownTimer) {
        this.currentCooldownTimer = currentCooldownTimer;
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
        if (this == o)
            return true;
        if (!(o instanceof Weaponable))
            return false;
        final Weaponable weapon = (Weaponable) o;
        return java.util.Objects.equals(getId(), weapon.getId());
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(getId());
    }
}
