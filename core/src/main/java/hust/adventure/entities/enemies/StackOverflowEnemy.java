package hust.adventure.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.adventure.collision.CollisionManager;

import hust.adventure.entities.components.BouncingBehavior;

/**
 * Enemy that bounces around and splits on death.
 */
public class StackOverflowEnemy extends BaseEnemy {
    private boolean isSplit = false;

    public StackOverflowEnemy(float x, float y, CollisionManager collisionManager) {
        super(x, y, 40, 40, 50, "StackOvfl", Color.RED, collisionManager);
        setBehavior(new BouncingBehavior(100, 100, 800, 600));
    }

    public boolean isSplit() {
        return isSplit;
    }

    public void setSplit(boolean split) {
        isSplit = split;
    }

    @Override
    protected void renderSpecific(SpriteBatch batch) {
        drawRect(batch, getX() - getWidth() / 2f, getY() - getHeight() / 2f, getWidth(), getHeight(), getColor());
    }
}
