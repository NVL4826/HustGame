package hust.adventure.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import hust.adventure.collision.CollisionManager;
import hust.adventure.collision.Collider;

import hust.adventure.entities.EntityManager;
import hust.adventure.entities.Player;
import hust.adventure.entities.base.BaseActor;
import hust.adventure.entities.components.AIBehavior;
import hust.adventure.entities.factory.EntityFactory;
import hust.adventure.entities.base.Damageable;
import hust.adventure.core.context.ProgressContext;

/**
 * Base class for all enemy types. Inherits core living entity logic from BaseActor.
 */
public abstract class BaseEnemy extends BaseActor {
    private static final float HP_BAR_OFFSET_Y = 5f;
    private static final float HP_BAR_HEIGHT = 5f;
    private static final float NAME_TEXT_OFFSET_Y = 25f;
    private static final float FLASHLIGHT_RADIUS_SQ = 10000f; // 100f * 100f

    private String name;
    private Color color;
    private CollisionManager collisionManager;
    private EntityFactory factory;
    private AIBehavior behavior;
    private float contactDamage;

    public BaseEnemy() {
        super();
    }

    public BaseEnemy(final float x, final float y, final float w, final float h, final float maxHp, final String name,
            final Color color, final CollisionManager collisionManager) {
        super(x, y, w, h, maxHp);
        init(x, y, w, h, maxHp, name, color, collisionManager, 0f);
    }

    public BaseEnemy(final float x, final float y, final float w, final float h, final float maxHp, final String name,
            final Color color, final CollisionManager collisionManager, final float contactDamage) {
        super(x, y, w, h, maxHp);
        init(x, y, w, h, maxHp, name, color, collisionManager, contactDamage);
    }

    /**
     * Sets state for the enemy.
     */
    public void init(final float x, final float y, final float w, final float h, final float maxHp, final String name,
            final Color color, final CollisionManager collisionManager, final float contactDamage) {
        this.width = w;
        this.height = h;
        setX(x);
        setY(y);
        this.setMaxHp(maxHp);
        this.setHp(maxHp);
        this.name = name;
        this.color = color;
        this.collisionManager = collisionManager;
        this.contactDamage = contactDamage;
        setDestroyed(false);
    }

    @Override
    public void setCollider(Collider collider) {
        super.setCollider(collider);
        if (collider != null) {
            collider.setListener(other -> {
                if (contactDamage > 0 && other instanceof Damageable) {
                    ((Damageable) other).takeDamage(contactDamage);
                }
            });
        }
    }

    @Override
    public void update(final float delta) {
        super.update(delta * ProgressContext.instance.getEnemyTimeScale());
        if (isDead()) {
            destroy();
        }
    }

    /**
     * Executes AI behavior logic.
     */
    public void handleUpdate(final float delta, final Player player, final EntityManager entityManager) {
        if (isDead())
            return;

        if (behavior != null) {
            behavior.execute(this, delta, player, entityManager);
        }
    }

    public final void setBehavior(final AIBehavior behavior) {
        this.behavior = behavior;
    }

    @Override
    public final void draw(final SpriteBatch batch) {
        if (isDead())
            return;
        
        // Flashlight culling in lights out mode (if showEnemiesTimer / radar is not active)
        if (ProgressContext.instance.isLightsOut()
                && ProgressContext.instance.getShowEnemiesTimer() <= 0f) {
            final Player p = ProgressContext.instance.getPlayer();
            if (p != null) {
                final float dx = getX() - p.getX();
                final float dy = getY() - p.getY();
                if ((dx * dx + dy * dy) > FLASHLIGHT_RADIUS_SQ) {
                    return; // Skip rendering
                }
            }
        }

        // Blink effect: skip render khi enemyBlinkVisible == false
        if (!ProgressContext.instance.isEnemyBlinkVisible())
            return;
        
        renderSpecific(batch);
    }

    /**
     * Template method for subclass-specific rendering.
     */
    protected abstract void renderSpecific(SpriteBatch batch);

    public void drawDebug(final ShapeRenderer sr, final SpriteBatch batch, final BitmapFont font) {
        sr.setColor(color);
        sr.rect(getX(), getY(), getWidth(), getHeight());

        // HP Bar
        sr.setColor(Color.GREEN);
        sr.rect(getX(), getY() + getHeight() + HP_BAR_OFFSET_Y, (getHp() / getMaxHp()) * getWidth(), HP_BAR_HEIGHT);

        // Note: batch.begin()/end() should be called by the caller of this method
        // to avoid multiple flushes during batch processing of multiple entities.
        font.draw(batch, name, getX(), getY() + getHeight() + NAME_TEXT_OFFSET_Y);
    }

    public final String getName() {
        return name;
    }

    public final Color getColor() {
        return color;
    }

    public final CollisionManager getCollisionManager() {
        return collisionManager;
    }

    public final void setFactory(final EntityFactory factory) {
        this.factory = factory;
    }

    protected final EntityFactory getFactory() {
        return factory;
    }

}
