package hust.adventure.entities.components;

import hust.adventure.entities.EntityManager;
import hust.adventure.entities.Player;
import hust.adventure.entities.enemies.BaseEnemy;

public class BouncingBehavior implements AIBehavior {
    private float vx, vy;
    private float worldWidth, worldHeight;

    public BouncingBehavior(float vx, float vy, float worldWidth, float worldHeight) {
        this.vx = vx;
        this.vy = vy;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    @Override
    public void execute(BaseEnemy enemy, float delta, Player player, EntityManager entityManager) {
        enemy.setX(enemy.getX() + vx * delta);
        enemy.setY(enemy.getY() + vy * delta);

        if (enemy.getX() < 0) {
            enemy.setX(0);
            vx = -vx;
        } else if (enemy.getX() > worldWidth - enemy.getWidth()) {
            enemy.setX(worldWidth - enemy.getWidth());
            vx = -vx;
        }

        if (enemy.getY() < 0) {
            enemy.setY(0);
            vy = -vy;
        } else if (enemy.getY() > worldHeight - enemy.getHeight()) {
            enemy.setY(worldHeight - enemy.getHeight());
            vy = -vy;
        }
    }
}
