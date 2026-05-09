package hust.adventure.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import hust.adventure.collision.CollisionManager;

import hust.adventure.entities.EntityManager;
import hust.adventure.entities.Player;
import hust.adventure.entities.components.FleeBehavior;

/**
 * Enemy that fires projectiles and stays at range.
 */
public class SyntaxErrorEnemy extends BaseEnemy {
    private float fireTimer = 0;

    public SyntaxErrorEnemy(float x, float y, CollisionManager collisionManager) {
        super(x, y, 30, 30, 25, "SyntaxErr", Color.ORANGE, collisionManager);
        setBehavior(new FleeBehavior(30f, 200f));
    }

    @Override
    public void handleUpdate(float delta, Player p, EntityManager em) {
        super.handleUpdate(delta, p, em);

        float dx = p.getX() - getX();
        float dy = p.getY() - getY();
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        // Clamp to screen
        setX(MathUtils.clamp(getX(), 0, 800 - getWidth()));
        setY(MathUtils.clamp(getY(), 0, 600 - getHeight()));

        fireTimer += delta;
        if (fireTimer >= 1.5f) {
            fireTimer = 0;
            if (dist > 0) {
                // Fire towards player
                float vx = (dx / dist) * 200;
                float vy = (dy / dist) * 200;
                em.addEntity(getFactory().createProjectile(getX(), getY(), vx, vy, 10, Color.ORANGE, false));
            }
        }
    }

    @Override
    protected void renderSpecific(SpriteBatch batch) {
        drawRect(batch, getX() - getWidth() / 2f, getY() - getHeight() / 2f, getWidth(), getHeight(), getColor());
    }
}
