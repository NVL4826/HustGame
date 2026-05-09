package hust.adventure.entities.components;

import hust.adventure.entities.EntityManager;
import hust.adventure.entities.Player;
import hust.adventure.entities.enemies.BaseEnemy;

public class ChaseBehavior implements AIBehavior {
    private float speed;

    public ChaseBehavior(float speed) {
        this.speed = speed;
    }

    @Override
    public void execute(BaseEnemy enemy, float delta, Player player, EntityManager entityManager) {
        float dx = player.getX() - enemy.getX();
        float dy = player.getY() - enemy.getY();
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist > 0) {
            enemy.setX(enemy.getX() + (dx / dist) * speed * delta);
            enemy.setY(enemy.getY() + (dy / dist) * speed * delta);
        }
    }
}
