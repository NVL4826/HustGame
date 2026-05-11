package hust.adventure.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.adventure.collision.CollisionManager;
import hust.adventure.entities.components.ChaseBehavior;

/**
 * Basic enemy that chases the player.
 */
public class NullPointerEnemy extends BaseEnemy {
 
    public NullPointerEnemy() {
        super();
    }
 
    public NullPointerEnemy(float x, float y, CollisionManager collisionManager) {
        super(x, y, 32, 32, 30, "NullPtr", Color.GREEN, collisionManager);
        setBehavior(new ChaseBehavior(60f));
    }
 
    @Override
    public void reset() {
        super.reset();
    }

    @Override
    protected void renderSpecific(SpriteBatch batch) {
        drawRect(batch, getX() - getWidth() / 2f, getY() - getHeight() / 2f, getWidth(), getHeight(), getColor());
    }
}
