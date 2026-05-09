package hust.adventure.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hust.adventure.collision.CollisionManager;

import hust.adventure.entities.EntityManager;
import hust.adventure.entities.Player;

/**
 * Entity representing the final boss (T.H.T) in the Boss Room.
 */
public class FinalBoss extends BaseEnemy {
    private Texture texture;
    private static final float MAX_HP = 1000f;

    public FinalBoss(float x, float y, CollisionManager collisionManager, Texture texture) {
        super(x, y, 100, 100, MAX_HP, "T.H.T", Color.RED, collisionManager);
        this.texture = texture;
    }

    @Override
    public void handleUpdate(float delta, Player player, EntityManager entityManager) {
        // Boss logic (stationary for now)
    }

    @Override
    protected void renderSpecific(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, getX(), getY(), getWidth(), getHeight());
        }
    }
}
