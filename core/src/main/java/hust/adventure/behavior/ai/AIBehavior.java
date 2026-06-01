package hust.adventure.behavior.ai;

import hust.adventure.entities.EntityManager;
import hust.adventure.entities.enemies.Enemy;
import hust.adventure.entities.player.Player;

/**
 * Interface for AI strategies (Strategy Pattern).
 */
public interface AIBehavior {
    void execute(Enemy enemy, float delta, Player player, EntityManager entityManager);
}
