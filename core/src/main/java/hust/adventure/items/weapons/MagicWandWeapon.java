package hust.adventure.items.weapons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import hust.adventure.collision.CollisionLayer;
import hust.adventure.entities.Player;
import hust.adventure.entities.base.GameEntity;
import hust.adventure.entities.combat.Projectile;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.GameEvent;
import hust.adventure.events.EventType;

/**
 * A magic wand weapon that fires projectiles at the nearest enemy.
 */
public class MagicWandWeapon extends BaseWeapon {
    private static final float PROJECTILE_SPEED = 200f;
    private static final float MAX_RANGE = 400f;

    public MagicWandWeapon(final Player owner, final float baseDamage, final float cooldown, final float area) {
        super(owner, "magic_wand", "Gậy phép", "Bắn tia phép vào kẻ địch gần nhất.", baseDamage, cooldown, area);
    }

    @Override
    protected void executeAttackAction() {
        final GameEntity target = getOwner().getCollisionManager().getNearestEntity(getOwner().getX(),
                getOwner().getY(), MAX_RANGE, CollisionLayer.ENEMY);

        if (target != null) {
            fireAt(target);
        }
    }

    private void fireAt(final GameEntity target) {
        final float startX = getOwner().getX();
        final float startY = getOwner().getY();

        final Vector2 direction = new Vector2(target.getX() - startX, target.getY() - startY).nor();
        final float vx = direction.x * PROJECTILE_SPEED;
        final float vy = direction.y * PROJECTILE_SPEED;

        if (getOwner().getFactory() != null) {
            final Projectile projectile = getOwner().getFactory().createProjectile(startX, startY, vx, vy,
                    getEffectiveDamage(), Color.CYAN, true);
            if (projectile != null) {
                projectile.setPierce(getPierce());
            }
            EventDispatcher.getInstance().dispatch(new GameEvent<>(EventType.PLAY_SFX, "audio/sfx/skill_e.mp3"));
        }
    }

    @Override
    public void upgrade(final float damageBonus, final float cooldownReduction) {
        if (getLevel() >= 5) {
            return;
        }
        super.upgrade(damageBonus, cooldownReduction);
        applyLevelStats();
    }

    private void applyLevelStats() {
        switch (getLevel()) {
        case 2:
            setAmount(2);
            break;
        case 3:
            setCooldown(1.0f);
            break;
        case 4:
            setAmount(3);
            break;
        case 5:
            setBaseDamage(20f);
            break;
        }
    }
}
