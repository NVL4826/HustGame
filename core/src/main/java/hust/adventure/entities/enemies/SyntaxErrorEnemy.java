package hust.adventure.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.adventure.collision.CollisionManager;

import hust.adventure.entities.EntityManager;
import hust.adventure.entities.Player;
import hust.adventure.entities.components.FleeBehavior;

/**
 * Enemy that fires projectiles and stays at range.
 */
public class SyntaxErrorEnemy extends BaseEnemy {
    private float fireTimer = 0;

    public SyntaxErrorEnemy() {
        super();
    }

    public SyntaxErrorEnemy(float x, float y, CollisionManager collisionManager) {
        super(x, y, 30, 30, 25, "SyntaxErr", Color.ORANGE, collisionManager);
        setBehavior(new FleeBehavior(30f, 200f));
    }

    @Override
    public void reset() {
        super.reset();
        fireTimer = 0;
    }

    @Override
    public void handleUpdate(float delta, Player p, EntityManager em) {
        super.handleUpdate(delta, p, em);

        float dx = p.getX() - getX();
        float dy = p.getY() - getY();
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        // Map bounds check handled by CollisionManager
        CollisionManager cm = getCollisionManager();
        if (cm != null && cm.getMapWidth() > 0 && !cm.isInfinite()) {
            float minX = getWidth() / 2f;
            float maxX = cm.getMapWidth() - getWidth() / 2f;
            float minY = getHeight() / 2f;
            float maxY = cm.getMapHeight() - getHeight() / 2f;

            if (getX() < minX)
                setX(minX);
            if (getX() > maxX)
                setX(maxX);
            if (getY() < minY)
                setY(minY);
            if (getY() > maxY)
                setY(maxY);
        }

        fireTimer += delta;
        if (fireTimer >= 1.5f) {
            fireTimer = 0;
            if (dist > 0) {
                // Fire towards player
                float vx = (dx / dist) * 200;
                float vy = (dy / dist) * 200;
                getFactory().createProjectile(getX(), getY(), vx, vy, 10, Color.ORANGE, false);
            }
        }
    }

    @Override
    protected void renderSpecific(SpriteBatch batch) {
        drawRect(batch, getX() - getWidth() / 2f, getY() - getHeight() / 2f, getWidth(), getHeight(), getColor());
    }
}
