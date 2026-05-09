package hust.adventure.entities.enemies;

import com.badlogic.gdx.Gdx;
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

/**
 * Base class for all enemy types. Inherits core living entity logic from BaseActor.
 */
public abstract class BaseEnemy extends BaseActor {
    private final String name;
    private final Color color;
    private final CollisionManager collisionManager;
    private EntityFactory factory;
    private AIBehavior behavior;

    public BaseEnemy(final float x, final float y, final float w, final float h, final float maxHp, final String name,
            final Color color, final CollisionManager collisionManager) {
        super(x, y, w, h, maxHp);
        this.name = name;
        this.color = color;
        this.collisionManager = collisionManager;
    }

    @Override
    public void setCollider(Collider collider) {
        super.setCollider(collider);
        if (collider != null) {
            collider.setListener(other -> {
                if (other instanceof Player && !(this instanceof SyntaxErrorEnemy)) {
                    ((Player) other).takeDamage(10 * Gdx.graphics.getDeltaTime());
                }
            });
        }
    }

    @Override
    public void update(final float delta) {
        super.update(delta);
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
        sr.rect(getX(), getY() + getHeight() + 5, (getHp() / getMaxHp()) * getWidth(), 5);

        batch.begin();
        font.draw(batch, name, getX(), getY() + getHeight() + 25);
        batch.end();
    }

    public final String getName() {
        return name;
    }

    public final Color getColor() {
        return color;
    }

    protected final CollisionManager getCollisionManager() {
        return collisionManager;
    }

    public final void setFactory(final EntityFactory factory) {
        this.factory = factory;
    }

    protected final EntityFactory getFactory() {
        return factory;
    }

}
