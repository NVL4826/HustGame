package hust.adventure.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.adventure.collision.CollisionManager;
import hust.adventure.entities.components.FleeBehavior;
import hust.adventure.graphics.ShapeDrawUtils;

/**
 * Enemy that flees from the player.
 */
public class InfiniteLoopEnemy extends BaseEnemy {
 
    public InfiniteLoopEnemy() {
        super();
    }
 
    public InfiniteLoopEnemy(float x, float y, CollisionManager collisionManager) {
        super(x, y, 32, 32, 200, "InfLoop", Color.PURPLE, collisionManager, 15f);
        setSpeed(80f);
        setBehavior(new FleeBehavior(150f));
    }
 

    @Override
    protected void renderSpecific(SpriteBatch batch) {
        ShapeDrawUtils.drawRect(batch, getX() - getWidth() / 2f, getY() - getHeight() / 2f, getWidth(), getHeight(), getColor());
    }
}
