package hust.adventure.entities.weapons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import hust.adventure.collision.CollisionLayer;
import hust.adventure.entities.Player;
import hust.adventure.entities.base.GameEntity;

/**
 * A magic wand weapon that fires projectiles at the nearest enemy.
 */
public class MagicWandWeapon extends BaseWeapon {
    private static final float PROJECTILE_SPEED = 200f;
    private static final float MAX_RANGE = 400f;

    public MagicWandWeapon(final Player owner, final WeaponStats initialStats) {
        super(owner, initialStats);
    }

    @Override
    protected void executeAttackAction() {
        final GameEntity target = owner.getCollisionManager().getNearestEntity(owner.getX(), owner.getY(), MAX_RANGE,
                CollisionLayer.ENEMY);

        if (target != null) {
            fireAt(target);
        }
    }

    private void fireAt(final GameEntity target) {
        final float startX = owner.getX();
        final float startY = owner.getY();

        final Vector2 direction = new Vector2(target.getX() - startX, target.getY() - startY).nor();
        final float vx = direction.x * PROJECTILE_SPEED;
        final float vy = direction.y * PROJECTILE_SPEED;

        if (owner.getFactory() != null) {
            owner.getFactory().createProjectile(startX, startY, vx, vy, baseDamage, Color.CYAN, true);
        }
    }
}
