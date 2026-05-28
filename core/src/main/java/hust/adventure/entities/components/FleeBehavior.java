package hust.adventure.entities.components;
 
import hust.adventure.entities.EntityManager;
import hust.adventure.entities.Player;
import hust.adventure.entities.enemies.BaseEnemy;
import hust.adventure.collision.CollisionManager;
 
public class FleeBehavior implements AIBehavior {
    private final float speed;
    private final float safeDistance;
 
    public FleeBehavior(float speed, float safeDistance) {
        this.speed = speed;
        this.safeDistance = safeDistance;
    }
 
    @Override
    public void execute(BaseEnemy enemy, float delta, Player player, EntityManager entityManager) {
        float dx = player.getX() - enemy.getX();
        float dy = player.getY() - enemy.getY();
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
 
        if (dist < safeDistance && dist > 0) {
            float nextX = enemy.getX() - (dx / dist) * speed * delta;
            float nextY = enemy.getY() - (dy / dist) * speed * delta;
            enemy.setX(nextX);
            enemy.setY(nextY);
        }
    }
}
