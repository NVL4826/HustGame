package hust.adventure.entities.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.utils.Pool;
import hust.adventure.entities.Player;
import hust.adventure.entities.base.BaseEntity;
import hust.adventure.entities.enemies.BaseEnemy;
import hust.adventure.collision.CollisionLayer;
import hust.adventure.collision.Collider;
import hust.adventure.core.context.ProgressContext;

/**
 * Represents a projectile fired by a player or enemy.
 */
public class Projectile extends BaseEntity implements Pool.Poolable {
    private float vx, vy;
    private Color color;
    private boolean isPlayerProjectile;
    private float damage;
    private float startX, startY;
    private static final float MAX_RANGE = 2000f;

    public Projectile() {
        super(0, 0, 10, 10);
    }

    public Projectile(float x, float y, float vx, float vy, float damage, Color color, boolean isPlayer) {
        super(x, y, 10, 10);
        init(x, y, vx, vy, damage, color, isPlayer);
    }

    public void init(float x, float y, float vx, float vy, float damage, Color color, boolean isPlayer) {
        setX(x);
        setY(y);
        this.vx = vx;
        this.vy = vy;
        this.damage = damage;
        this.color = color;
        this.isPlayerProjectile = isPlayer;
        this.startX = x;
        this.startY = y;
        setDestroyed(false);
    }

    @Override
    public void reset() {
        setDestroyed(false);
        vx = 0;
        vy = 0;
        damage = 0;
        color = Color.WHITE;
        isPlayerProjectile = false;
        if (getCollider() != null) {
            getCollider().setListener(null);
        }
    }

    @Override
    public void setCollider(Collider collider) {
        super.setCollider(collider);
        if (collider != null) {
            collider.setListener(other -> {
                if (isPlayerProjectile) {
                    if (other instanceof BaseEnemy) {
                        ((BaseEnemy) other).takeDamage(damage);
                        destroy();
                    }
                } else {
                    if (other instanceof Player) {
                        ((Player) other).takeDamage(damage);
                        destroy();
                    }
                }
                // Walls or other obstacles can also destroy the projectile
                if (other.getCollider() != null && other.getCollider().getLayer() == CollisionLayer.WALL) {
                    destroy();
                }
            });
        }
    }

    @Override
    public void update(float delta) {
        float localDelta = delta;
        if (!isPlayerProjectile) {
            localDelta *= ProgressContext.instance.getEnemyTimeScale();
        }
        setX(getX() + vx * localDelta);
        setY(getY() + vy * localDelta);

        // Destroy if traveled too far from spawn point
        if (Math.abs(getX() - startX) > MAX_RANGE || Math.abs(getY() - startY) > MAX_RANGE) {
            destroy();
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        drawRect(batch, getX() - getWidth() / 2f, getY() - getHeight() / 2f, getWidth(), getHeight(), color);
    }

    public void drawDebug(ShapeRenderer sr) {
        sr.setColor(color);
        sr.rect(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void dispose() {
        // No native resources to clean up here.
    }

    public boolean isPlayerProjectile() {
        return isPlayerProjectile;
    }

    public float getDamage() {
        return damage;
    }
}
