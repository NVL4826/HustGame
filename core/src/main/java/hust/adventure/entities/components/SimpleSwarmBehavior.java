package hust.adventure.entities.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import hust.adventure.entities.EntityManager;
import hust.adventure.entities.Player;
import hust.adventure.entities.enemies.BaseEnemy;
import hust.adventure.collision.CollisionManager;

/**
 * CPU-optimized behavior for swarm enemies. Moves directly towards the player using a simple vector and respects walls.
 * Implements Poolable to avoid GC overhead.
 */
public class SimpleSwarmBehavior implements AIBehavior, Pool.Poolable {
    private final Vector2 tmpVector = new Vector2();
    private float speed;

    /**
     * Default constructor for pooling.
     */
    public SimpleSwarmBehavior() {
        this.speed = 0f;
    }

    public void init(final float speed) {
        this.speed = speed;
    }

    @Override
    public void execute(final BaseEnemy enemy, final float delta, final Player player,
            final EntityManager entityManager) {
        if (player == null || enemy == null || entityManager == null) {
            return;
        }

        // Calculate direction to player
        tmpVector.set(player.getX() - enemy.getX(), player.getY() - enemy.getY());
        final float distance = tmpVector.len();

        if (distance > 0) {
            // Normalize and scale by speed
            tmpVector.nor().scl(speed * delta);

            final float nextX = enemy.getX() + tmpVector.x;
            final float nextY = enemy.getY() + tmpVector.y;

            // Check collision with walls (static environment)
            // Enemies pass through each other to optimize CPU
            CollisionManager collisionManager = enemy.getCollisionManager();
            if (collisionManager != null && collisionManager.canMove(enemy, nextX, nextY)) {
                enemy.setX(nextX);
                enemy.setY(nextY);
            }
        }
    }

    @Override
    public void reset() {
        this.speed = 0f;
        this.tmpVector.setZero();
    }
}
