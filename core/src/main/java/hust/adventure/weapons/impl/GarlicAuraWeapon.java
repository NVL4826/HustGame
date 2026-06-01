package hust.adventure.weapons.impl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import hust.adventure.collision.CollisionLayer;
import hust.adventure.entities.base.MapObject;
import hust.adventure.entities.player.Player;
import hust.adventure.entities.base.Damageable;
import hust.adventure.graphics.ShapeDrawUtils;
import hust.adventure.weapons.BaseWeapon;
import hust.adventure.weapons.WeaponConfig;

/**
 * A garlic weapon that creates an aura damaging all nearby enemies periodically. Renders three rotating concentric
 * dashed rings around the player.
 */
public class GarlicAuraWeapon extends BaseWeapon {
    private float rotationAngle = 0f;

    public GarlicAuraWeapon(final Player owner, final WeaponConfig config) {
        super(owner, config);
    }

    @Override
    protected void executeAttackAction() {
        final float radius = getArea(); // Using 'area' stat as radius
        final Array<MapObject> targets = getOwner().getCollisionManager().getEntitiesInRadius(getOwner().getX(),
                getOwner().getY(), radius, CollisionLayer.ENEMY);

        for (final MapObject target : targets) {
            if (target instanceof Damageable) {
                ((Damageable) target).takeDamage(getBaseDamage());
            }
        }
    }

    @Override
    public void updateTimer(final float delta) {
        super.updateTimer(delta);
        rotationAngle += delta * 45f; // rotate 45 degrees per second
    }

    @Override
    public void draw(final SpriteBatch batch) {
        final float px = getOwner().getX();
        final float py = getOwner().getY();
        final float baseRadius = getArea();

        // Draw 3 rotating concentric circles
        ShapeDrawUtils.drawDashedCircle(batch, px, py, baseRadius * 0.6f, rotationAngle,
                new Color(0.85f, 0.95f, 0.75f, 0.3f));
        ShapeDrawUtils.drawDashedCircle(batch, px, py, baseRadius * 0.8f, -rotationAngle * 0.7f,
                new Color(0.85f, 0.95f, 0.75f, 0.25f));
        ShapeDrawUtils.drawDashedCircle(batch, px, py, baseRadius * 1.0f, rotationAngle * 0.4f,
                new Color(0.85f, 0.95f, 0.75f, 0.15f));
    }

}
