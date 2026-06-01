package hust.adventure.weapons.impl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import hust.adventure.collision.CollisionLayer;
import hust.adventure.entities.base.MapObject;
import hust.adventure.entities.combat.Projectile;
import hust.adventure.entities.player.Player;
import hust.adventure.events.EventDispatcher;
import hust.adventure.events.GameEvent;
import hust.adventure.weapons.BaseWeapon;
import hust.adventure.weapons.WeaponConfig;
import hust.adventure.events.EventType;

/**
 * A magic wand weapon that fires projectiles at the nearest enemy.
 */
public class MagicWandWeapon extends BaseWeapon {
    private static final float PROJECTILE_SPEED = 200f;
    private static final float MAX_RANGE = 400f;

    public MagicWandWeapon(final Player owner, final WeaponConfig config) {
        super(owner, config);
    }

    @Override
    protected void executeAttackAction() {
        final MapObject target = getOwner().getCollisionManager().getNearestEntity(getOwner().getX(), getOwner().getY(),
                MAX_RANGE, CollisionLayer.ENEMY);

        if (target != null) {
            fireAt(target);
        }
    }

    private void fireAt(final MapObject target) {
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
            EventDispatcher.getInstance().dispatch(new GameEvent<>(EventType.PLAY_SFX, "audio/sfx/magic.wav"));
        }
    }

}
