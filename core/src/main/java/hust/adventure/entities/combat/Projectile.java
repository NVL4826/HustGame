package hust.adventure.entities.combat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Array;
import hust.adventure.entities.base.GameEntity;
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
    private int pierce = 1;
    private final Array<GameEntity> hitEntities = new Array<>();
    private static Texture bulletTexture;

    public Projectile() {
        super(0, 0, 30, 30);
    }

    public Projectile(float x, float y, float vx, float vy, float damage, Color color, boolean isPlayer) {
        super(x, y, 30, 30);
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
        this.pierce = 1;
        this.hitEntities.clear();
    }

    /** Lazily load the bullet texture the first time it is needed. */
    private static Texture getBulletTexture() {
        if (bulletTexture == null) {
            bulletTexture = new Texture(Gdx.files.internal("bullet.png"));
        }
        return bulletTexture;
    }

    /** Call this when the game shuts down to free the texture from GPU memory. */
    public static void disposeStaticResources() {
        if (bulletTexture != null) {
            bulletTexture.dispose();
            bulletTexture = null;
        }
    }

    @Override
    public void reset() {
        setDestroyed(false);
        vx = 0;
        vy = 0;
        damage = 0;
        color = Color.WHITE;
        isPlayerProjectile = false;
        pierce = 1;
        hitEntities.clear();
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
                        final BaseEnemy enemy = (BaseEnemy) other;
                        if (!hitEntities.contains(enemy, true)) {
                            hitEntities.add(enemy);
                            enemy.takeDamage(damage);
                            pierce--;
                            if (pierce <= 0) {
                                destroy();
                            }
                        }
                    }
                } else {
                    if (other instanceof Player) {
                        final Player player = (Player) other;
                        if (!hitEntities.contains(player, true)) {
                            hitEntities.add(player);
                            player.takeDamage(damage);
                            pierce--;
                            if (pierce <= 0) {
                                destroy();
                            }
                        }
                    }
                }
                // Walls or other obstacles can also destroy the projectile
                if (other.getCollider() != null && other.getCollider().getLayer() == CollisionLayer.WALL) {
                    destroy();
                }
            });
        }
    }

    public int getPierce() {
        return pierce;
    }

    public void setPierce(final int pierce) {
        this.pierce = pierce;
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
        Texture tex = getBulletTexture();

        float w = getWidth();
        float h = getHeight();
        float originX = w / 2f;
        float originY = h / 2f;

        // Calculate rotation angle from velocity vector.
        // The sprite faces RIGHT by default (0°); atan2 gives CCW angle from +X axis.
        float angle = 0f;
        if (vx != 0 || vy != 0) {
            angle = (float) Math.toDegrees(Math.atan2(vy, vx));
        }

        batch.setColor(Color.WHITE); // draw without tint so the sprite's own colours show
        batch.draw(
            tex,
            getX() - originX, getY() - originY, // position (bottom-left)
            originX, originY,                    // origin for rotation
            w, h,                                // size
            1f, 1f,                              // scale
            angle,                               // rotation in degrees
            0, 0,                                // source rect start
            tex.getWidth(), tex.getHeight(),     // source rect size
            false, false                         // flip
        );
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
