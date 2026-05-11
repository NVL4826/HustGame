package hust.adventure.entities.components;

import hust.adventure.entities.EntityManager;
import hust.adventure.entities.Player;
import hust.adventure.entities.enemies.BaseEnemy;

import hust.adventure.collision.CollisionManager;
import com.badlogic.gdx.utils.Pool;

public class BouncingBehavior implements AIBehavior, Pool.Poolable {
    private float vx, vy;
    private float worldWidth, worldHeight;

    public BouncingBehavior() {
        this.vx = 0f;
        this.vy = 0f;
        this.worldWidth = 0f;
        this.worldHeight = 0f;
    }

    public BouncingBehavior(float vx, float vy, float worldWidth, float worldHeight) {
        this.vx = vx;
        this.vy = vy;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void init(float vx, float vy, float worldWidth, float worldHeight) {
        this.vx = vx;
        this.vy = vy;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    @Override
    public void execute(BaseEnemy enemy, float delta, Player player, EntityManager entityManager) {
        CollisionManager cm = enemy.getCollisionManager();
        float currentMapW = (cm != null) ? cm.getMapWidth() : worldWidth;
        float currentMapH = (cm != null) ? cm.getMapHeight() : worldHeight;

        enemy.setX(enemy.getX() + vx * delta);
        enemy.setY(enemy.getY() + vy * delta);

        if (cm == null || !cm.isInfinite()) {
            if (enemy.getX() < 0) {
                enemy.setX(0);
                vx = -vx;
            } else if (enemy.getX() > currentMapW - enemy.getWidth()) {
                enemy.setX(currentMapW - enemy.getWidth());
                vx = -vx;
            }

            if (enemy.getY() < 0) {
                enemy.setY(0);
                vy = -vy;
            } else if (enemy.getY() > currentMapH - enemy.getHeight()) {
                enemy.setY(currentMapH - enemy.getHeight());
                vy = -vy;
            }
        }
    }

    @Override
    public void reset() {
        this.vx = 0f;
        this.vy = 0f;
        this.worldWidth = 0f;
        this.worldHeight = 0f;
    }
}
