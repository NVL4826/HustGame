package hust.adventure.entities.base;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import hust.adventure.collision.Collider;
import hust.adventure.entities.state.EntityState;
import hust.adventure.entities.state.IdleState;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

/**
 * Base implementation of a game entity. Provides positioning, bounding box, and basic state management.
 */
public abstract class BaseEntity implements GameEntity, Collidable {
    protected float x;
    protected float y;
    protected final float width;
    protected final float height;
    protected final Rectangle bounds;
    private boolean isDestroyed;
    private EntityState state;
    private Collider collider;
    private static Texture whitePixel;

    public BaseEntity(final float x, final float y, final float width, final float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle(x - width / 2f, y - height / 2f, width, height);
        this.isDestroyed = false;
        this.state = new IdleState();
    }

    @Override
    public abstract void update(float delta);

    @Override
    public abstract void draw(SpriteBatch batch);

    @Override
    public void destroy() {
        this.isDestroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

    public final float getX() {
        return x;
    }

    public void setX(final float x) {
        this.x = x;
        updateBounds();
    }

    public final float getY() {
        return y;
    }

    public void setY(final float y) {
        this.y = y;
        updateBounds();
    }

    public final float getWidth() {
        return width;
    }

    public final float getHeight() {
        return height;
    }

    @Override
    public final Rectangle getBounds() {
        return bounds;
    }

    /**
     * Synchronize collision bounds with current position.
     */
    protected final void updateBounds() {
        this.bounds.set(x - width / 2f, y - height / 2f, width, height);
    }

    public final EntityState getState() {
        return state;
    }

    public void setState(final EntityState newState) {
        if (newState == null)
            throw new IllegalArgumentException("State cannot be null");

        if (this.state != null) {
            this.state.exit(this);
        }
        this.state = newState;
        this.state.enter(this);
    }

    @Override
    public final Collider getCollider() {
        return collider;
    }

    @Override
    public void setCollider(final Collider collider) {
        this.collider = collider;
    }

    @Override
    public void dispose() {
    }

    protected void drawRect(SpriteBatch batch, float x, float y, float width, float height, Color color) {
        if (whitePixel == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            whitePixel = new Texture(pixmap);
            pixmap.dispose();
        }
        batch.setColor(color);
        batch.draw(whitePixel, x, y, width, height);
        batch.setColor(Color.WHITE);
    }
}
