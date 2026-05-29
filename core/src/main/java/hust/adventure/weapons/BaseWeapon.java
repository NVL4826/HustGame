package hust.adventure.weapons;

import hust.adventure.entities.Player;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.adventure.core.context.ProgressContext;

/**
 * Abstract base class for all weapons. Handles common functionality like cooldown management and level tracking.
 */
public abstract class BaseWeapon implements Weaponable {
    private final Player owner;
    private final WeaponConfig config;
    private float baseDamage;
    private float cooldown;
    private float area;
    private int level;
    private float currentCooldownTimer;
    private int amount;
    private int pierce;
    private float projectileInterval;
    private int shotsRemaining;
    private float shotTimer;

    public BaseWeapon(final Player owner, final WeaponConfig config) {
        if (owner == null) {
            throw new IllegalArgumentException("Owner cannot be null");
        }
        if (config == null) {
            throw new IllegalArgumentException("Config cannot be null");
        }
        this.owner = owner;
        this.config = config;
        this.level = 1;
        this.baseDamage = config.getBaseDamage();
        this.cooldown = config.getCooldown();
        this.area = config.getArea();
        this.currentCooldownTimer = 0;
        this.amount = 1;
        this.pierce = 1;
        this.projectileInterval = 0.1f;
        this.shotsRemaining = 0;
        this.shotTimer = 0f;
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

        if (shotsRemaining > 0) {
            shotTimer -= deltaTime;
            if (shotTimer <= 0) {
                executeAttackAction();
                shotsRemaining--;
                shotTimer = projectileInterval;
            }
        }

        if (isAutoFiring() && currentCooldownTimer <= 0 && shotsRemaining == 0) {
            fire();
        }
    }

    @Override
    public final void fire() {
        if (currentCooldownTimer <= 0) {
            if (amount <= 1) {
                executeAttackAction();
            } else {
                shotsRemaining = amount;
                shotTimer = 0f;
            }
            currentCooldownTimer = getCooldown();
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
        if (this.level >= config.getMaxLevel()) {
            return;
        }
        this.level++;
        this.baseDamage += damageBonus;
        this.cooldown = Math.max(0.1f, this.cooldown - cooldownReduction);

        if (config.getLevels() != null && config.getLevels().size >= this.level) {
            final WeaponLevelConfig levelCfg = config.getLevels().get(this.level - 1);
            if (levelCfg != null) {
                if (levelCfg.getBaseDamage() > 0) this.baseDamage = levelCfg.getBaseDamage();
                if (levelCfg.getCooldown() > 0) this.cooldown = levelCfg.getCooldown();
                if (levelCfg.getArea() > 0) this.area = levelCfg.getArea();
                if (levelCfg.getAmount() > 0) this.amount = levelCfg.getAmount();
                if (levelCfg.getPierce() > 0) this.pierce = levelCfg.getPierce();
            }
        }
    }

    @Override
    public final int getLevel() {
        return level;
    }

    public final Player getOwner() {
        return owner;
    }

    public final float getBaseDamage() {
        return baseDamage * owner.getPowerMultiplier();
    }

    public final float getEffectiveDamage() {
        return baseDamage * ProgressContext.instance.getDamageMultiplier();
    }

    public final void setBaseDamage(final float baseDamage) {
        this.baseDamage = baseDamage;
    }

    public final float getCooldown() {
        return cooldown * owner.getCooldownMultiplier();
    }

    public final void setCooldown(final float cooldown) {
        this.cooldown = cooldown;
    }

    public final float getArea() {
        return area * owner.getAreaMultiplier();
    }

    public final void setArea(final float area) {
        this.area = area;
    }

    public final int getAmount() {
        return amount;
    }

    public final void setAmount(final int amount) {
        this.amount = amount;
    }

    public final int getPierce() {
        return pierce;
    }

    public final void setPierce(final int pierce) {
        this.pierce = pierce;
    }

    public final float getProjectileInterval() {
        return projectileInterval;
    }

    public final void setProjectileInterval(final float projectileInterval) {
        this.projectileInterval = projectileInterval;
    }

    public final int getShotsRemaining() {
        return shotsRemaining;
    }

    protected final void setShotsRemaining(final int shotsRemaining) {
        this.shotsRemaining = shotsRemaining;
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
        return config.getId();
    }

    @Override
    public final String getName() {
        return config.getName();
    }

    @Override
    public final String getDescription() {
        return config.getDescription();
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
