package hust.adventure.entities.weapons;

import hust.adventure.entities.Player;
import hust.adventure.stats.StatType;

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

    public BaseWeapon(final Player owner, final WeaponStats initialStats) {
        if (owner == null) {
            throw new IllegalArgumentException("Owner cannot be null");
        }
        this.owner = owner;
        this.level = 1;
        applyStats(initialStats);
        this.currentCooldownTimer = 0;
    }

    @Override
    public void updateTimer(final float deltaTime) {
        if (currentCooldownTimer > 0) {
            currentCooldownTimer -= deltaTime;
        }
        
        // Auto-fire logic can be implemented here if needed, 
        // but typically the Manager or Player calls fire().
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
    public void upgrade(final WeaponStats nextLevelStats) {
        this.level++;
        applyStats(nextLevelStats);
    }

    protected void applyStats(final WeaponStats stats) {
        this.baseDamage = stats.getStat(StatType.POWER);
        this.cooldown = stats.getStat(StatType.COOLDOWN);
        this.area = stats.getStat(StatType.AREA);
    }

    @Override
    public final int getLevel() {
        return level;
    }

    public void addBaseDamage(float amount) {
        this.baseDamage += amount;
    }
}
