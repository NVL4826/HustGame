package hust.adventure.entities.base;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import hust.adventure.collision.Collider;

/**
 * Core contract for all game entities.
 */
public interface GameEntity extends Disposable {
    void update(float delta);

    void draw(SpriteBatch batch);

    void drawHitbox(com.badlogic.gdx.graphics.glutils.ShapeRenderer sr);

    float getX();

    float getY();

    float getWidth();

    float getHeight();

    Rectangle getBounds();

    Collider getCollider();

    void setCollider(Collider collider);

    boolean isDestroyed();

    void destroy();

    Rectangle getMovementBounds(float x, float y, Rectangle out);
}
