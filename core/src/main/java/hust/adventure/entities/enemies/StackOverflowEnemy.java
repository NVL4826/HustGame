package hust.adventure.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.adventure.collision.CollisionManager;
import hust.adventure.entities.components.BouncingBehavior;
import hust.adventure.graphics.ShapeDrawUtils;

/**
 * Enemy that bounces around and splits on death.
 */
public class StackOverflowEnemy extends BaseEnemy {
    private boolean isSplit = false;
 
    public StackOverflowEnemy() {
        super();
    }
 
    public StackOverflowEnemy(float x, float y, CollisionManager collisionManager) {
        super(x, y, 40, 40, 50, "StackOvfl", Color.RED, collisionManager, 20f);
        setSpeed(25f);
        setBehavior(new BouncingBehavior(100, 100, 800, 600));
    }
 

    public boolean isSplit() {
        return isSplit;
    }

    public void setSplit(boolean split) {
        isSplit = split;
    }

    @Override
    public void destroy() {
        if (isDestroyed()) {
            return;
        }
        super.destroy();
        if (!isSplit && getFactory() != null) {
            final BaseEnemy e1 = (BaseEnemy) getFactory().createEnemy("stack_overflow", getX() - 30, getY());
            final BaseEnemy e2 = (BaseEnemy) getFactory().createEnemy("stack_overflow", getX() + 30, getY());
            if (e1 instanceof StackOverflowEnemy) {
                ((StackOverflowEnemy) e1).setSplit(true);
            }
            if (e2 instanceof StackOverflowEnemy) {
                ((StackOverflowEnemy) e2).setSplit(true);
            }
        }
    }

    @Override
    protected void renderSpecific(SpriteBatch batch) {
        ShapeDrawUtils.drawRect(batch, getX() - getWidth() / 2f, getY() - getHeight() / 2f, getWidth(), getHeight(), getColor());
    }
}
