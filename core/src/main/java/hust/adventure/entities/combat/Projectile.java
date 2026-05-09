package hust.adventure.entities.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import hust.adventure.entities.Player;
import hust.adventure.entities.base.BaseEntity;
import hust.adventure.entities.enemies.BaseEnemy;
import hust.adventure.collision.CollisionLayer;
import hust.adventure.collision.Collider;

/**
 * Represents a projectile fired by a player or enemy.
 */
public class Projectile extends BaseEntity {
    private float vx, vy;
    private Color color;
    private boolean isPlayerProjectile;
    private float damage;

    public Projectile(float x, float y, float vx, float vy, float damage, Color color, boolean isPlayer) {
        super(x, y, 10, 10);
        this.vx = vx;
        this.vy = vy;
        this.damage = damage;
        this.color = color;
        this.isPlayerProjectile = isPlayer;
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
        setX(getX() + vx * delta);
        setY(getY() + vy * delta);

        // Destroy if far off screen (assuming a safe margin)
        if (getX() < -500 || getX() > 2500 || getY() < -500 || getY() > 2500) {
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
        // No resources to dispose
    }

    public boolean isPlayerProjectile() {
        return isPlayerProjectile;
    }

    public float getDamage() {
        return damage;
    }
}
