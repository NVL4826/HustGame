package hust.adventure.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.adventure.collision.CollisionManager;

import hust.adventure.entities.components.ChaseBehavior;

/**
 * Boss for the library level.
 */
public class LibraryBoss extends BaseEnemy {
 
    public LibraryBoss() {
        super();
    }
 
    public LibraryBoss(float x, float y, CollisionManager collisionManager) {
        super(x, y, 64, 64, 500, "LibBoss", Color.DARK_GRAY, collisionManager);
        setBehavior(new ChaseBehavior(40f));
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
